package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.ReplayUpdateLightData;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerUpdateLight;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

import java.util.ArrayList;
import java.util.List;

public class ReplayUpdateLightChain extends ReplayChainImpl<ReplayUpdateLightData> {
    private LightData lightData;

    public ReplayUpdateLightChain(ReplayUpdateLightData packet, long millis, ReplayChain back) {
        super(packet, millis, back);
    }

    @Override
    public ReplayChainType getType() {
        return ReplayChainType.CHUNK;
    }

    @Override
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        ReplayChunkData chunkData = handler.getChunk(this.packet.getChunkPos());
        if (chunkData != null) {
            LightData lightData = chunkData.getLightData();
            this.lightData = lightData.clone();
            for (int i = 0; i < this.packet.getLightData().getSkyLightCount(); i++) {
                if (this.packet.getLightData().getSkyLightMask().get(i)) {
                    lightData.getSkyLightArray()[i] = this.packet.getLightData().getSkyLightArray()[i];
                }
            }
            for (int i = 0; i < this.packet.getLightData().getBlockLightCount(); i++) {
                if (this.packet.getLightData().getBlockLightMask().get(i)) {
                    lightData.getBlockLightArray()[i] = this.packet.getLightData().getBlockLightArray()[i];
                }
            }
        }
        return super.send(handler);
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        ReplayChunkData chunkData = handler.getChunk(this.packet.getChunkPos());
        if (chunkData != null) {
            chunkData.setLightData(this.lightData);
        }
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerUpdateLight(this.packet.getX(), this.packet.getZ(), this.lightData));
        return packets;
    }
}
