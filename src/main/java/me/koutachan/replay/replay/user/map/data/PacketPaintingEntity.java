package me.koutachan.replay.replay.user.map.data;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPainting;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.ReplayUser;

public class PacketPaintingEntity extends BasePacketEntity {
    public PacketPaintingEntity(ReplayUser user, WrapperPlayServerSpawnPainting painting) {
        super(user, painting.getEntityId());
    }

    @Override
    public ReplayEntityAbstract toPacket() {
        return null;
    }
}
