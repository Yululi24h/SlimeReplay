package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayUnloadChunkData;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayChunk;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

import java.util.List;

public class ReplayUnloadChunkChain extends ReplayChainImpl<ReplayUnloadChunkData> {
    private ReplayChunk chunk;

    public ReplayUnloadChunkChain(ReplayUnloadChunkData packet, long millis, ReplayChain back) {
        super(packet, millis, back);
    }

    @Override
    public ReplayChainType getType() {
        return ReplayChainType.CHUNK;
    }

    @Override
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        this.chunk = handler.getChunk(this.packet.getChunkPos());
        if (this.chunk != null) {
            handler.removeChunk(this.chunk.getX(), this.chunk.getZ());
        }
        return null;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        if (this.chunk != null) {
            handler.handleChunk(this.chunk);
        }
        return null;
    }
}