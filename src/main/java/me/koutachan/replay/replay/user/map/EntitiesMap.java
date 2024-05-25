package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.user.PacketMap;

public class EntitiesMap extends PacketMap<ReplayPacket> {
    @Override
    public void onPacket(ReplayPacket packet) {
        PacketWrapper<?> packetWrapper = packet.toPacket();
        if (packetWrapper instanceof WrapperPlayServerSpawnEntity) {
            register(((WrapperPlayServerSpawnEntity) packetWrapper).getEntityId(), packet);
        } else if (packetWrapper instanceof WrapperPlayServerSpawnPainting) {
            register(((WrapperPlayServerSpawnPainting) packetWrapper).getEntityId(), packet);
        } else if (packetWrapper instanceof WrapperPlayServerSpawnExperienceOrb) {
            register(((WrapperPlayServerSpawnExperienceOrb) packetWrapper).getEntityId(), packet);
        } else if (packetWrapper instanceof WrapperPlayServerSpawnPlayer) {
            register(((WrapperPlayServerSpawnPlayer) packetWrapper).getEntityId(), packet);
        } else if (packetWrapper instanceof WrapperPlayServerDestroyEntities) {
            unregister(((WrapperPlayServerDestroyEntities) packetWrapper).getEntityIds());
        }
    }
}
