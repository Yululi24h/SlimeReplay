package me.koutachan.replay.replay.user.record;

import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.user.ReplayUser;

public class RecordHookImpl implements RecordHook {
    private final ReplayUser user;
    private final ReplayPacketContainer container = new ReplayPacketContainer();

    private long startMillis;

    public RecordHookImpl(ReplayUser user) {
        this.user = user;
        this.container.addPacket(user.getWorld().toPacket(), 0);
        this.container.addPacket(user.getChunk().toPacket(), 0);
        this.container.addPacket(user.getEntities().toPacket(), 0);
    }

    @Override
    public ReplayPacketContainer onSave() {
        //ReplayPacketContainer container = this.container;
        //this.container = new ReplayPacketContainer(); // Just in
        return container;
    }

    @Override
    public void onPacket(ReplayPacket packet) {
        //this.container.addPacket();
        this.container.addPacket(packet, System.currentTimeMillis() - startMillis);
    }

    @Override
    public void start() {
        startMillis = System.currentTimeMillis();
    }

    @Override
    public ReplayUser getUser() {
        return null;
    }
}