package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.packet.in.ReplayStartData;
import me.koutachan.replay.replay.packet.in.ReplayUpdateLightData;
import me.koutachan.replay.replay.user.replay.chain.*;
import org.bukkit.Bukkit;

import java.util.List;

public class ReplayStartDataChain extends ReplayChainImpl<ReplayStartData> {
    public ReplayStartDataChain(ReplayStartData packet, long millis, ReplayChain back) {
        super(packet, millis, back);
    }

    @Override
    public ReplayChainType getType() {
        return ReplayChainType.CHUNK;
    }

    @Override
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        handler.onSpawn(this.packet.getDimension(), this.packet.getLocation(), GameMode.SPECTATOR);
        for (ReplayChunkData chunkData : this.packet.getChunkData()) {
            handler.handleChunk(chunkData);
        }
        if (this.packet.getLightData() != null) {
            for (ReplayUpdateLightData lightData : this.packet.getLightData()) {
                ReplayChunk chunk = handler.getChunk(lightData.getChunkPos());
                if (chunk == null) {
                    Bukkit.getLogger().warning("[SlimeReplay] Chunk was null. (x=" + lightData.getX() + " z=" + lightData.getZ() + ")");
                    continue;
                }
                chunk.setLightData(lightData.getLightData());
            }
        }
        for (ReplayEntityAbstract replayEntity : this.packet.getEntityData()) {
            handler.handleEntity(replayEntity);
        }

        return null;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        throw new IllegalStateException();
    }
}