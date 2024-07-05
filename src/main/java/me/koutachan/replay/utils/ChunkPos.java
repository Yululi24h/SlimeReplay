package me.koutachan.replay.utils;

import java.util.Objects;

public record ChunkPos(int x, int z) {
    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkPos chunkPos)) return false;
        return x == chunkPos.x && z == chunkPos.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public String toString() {
        return "ChunkPos{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}