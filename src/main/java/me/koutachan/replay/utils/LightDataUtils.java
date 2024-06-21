package me.koutachan.replay.utils;

import me.koutachan.replay.replay.packet.in.packetevents.LightData;

import java.util.BitSet;

public class LightDataUtils {
    public static void merge(LightData original, LightData from) {
        for (int i = 0; i < from.getSkyLightCount(); i++) {
            if (mergeLightMask(i, from.getSkyLightMask(), original.getSkyLightMask(), original.getEmptySkyLightMask())) {
                original.getSkyLightArray()[i] = from.getSkyLightArray()[i];
            } else {
                original.getSkyLightArray()[i] = null;
            }
        }
        for (int i = 0; i < from.getBlockLightCount(); i++) {
            if (mergeLightMask(i, from.getBlockLightMask(), original.getBlockLightMask(), original.getEmptyBlockLightMask())) {
                original.getBlockLightArray()[i] = from.getBlockLightArray()[i];
            } else {
                original.getBlockLightArray()[i] = null;
            }
        }
    }

    public static boolean mergeLightMask(int pos, BitSet fromLight, BitSet empty, BitSet light) {
        boolean hasLight = fromLight.get(pos);
        if (hasLight) {
            light.set(pos, true);
            empty.set(pos, false);
        } else {
            empty.set(pos, true);
            light.set(pos, false);
        }
        return hasLight;
    }
}
