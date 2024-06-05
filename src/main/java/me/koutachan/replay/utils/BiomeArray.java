package me.koutachan.replay.utils;

import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;

public interface BiomeArray {
    byte[] ZERO = new byte[2048];

    byte[] toByteArray2D();

    int[] toIntArray2D();

    int[] toIntArray3D();

    DataPalette toDataPalette(DataPalette palette, int index);


}