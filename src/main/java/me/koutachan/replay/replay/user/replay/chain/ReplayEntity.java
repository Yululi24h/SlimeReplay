package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.ChunkCache;

import java.util.List;

public class ReplayEntity {
    private final ReplayUser replayUser;
    private final ReplayEntityAbstract spawnPacket; //TODO:
    private ReplayChunk replayChunk;
    private Location currentPos;

    private boolean loaded;

    public ReplayEntity(ReplayUser replayUser, ReplayEntityAbstract replayWrapper) {
        this(replayUser, replayWrapper, replayWrapper.getLocation());
    }

    public ReplayEntity(ReplayUser replayUser, ReplayEntityAbstract replayWrapper, Location currentPos) {
        this.replayUser = replayUser;
        this.spawnPacket = replayWrapper;

        this.currentPos = currentPos;
    }

    public void send() {
        if (this.loaded)
            return;
        this.spawnPacket.setLocation(this.currentPos);
        this.replayUser.sendSilent(this.spawnPacket.getPackets());
        this.loaded = true;
    }

    public void unload(UnloadReason reason) {
        if (!this.loaded)
            return;
        this.replayUser.sendSilent(new WrapperPlayServerDestroyEntities(getEntityId()));
    }

    public void remove() {
        unload(UnloadReason.REMOVE);
        if (this.replayChunk != null) {
            this.replayChunk.removeEntity(this);
        }
    }

    public void setEntityMeta(ReplayRunnerHandler handler, List<EntityData> entityData) {
        this.spawnPacket.setEntityMeta(entityData);
        if (this.replayChunk != null && handler.hasSentChunk(this.replayChunk.getX(), this.replayChunk.getZ())) {
            this.replayUser.sendSilent(new WrapperPlayServerEntityMetadata(getEntityId(), entityData));
        }
    }

    public void move(ReplayRunnerHandler handler, Location location) {
        final ChunkCache.ChunkPos pos = handler.getChunkPos(location);
        if (this.replayChunk == null) {
            this.replayChunk = handler.getChunk(pos);
        } else {
            if (!this.replayChunk.getChunkPos().equals(pos)) {
                this.replayChunk.removeEntity(this);
                this.replayChunk = handler.getChunk(pos);
            } else {
                return; // This entity is not moving chunk, so no processing is required
            }
        }
        if (this.replayChunk != null) {
            this.replayChunk.addEntity(this);

        } else {
            unload(UnloadReason.CHUNK);
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
        return this.spawnPacket.getEntityId();
    }

    public int getEntityId() {
        return getIncrementedEntityId(this.spawnPacket.getEntityId());
    }

    public int getIncrementedEntityId(int entityId) {
        return this.replayUser.getEntityId() >= entityId ? entityId + 1 : entityId;
    }

    public enum UnloadReason {
        CHUNK,
        REMOVE
    }
}