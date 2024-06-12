package me.koutachan.replay.replay.user.replay.chain.impl;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

import java.util.List;

public abstract class ReplayChainImpl<T extends ReplayWrapper<T>> implements ReplayChain {
    protected final T packet;
    private ReplayChain next;
    private final long millis;
    private final ReplayChain back;

    public ReplayChainImpl(T packet, long millis, ReplayChain back) {
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
    public List<PacketWrapper<?>> send(ReplayRunnerHandler handler) {
        return packet.getPackets();
    }
}