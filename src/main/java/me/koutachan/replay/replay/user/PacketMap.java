package me.koutachan.replay.replay.user;

import me.koutachan.replay.replay.ReplayPacket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class PacketMap<T extends ReplayPacket> {
    private final Map<Object, T> packets = new HashMap<>();

    public abstract void onPacket(ReplayPacket packet);

    public Collection<T> values() {
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

    public static class ChunkPair {
        private final int x, z;

        public ChunkPair(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChunkPair)) return false;
            ChunkPair chunkPair = (ChunkPair) o;
            return x == chunkPair.x && z == chunkPair.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
    }
}