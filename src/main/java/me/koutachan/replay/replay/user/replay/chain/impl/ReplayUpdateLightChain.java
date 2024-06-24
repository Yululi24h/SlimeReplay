package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.ReplayUpdateLightData;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerUpdateLight;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;
import me.koutachan.replay.utils.LightDataUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ReplayUpdateLightChain extends ReplayChainImpl<ReplayUpdateLightData> {
    public static BitSet EMPTY = new BitSet();
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
            LightData packetLight = this.packet.getLightData();
            if (lightData == null) {
                chunkData.setLightData(packetLight);
                return handler.hasSentChunk(this.packet.getX(), this.packet.getZ()) ? super.send(handler) : null;
            }
            this.lightData = lightData.clone();
            LightDataUtils.appendLightData(lightData, packetLight);
        }
        return handler.hasSentChunk(this.packet.getX(), this.packet.getZ()) ? super.send(handler) : null;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        ReplayChunkData chunkData = handler.getChunk(this.packet.getChunkPos());
        if (chunkData != null && this.lightData != null) {
            chunkData.setLightData(this.lightData);
            if (handler.hasSentChunk(chunkData.getX(), chunkData.getZ())) {
                List<PacketWrapper<?>> packets = new ArrayList<>();
                packets.add(new WrapperPlayServerUpdateLight(this.packet.getX(), this.packet.getZ(), this.lightData));
                return packets;
            }
        }
        return null;
    }
}