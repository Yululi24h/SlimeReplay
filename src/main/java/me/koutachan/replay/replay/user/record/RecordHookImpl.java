package me.koutachan.replay.replay.user.record;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.packet.in.ReplayStartData;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.ArrayList;

public class RecordHookImpl implements RecordHook {
    private final ReplayUser user;
    private final ReplayPacketContainer container = new ReplayPacketContainer();

    private long startMillis;

    public RecordHookImpl(ReplayUser user) {
        this.user = user;
        this.container.addPacket(
                ReplayPacket.of(new ReplayStartData(
                        this.user.getChunk().toPacket(),
                        new ArrayList<>(),
                        this.user.getDimension(),
                        SpigotConversionUtil.fromBukkitLocation(this.user.getPlayer().getLocation())
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
        return null;
    }
}