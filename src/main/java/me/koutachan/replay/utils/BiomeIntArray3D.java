package me.koutachan.replay.utils;

import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;

public class BiomeIntArray3D extends BiomeArrayBase {
    private final int[] biomeArray;

    public BiomeIntArray3D(int[] biomeArray) {
        this.biomeArray = biomeArray;
    }

    @Override
    public byte[] toByteArray2D() {
        return toByteArray(toIntArray2D());
    }

    @Override
    public int[] toIntArray2D() { // 256 array size = 16 * 16
        int[] biomeArray = new int[256];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biomeArray[index(x, z)] = getBiomeAt(x, 0, z);
            }
        }
        return biomeArray;
    }

    @Override
    public int[] toIntArray3D() {
        return this.biomeArray;
    }

    @Override
    public DataPalette toDataPalette(DataPalette palette, int index) { // 64 array size
        final int fixedIndexPos = index * 4;
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                for (int y = 0; y < 4; y++) {
                    palette.set(x, y, z, getBiomeAt(x, y + fixedIndexPos, z));
                }
            }
        }
        return palette;
    }

    public int getBiomeAt(int x, int y, int z) {
        if (y > 255) { // Y limit
            y = 255;
        }
        int indexPos = index(x, y, z);
        return this.biomeArray[indexPos];
    }

    public static byte[] toByteArray(int[] byteArray) {
        byte[] intArray = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; intArray[i] = (byte) byteArray[i++]);
        return intArray;
    }
}
