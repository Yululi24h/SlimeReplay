package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayUpdateMultipleBlock;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

import java.util.List;

public class ReplayUpdateMultipleBlockChain extends ReplayChainImpl<ReplayUpdateMultipleBlock> {
    private ReplayRunnerHandler.BlockChangesData changesData;

    public ReplayUpdateMultipleBlockChain(ReplayUpdateMultipleBlock packet, long millis, ReplayChain back) {
        super(packet, millis, back);
    }

    @Override
    public ReplayChainType getType() {
        return ReplayChainType.BLOCK;
    }

    @Override
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        this.changesData = handler.setBlocks(this.packet);
        return null;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        handler.setBlocks(new ReplayUpdateMultipleBlock(
                this.packet.getChunkPosition(),
                this.packet.getTrustEdges(),
                this.changesData.getBlocks()
        ));
        return null;
    }
}