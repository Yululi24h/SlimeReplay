package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayUpdateBlock;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

import java.util.List;

public class ReplayUpdateBlockChain extends ReplayChainImpl<ReplayUpdateBlock> {
    private WrappedBlockState state;

    public ReplayUpdateBlockChain(ReplayUpdateBlock packet, long millis, ReplayChain back) {
        super(packet, millis, back);
    }

    @Override
    public ReplayChainType getType() {
        return ReplayChainType.BLOCK;
    }

    @Override
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        this.state = handler.setBlock(this.packet);
        return null;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        if (this.state != null) {
            handler.setBlock(this.packet.getBlockPos(), this.state.getGlobalId());
        }
        return null;
    }
}