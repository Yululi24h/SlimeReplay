package me.koutachan.replay.replay.user.map;

import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.user.PacketMap;

public class PlayerMap extends PacketMap<ReplayPacket> {
    @Override
    public void onPacket(ReplayPacket packet) {
        /*PacketWrapper<?> packetWrapper = packet.toPacket();
        if (packetWrapper instanceof WrapperPlayServerSpawnPlayer) {

        }*/

    }
}
