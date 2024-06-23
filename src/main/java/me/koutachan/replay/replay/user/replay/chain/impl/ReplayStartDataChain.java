package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.ReplayStartData;
import me.koutachan.replay.replay.packet.in.ReplayUpdateLightData;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

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
        handler.onSpawn(this.packet.getDimension(), this.packet.getLocation(), GameMode.CREATIVE);
        for (ReplayChunkData chunkData : this.packet.getChunkData()) {
            handler.handleChunk(chunkData);
        }
        if (this.packet.getLightData() != null) {
            for (ReplayUpdateLightData lightData : this.packet.getLightData()) {
                handler.getChunk(lightData.getChunkPos()).setLightData(lightData.getLightData());
            }
        }
        return null;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        return null; //Cannot invertible
    }
}
