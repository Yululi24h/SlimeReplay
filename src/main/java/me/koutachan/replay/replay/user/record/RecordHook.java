package me.koutachan.replay.replay.user.record;

import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

public interface RecordHook {
    ReplayPacketContainer onSave();

    void onPacket(ReplayWrapper<?> packet);

    void start();

    ReplayUser getUser();
}