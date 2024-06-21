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
                this.lightData = new LightData(EMPTY, EMPTY, EMPTY, EMPTY);
                chunkData.setLightData(packetLight);
                return super.send(handler);
            }
            this.lightData = lightData.clone();
            for (int i = 0; i < packetLight.getSkyLightCount(); i++) {
                if (mergeLightMask(i, packetLight.getSkyLightMask(), lightData.getSkyLightMask(), lightData.getEmptySkyLightMask())) {
                    lightData.getSkyLightArray()[i] = packetLight.getSkyLightArray()[i];
                }
            }
            for (int i = 0; i < packetLight.getBlockLightCount(); i++) {
                if (mergeLightMask(i, packetLight.getBlockLightMask(), lightData.getBlockLightMask(), lightData.getEmptyBlockLightMask())) {
                    lightData.getBlockLightArray()[i] = packetLight.getBlockLightArray()[i];
                }
            }
        }
        return super.send(handler);
    }

    public static boolean mergeLightMask(int pos, BitSet fromLight, BitSet empty, BitSet light) {
        boolean hasLight = fromLight.get(pos);
        if (hasLight) {
            light.set(pos, true);
            empty.set(pos, false);
        } else {
            empty.set(pos, true);
            light.set(pos, false);
        }
        return hasLight;
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