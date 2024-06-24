package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.protocol.world.Location;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.ReplayUser;

public class ReplayEntity {
    private final ReplayUser replayUser;
    private final ReplayEntityAbstract spawnPacket; //TODO:
    private ReplayChunk replayChunk;
    private Location currentPos;

    public ReplayEntity(ReplayUser replayUser, ReplayEntityAbstract replayWrapper) {
        this(replayUser, replayWrapper, replayWrapper.getLocation());
    }

    public ReplayEntity(ReplayUser replayUser, ReplayEntityAbstract replayWrapper, Location currentPos) {
        this.replayUser = replayUser;
        this.spawnPacket = replayWrapper;
        this.currentPos = currentPos;
    }

    public void send() {
        this.spawnPacket.setLocation(this.currentPos);
        this.replayUser.sendSilent(this.spawnPacket.getPackets());
    }

    public void unload() {

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

    public int getEntityId() {
        return cacheUpEntityId(this.replayUser.getEntityId());
    }

    public int cacheUpEntityId(int entityId) {
        return this.replayUser.getEntityId() >= entityId ? entityId + 1 : entityId;
    }

    public enum UnloadReason {
        CHUNK,
        REMOVE
    }
}