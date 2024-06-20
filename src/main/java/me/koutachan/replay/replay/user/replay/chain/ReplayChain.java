package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.ReplayPacketContainer;

import java.util.List;

/**
 * The {@code ReplayChain} interface represents a chain of replay actions
 * that can be navigated backward. This interface provides methods
 * to move to the next action in the chain.
 */
public interface ReplayChain {
    ReplayChain next();

    default boolean hasNext() {
        return next() != null;
    }

    void setNext(ReplayChain chain);

    ReplayChain back();

    long getMillis();

    ReplayChainType getType();

    List<PacketWrapper<?>> send(ReplayRunnerHandler handler);

    //** TODO*
    List<PacketWrapper<?>> inverted(ReplayRunnerHandler handler);

    static ReplayChainFactory toContainer(ReplayPacketContainer container) {
        ReplayChainFactory factory = new ReplayChainFactory();
        for (ReplayPacket packet : container.getPackets()) {
            factory.appendChain(packet);
        }
        return factory;
    }

    static ReplayChain fromContainer(ReplayPacketContainer container) {
        return toContainer(container).getFirstChain();
    }
}