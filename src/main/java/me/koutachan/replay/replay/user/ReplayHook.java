package me.koutachan.replay.replay.user;

import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.ReplayPacketContainer;

public interface ReplayHook {
    ReplayPacketContainer onTick();

    void onPacket(ReplayPacket packet);

    ReplayUser getUser();

    boolean isRecording();
}