package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.utils.ChunkPos;

import java.util.List;

public class ReplayEntity {
    private final ReplayRunnerHandler handler;
    private final ReplayUser replayUser;

    private final ReplayEntityAbstract spawnPacket; //TODO:
    private final int entityId;
    private ReplayChunk replayChunk;
    private Location currentPos;

    private boolean loaded;

    public ReplayEntity(ReplayRunnerHandler handler, ReplayEntityAbstract replayWrapper) {
        this(handler, replayWrapper, replayWrapper.getLocation());
    }

    public ReplayEntity(ReplayRunnerHandler handler, ReplayEntityAbstract replayWrapper, Location currentPos) {
        this.handler = handler;
        this.replayUser = handler.getUser();
        this.spawnPacket = replayWrapper;
        this.entityId = replayWrapper.getEntityId();
        this.spawnPacket.setEntityId(getIncrementedEntityId(this.entityId));
        move(currentPos);
        handler.getUser().sendMessage("Moving chunk");
    }

    public void send() {
        handler.getUser().sendMessage("Trying called" + (this.replayChunk == null));
        if (this.loaded || this.replayChunk == null)
            return;
        this.spawnPacket.setLocation(this.currentPos);
        handler.getUser().sendMessage("Send: " + this.spawnPacket.getClass().getName());
        this.replayUser.sendSilent(this.spawnPacket.getPackets());
        this.loaded = true;
    }

    public void unload(UnloadReason reason) {
        if (!this.loaded)
            return;
        this.replayUser.sendSilent(new WrapperPlayServerDestroyEntities(getEntityId()));
        this.loaded = false;
    }

    public void remove() {
        unload(UnloadReason.REMOVE);
        if (this.replayChunk != null) {
            this.replayChunk.removeEntity(this);
        }
    }

    public void setEntityMeta(List<EntityData> entityData) {
        this.spawnPacket.setEntityMeta(entityData);
        if (this.loaded) {
            this.replayUser.sendSilent(new WrapperPlayServerEntityMetadata(getEntityId(), entityData));
        }
    }

    public void move(Location location) {
        if (this.currentPos == null || !this.currentPos.getPosition().equals(location.getPosition())) {
            final ChunkPos pos = this.handler.getChunkPos(location);
            if (this.replayChunk == null) {
                this.replayChunk = this.handler.getChunk(pos);
            } else {
                if (!this.replayChunk.getChunkPos().equals(pos)) {
                    this.replayChunk.removeEntity(this);
                    this.replayChunk = this.handler.getChunk(pos);
                } else {
                    return; // This entity is not moving chunk, so no processing is required // Todo: update current pos.
                }
            }
            this.currentPos = location;
            if (this.replayChunk == null) {
                unload(UnloadReason.CHUNK);
                return;
            }
            this.replayChunk.addEntity(this);
            if (replayChunk.isLoaded()) {
                send();
            } else {
                unload(UnloadReason.CHUNK);
            }
        }
    }

    public void setCurrentPos(Location currentPos) {
        this.currentPos = currentPos;
    }

    public ReplayChunk getReplayChunk() {
        return replayChunk;
    }

    public void setReplayChunk(ReplayChunk replayChunk) {
        this.replayChunk = replayChunk;
    }

    public int getRealEntityId() {
        return this.entityId;
    }

    public int getEntityId() {
        return this.spawnPacket.getEntityId();
    }

    public int getIncrementedEntityId(int entityId) {
        return this.replayUser.getEntityId() >= entityId ? entityId + 1 : entityId;
    }

    public enum UnloadReason {
        CHUNK,
        REMOVE
    }
}