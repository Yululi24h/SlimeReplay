package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerUpdateLight;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.utils.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class ReplayChunk {
    private final ReplayUser user;
    private final ReplayChunkData chunkData;
    private final List<ReplayEntity> currentChunkEntities = new ArrayList<>();

    private boolean loaded;

    public ReplayChunk(ReplayUser user, ReplayChunkData chunkData) {
        this.user = user;
        this.chunkData = chunkData;
    }

    public ReplayChunkData getChunkData() {
        return chunkData;
    }

    public void addEntity(ReplayEntity replayEntity) {
        this.currentChunkEntities.add(replayEntity);
        // replayEntity.setReplayChunk(this);
    }

    public void removeEntity(ReplayEntity replayEntity) {
        if (this.currentChunkEntities.remove(replayEntity)) {
            replayEntity.setReplayChunk(null);
        }
    }

    public void unloadEntities() {
        for (ReplayEntity replayEntity : this.currentChunkEntities) {
            replayEntity.unload(ReplayEntity.UnloadReason.CHUNK);
        }
        this.currentChunkEntities.clear();
    }

    public int getX() {
        return chunkData.getX();
    }

    public int getZ() {
        return chunkData.getZ();
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos(getX(), getZ());
    }

    public LightData getLightData() {
        return chunkData.getLightData();
    }

    public void setLightData(LightData lightData) {
        chunkData.setLightData(lightData);
    }

    public BaseChunk[] getBaseChunk() {
        return chunkData.getColumn().getChunks();
    }

    public ServerVersion getServerVersion() {
        return chunkData.getServerVersion();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    protected List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packetWrappers = chunkData.getPackets(); //TODO: Send Entities Packet.
        if (chunkData.getLightData() != null && chunkData.getServerVersion().isOlderThan(ServerVersion.V_1_18)) {
            packetWrappers.add(new WrapperPlayServerUpdateLight(getX(), getZ(), getLightData()));
        }
        for (ReplayEntity replayEntity : currentChunkEntities) {
            replayEntity.send();
        }
        return packetWrappers;
    }

    public void load() {
        if (this.loaded)
            return;
        this.user.sendSilent(getPackets());
        this.loaded = true;
    }

    public void unload() {
        if (!this.loaded)
            return;
        this.user.sendSilent(new WrapperPlayServerUnloadChunk(getX(), getZ()));
        this.user.sendSilent(new WrapperPlayServerDestroyEntities(collectEntityIds()));
        this.loaded = false;
    }

    private int[] collectEntityIds() {
        return this.currentChunkEntities.stream()
                .mapToInt(ReplayEntity::getEntityId)
                .toArray();
    }
}