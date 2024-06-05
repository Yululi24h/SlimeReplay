package me.koutachan.replay.utils;

import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;

public class BiomeByteArray2D implements BiomeArray {
    private final byte[] biomeArray;

    public BiomeByteArray2D(byte[] biomeArray) {
        this.biomeArray = biomeArray;
    }

    @Override
    public byte[] toByteArray2D() {
        return this.biomeArray;
    }

    @Override
    public int[] toIntArray2D() {
        return toIntArray(this.biomeArray);
    }

    @Override
    public int[] toIntArray3D() {
        return new int[0];
    }

    @Override
    public DataPalette toDataPalette(DataPalette palette, int index) { // 64 array size
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                int l = this.biomeArray[(x * 4) << 4 | (z * 4)] & 255;
                for (int y = 0; y < 4; y++) {
                    palette.set(x, y, z, l);
                }
            }
        }
        return palette;
    }


    public static int[] toIntArray(byte[] byteArray) {
        int[] intArray = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; intArray[i] = byteArray[i++]);
        return intArray;
    }
}