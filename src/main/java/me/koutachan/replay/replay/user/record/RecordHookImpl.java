package me.koutachan.replay.replay.user.record;

import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.packet.in.ReplayStartData;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

public class RecordHookImpl implements RecordHook {
    private final ReplayUser user;
    private final ReplayPacketContainer container = new ReplayPacketContainer();

    private long startMillis;

    public RecordHookImpl(ReplayUser user) {
        this.user = user;
        this.container.addPacket(
                ReplayPacket.of(new ReplayStartData(
                        this.user.getChunk().toPacket(),
                        this.user.getEntities().toPacket(),
                        this.user.getDimension(),
                        this.user.getEntities().getSelf().getLocation()
                ), 0L)
        );
    }

    @Override
    public ReplayPacketContainer getContainer() {
        return container;
    }

    @Override
    public void onPacket(ReplayWrapper<?> packet) {
        this.container.addPacket(ReplayPacket.of(packet, System.currentTimeMillis() - this.startMillis));
    }

    @Override
    public void start() {
        this.startMillis = System.currentTimeMillis();
    }

    @Override
    public ReplayUser getUser() {
        return user;
    }
}