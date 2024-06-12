package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.List;

/**
 * The {@code ReplayChain} interface represents a chain of replay actions
 * that can be navigated backward. This interface provides methods
 * to move to the next action in the chain.
 */
public interface ReplayChain {
    ReplayChain next();

    void setNext(ReplayChain chain);

    ReplayChain back();

    long getMillis();

    ReplayChainType getType();

    List<PacketWrapper<?>> send(ReplayRunnerHandler handler);

    //** TODO*
    List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler);
}