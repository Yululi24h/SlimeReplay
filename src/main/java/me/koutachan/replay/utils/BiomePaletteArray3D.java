package me.koutachan.replay.utils;

import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;

public class BiomePaletteArray3D implements BiomeArray{
    private DataPalette palette;

    public BiomePaletteArray3D(DataPalette palette) {
        this.palette = palette;
    }

    @Override
    public byte[] toByteArray2D() {
        return new byte[0];
    }

    @Override
    public int[] toIntArray2D() {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 4; z++) {
                    this.palette.get(x, y, z);
                }
            }
        }

        return new int[0];
    }

    @Override
    public int[] toIntArray3D() {
        return new int[0];
    }

    @Override
    public DataPalette toDataPalette(DataPalette palette, int index) {
        palette.storage = this.palette.storage;
        return palette;
    }
}
