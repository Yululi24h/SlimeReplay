package me.koutachan.replay.utils;

import me.koutachan.replay.replay.packet.in.packetevents.LightData;

import java.util.BitSet;

public class LightDataUtils {
    public static void appendLightData(LightData lightData, LightData from) {
        append(from.getSkyLightCount(), lightData.getSkyLightArray(), from.getSkyLightArray(), lightData.getSkyLightMask(), lightData.getEmptySkyLightMask(), from.getSkyLightMask(), from.getEmptySkyLightMask());
        append(from.getBlockLightCount(), lightData.getBlockLightArray(), from.getBlockLightArray(), lightData.getBlockLightMask(), lightData.getEmptyBlockLightMask(), from.getBlockLightMask(), from.getEmptyBlockLightMask());
    }

    private static void append(int length, byte[][] sourceArray, byte[][] fromArray, BitSet sourceLightMask, BitSet sourceEmptyMask, BitSet fromLightMask, BitSet fromEmptyMask) {
        for (int i = 0; i < length; i++) {
            if (fromLightMask.get(i)) {
                sourceArray[i] = fromArray[i];
                updateMask(i, sourceLightMask, sourceEmptyMask);
            } else if (fromEmptyMask.get(i)) {
                sourceArray[i] = null;
                updateMask(i, sourceEmptyMask, sourceLightMask);
            }
        }
    }

    private static void updateMask(int i, BitSet bitSet1, BitSet bitSet2) {
        bitSet1.set(i, true);
        bitSet2.set(i, false);
    }
}
