package me.koutachan.replay.replay.user.map.data;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPainting;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.ArrayList;
import java.util.List;

public class PacketPaintingEntity extends BasePacketEntity {
    public PacketPaintingEntity(ReplayUser user, WrapperPlayServerSpawnPainting painting) {
        super(user, painting.getEntityId());
    }

    @Override
    public List<ReplayPacket> toPacket() {
        List<ReplayPacket> packets = new ArrayList<>();
        return packets;
    }
}
