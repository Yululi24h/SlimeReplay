package me.koutachan.replay.utils;

import com.google.common.cache.CacheBuilder;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.ReplayUpdateLightData;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.user.map.ChunkCache;
import me.koutachan.replay.replay.user.replay.chain.ReplayChunk;

import java.util.Map;

public class LightDataQueue {
    private final Map<ChunkCache.ChunkPos, LightData> lightQueue = CacheBuilder.newBuilder() // This is not the best way to implement this, but it is better than creating Minecraft's light engine.
            .maximumSize(30)
            .<ChunkCache.ChunkPos, LightData>build()
            .asMap();

    public ReplayChunk newChunk(ReplayChunk chunk) {
        newChunk(chunk.getChunkData());
        return chunk;
    }

    public ReplayChunkData newChunk(ReplayChunkData chunk) {
        LightData lightData = this.lightQueue.remove(chunk.getChunkPos());
        if (lightData != null) {
            chunk.setLightData(lightData);
        }
        return chunk;
    }

    public void newLight(ReplayUpdateLightData lightData) {
        this.lightQueue.put(lightData.getChunkPos(), lightData.getLightData());
    }

    public Map<ChunkCache.ChunkPos, LightData> getLightQueue() {
        return lightQueue;
    }
}