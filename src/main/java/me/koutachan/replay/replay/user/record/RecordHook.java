package me.koutachan.replay.replay.user.record;

import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.user.ReplayUser;

public interface RecordHook {
    ReplayPacketContainer onSave();

    void onPacket(ReplayPacket packet);

    void start();

    ReplayUser getUser();
}