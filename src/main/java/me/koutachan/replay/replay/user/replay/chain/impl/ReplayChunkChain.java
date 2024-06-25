package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayChunk;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

import java.util.List;

public class ReplayChunkChain extends ReplayChainImpl<ReplayChunkData> {
    private ReplayChunk oldChunk;

    public ReplayChunkChain(ReplayChunkData packet, long millis, ReplayChain back) {
        super(packet, millis, back);
    }

    @Override
    public ReplayChainType getType() {
        return ReplayChainType.CHUNK;
    }

    @Override
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        this.oldChunk = handler.getChunk(this.packet.toChunkPos());
        handler.handleChunk(this.packet);
        return null;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        if (this.oldChunk != null) {
            handler.handleChunk(this.oldChunk);
        } else if (handler.hasChunk(this.packet)) {
            return this.packet.getInvertedPackets();
        }
        return null;
    }
}