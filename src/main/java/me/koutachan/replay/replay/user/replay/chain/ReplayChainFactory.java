package me.koutachan.replay.replay.user.replay.chain;

import me.koutachan.replay.replay.packet.in.ReplayWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReplayChainFactory {
    public ReplayChain firstChain;
    public ReplayChain currentChain;

    private static final Map<Class<ReplayWrapper<?>>, Class<ReplayChain>> TO_REPLAY_CHAIN = new HashMap<>();
    static {


    }

    private ReplayChainFactory(ReplayChain chain) {
        this.firstChain = this.currentChain = chain;
    }

    public void appendChain(Function<ReplayChain, ReplayChain> replayFunc) {
        ReplayChain nextChain = replayFunc.apply(this.currentChain);
        this.currentChain.setNext(nextChain);
        this.currentChain = nextChain;
    }

    public void appendChain(ReplayWrapper<?> replayWrapper) {

    }

    public ReplayChain getFirstChain() {
        return firstChain;
    }

    public static ReplayChainFactory createFactory(ReplayChain firstChain) {
        return new ReplayChainFactory(firstChain);
    }
}