package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

import java.util.List;

public class ReplaySpawnEntityChain extends ReplayChainImpl<ReplayEntityAbstract> {
    public ReplaySpawnEntityChain(ReplayEntityAbstract packet, long millis, ReplayChain back) {
        super(packet, millis, back);
    }

    @Override
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        return null;
    }

    @Override
    public ReplayChainType getType() {
        return ReplayChainType.ENTITY_SPAWN;
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        return null;
    }
}
