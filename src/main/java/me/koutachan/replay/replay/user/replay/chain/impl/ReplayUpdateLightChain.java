package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayUpdateLightData;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerUpdateLight;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayChunk;
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
        ReplayChunk chunk = handler.getChunk(this.packet.getChunkPos());
        if (chunk != null) {
            LightData lightData = chunk.getLightData();
            LightData packetLight = this.packet.getLightData();
            if (lightData == null) {
                chunk.setLightData(packetLight);
                return handler.hasSentChunk(this.packet.getX(), this.packet.getZ()) ? super.send(handler) : null;
            }
            this.lightData = lightData.clone();
            LightDataUtils.appendLightData(lightData, packetLight);
            return handler.hasSentChunk(this.packet.getX(), this.packet.getZ()) ? super.send(handler) : null;
        } else {
            handler.handleLightQueue(this.packet);
        }
        return null;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        ReplayChunk chunk = handler.getChunk(this.packet.getChunkPos());
        if (chunk != null && this.lightData != null) {
            chunk.setLightData(this.lightData);
            if (handler.hasSentChunk(chunk.getX(), chunk.getZ())) {
                List<PacketWrapper<?>> packets = new ArrayList<>();
                packets.add(new WrapperPlayServerUpdateLight(this.packet.getX(), this.packet.getZ(), this.lightData));
                return packets;
            }
        }
        this.lightData = null;
        return null;
    }
}