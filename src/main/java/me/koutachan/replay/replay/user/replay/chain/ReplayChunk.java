package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerUpdateLight;
import me.koutachan.replay.replay.user.map.ChunkCache;

import java.util.ArrayList;
import java.util.List;

public class ReplayChunk {
    private final ReplayChunkData chunkData;
    private final List<ReplayEntity> currentChunkEntities = new ArrayList<>();

    public ReplayChunk(ReplayChunkData chunkData) {
        this.chunkData = chunkData;
    }

    public ReplayChunkData getChunkData() {
        return chunkData;
    }

    public void addEntity(ReplayEntity replayEntity) {
        this.currentChunkEntities.add(replayEntity);
        replayEntity.setReplayChunk(this);
    }

    public void removeEntity(ReplayEntity replayEntity) {
        if (this.currentChunkEntities.remove(replayEntity)) {
            replayEntity.setReplayChunk(null);
        }
    }

    public void unloadEntities() {
        for (ReplayEntity replayEntity : this.currentChunkEntities) {
            replayEntity.unload();
        }
        this.currentChunkEntities.clear();
    }

    public int getX() {
        return chunkData.getX();
    }

    public int getZ() {
        return chunkData.getZ();
    }

    public ChunkCache.ChunkPos getChunkPos() {
        return new ChunkCache.ChunkPos(getX(), getZ());
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

    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packetWrappers = chunkData.getPackets(); //TODO: Send Entities Packet.
        if (chunkData.getLightData() != null && chunkData.getServerVersion().isOlderThan(ServerVersion.V_1_18)) {
            packetWrappers.add(new WrapperPlayServerUpdateLight(getX(), getZ(), getLightData()));
        }
        return packetWrappers;
    }

    public void unload() {


    }
}