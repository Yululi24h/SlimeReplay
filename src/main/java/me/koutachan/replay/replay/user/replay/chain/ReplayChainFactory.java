package me.koutachan.replay.replay.user.replay.chain;

import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.user.replay.chain.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReplayChainFactory {
    public ReplayChain firstChain;
    public ReplayChain currentChain;

    private static final Map<Class<? extends ReplayWrapper<?>>, Class<? extends ReplayChain>> TO_REPLAY_CHAIN = new HashMap<>();
    static {
        TO_REPLAY_CHAIN.put(ReplayStartData.class, ReplayStartDataChain.class);
        TO_REPLAY_CHAIN.put(ReplayChunkData.class, ReplayChunkChain.class);
        TO_REPLAY_CHAIN.put(ReplayUpdateBlock.class, ReplayUpdateBlockChain.class);
        TO_REPLAY_CHAIN.put(ReplayUpdateMultipleBlock.class, ReplayUpdateMultipleBlockChain.class);
        TO_REPLAY_CHAIN.put(ReplayUpdateLightData.class, ReplayUpdateLightChain.class);
    }

    public ReplayChainFactory() {

    }

    private ReplayChainFactory(ReplayChain chain) {
        this.firstChain = this.currentChain = chain;
    }

    public void appendChain(Function<ReplayChain, ReplayChain> replayFunc) {
        nextChain(replayFunc.apply(this.currentChain));
    }

    public void appendChain(ReplayPacket packet) {
        ReplayWrapper<?> replayWrapper = packet.getReplayWrapper();
        Class<? extends ReplayChain> chainClass = TO_REPLAY_CHAIN.get(replayWrapper.getClass());
        if (chainClass != null) {
            try {
                nextChain(chainClass.getConstructor(replayWrapper.getClass(), long.class, ReplayChain.class).newInstance(replayWrapper, packet.getMillis(), this.currentChain));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void nextChain(ReplayChain nextChain) {
        if (this.currentChain != null) {
            this.currentChain.setNext(nextChain);
        } else {
            this.firstChain = nextChain;
        }
        this.currentChain = nextChain;
    }

    public ReplayChain getFirstChain() {
        return firstChain;
    }

    public static ReplayChainFactory createFactory(ReplayChain firstChain) {
        return new ReplayChainFactory(firstChain);
    }
}