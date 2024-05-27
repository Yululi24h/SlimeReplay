package me.koutachan.replay.replay.user.map.data;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnExperienceOrb;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.ArrayList;
import java.util.List;

public class PacketExperienceEntity extends BasePacketEntity {
    protected Vector3d pos;
    protected short count;

    public PacketExperienceEntity(ReplayUser user, int entityId, Vector3d pos, short count) {
        super(user, entityId);
        this.pos = pos;
        this.count = count;
    }

    public PacketExperienceEntity(ReplayUser user, WrapperPlayServerSpawnExperienceOrb experienceOrb) {
        super(user, experienceOrb.getEntityId());
        this.pos = new Vector3d(experienceOrb.getX(), experienceOrb.getY(), experienceOrb.getZ());
        this.count = experienceOrb.getCount();
    }

    @Override
    public void onMove(double deltaX, double deltaY, double deltaZ) {
        this.pos = new Vector3d(deltaX + pos.getX(), deltaY + pos.getY(), deltaZ + pos.getZ());
    }

    @Override
    public void onPosition(Location location) {
        super.onPosition(location);
        this.pos = new Vector3d(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public List<ReplayPacket> toPacket() {
        List<ReplayPacket> packets = new ArrayList<>();
        packets.add(new ReplayPacketImpl(new WrapperPlayServerSpawnExperienceOrb(entityId, pos.getX(), pos.getY(), pos.getZ(), count)));
        return packets;
    }
}