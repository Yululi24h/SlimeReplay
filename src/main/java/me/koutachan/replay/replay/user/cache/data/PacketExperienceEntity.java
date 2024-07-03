package me.koutachan.replay.replay.user.cache.data;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnExperienceOrb;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.ReplayUser;

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
    public ReplayEntityAbstract toPacket() {
        return null;
    }
}