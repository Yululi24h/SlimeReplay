package me.koutachan.replay.utils;

public abstract class BiomeArrayBase implements BiomeArray {
    private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
    private static final int HEIGHT_BITS = (int)Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
    public static final int BIOMES_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + HEIGHT_BITS;
    public static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
    public static final int VERTICAL_MASK = (1 << HEIGHT_BITS) - 1;

    public static int index(int x, int z) {
        return (x << 4) | z;
    }

    public static int index(int x, int y, int z) {
        int i = x & HORIZONTAL_MASK;
        int j = clamp(y, 0, VERTICAL_MASK);
        int k = z & HORIZONTAL_MASK;
        return j << (WIDTH_BITS + WIDTH_BITS) | k << WIDTH_BITS | i;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        } else {
            return Math.min(value, max);
        }
    }
}
