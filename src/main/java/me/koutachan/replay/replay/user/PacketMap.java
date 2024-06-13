package me.koutachan.replay.replay.user;

import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class PacketMap<T extends ReplayPacket> {
    private final Map<Object, T> packets = new HashMap<>();

    public abstract void onPacket(ReplayWrapper<?> packet);

    public Collection<T> toPacket() {
        return packets.values();
    }

    public void register(Object id, T packet) {
        packets.put(id, packet);
    }

    public void unregister(Object id) {
        packets.remove(id);
    }

    public void unregister(Object... ids) {
        for (Object id : ids) {
            unregister(id);
        }
    }


}