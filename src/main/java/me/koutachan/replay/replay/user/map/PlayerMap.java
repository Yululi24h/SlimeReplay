package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.user.PacketMap;

public class PlayerMap extends PacketMap<ReplayPacket> {
    @Override
    public void onPacket(ReplayPacket packet) {
        PacketWrapper<?> packetWrapper = packet.toPacket();
        if (packetWrapper instanceof WrapperPlayServerSpawnPlayer) {

        }

    }
}
