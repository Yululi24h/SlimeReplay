package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainType;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

import java.util.List;

public class ReplayChainNonEffect implements ReplayChain {
    protected final ReplayWrapper<?> packet;
    private ReplayChain next;
    private final long millis;
    private final ReplayChain back;

    public ReplayChainNonEffect(ReplayWrapper<?> packet, long millis, ReplayChain back) {
        this.packet = packet;
        this.millis = millis;
        this.back = back;
    }

    @Override
    public ReplayChain next() {
        return next;
    }

    @Override
    public void setNext(ReplayChain chain) {
        this.next = chain;
    }

    @Override
    public ReplayChain back() {
        return back;
    }

    @Override
    public long getMillis() {
        return millis;
    }

    @Override
    public ReplayChainType getType() {
        return ReplayChainType.UNKNOWN;
    }

    @Override
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        return packet.getPackets();
    }

    @Override
    public List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler) {
        return null;
    }
}
