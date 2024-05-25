package me.koutachan.replay.replay.user.impl;

import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.ReplayPacketContainer;
import me.koutachan.replay.replay.user.ReplayHook;
import me.koutachan.replay.replay.user.ReplayUser;

public class ReplayHookImpl implements ReplayHook {
    private ReplayHook hook;

    @Override
    public ReplayPacketContainer onTick() {
        return null;
    }

    @Override
    public void onPacket(ReplayPacket packet) {

    }

    @Override
    public ReplayUser getUser() {
        return null;
    }

    @Override
    public boolean isRecording() {
        return false;
    }
}