package me.koutachan.replay.replay.user.map.data;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.user.ReplayUser;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketEntity {
    public static boolean VELOCITY_FETCH = true;
    protected ReplayUser user;

    protected int entityId;
    protected UUID uuid;
    protected EntityType entityType;
    protected Location location;
    protected float headYaw;

    protected int data;
    protected Vector3d velocity;

    protected List<Equipment> equipment = new ArrayList<>();
    protected boolean flag;

    public PacketEntity(ReplayUser user, int entityId, UUID uuid, EntityType entityType, Location location, float headYaw, int data, Vector3d velocity) {
        this.user = user;
        this.entityId = entityId;
        this.uuid = uuid;
        this.entityType = entityType;
        this.location = location;
        this.headYaw = headYaw;
        this.data = data;
        this.velocity = velocity;
    }

    public PacketEntity(ReplayUser user, WrapperPlayServerSpawnEntity entity) {
        entity.getVelocity();
    }

    public void onVelocity() {
        this.flag = true;
    }

    public void onPosition(Location location) {
        this.location = location;
        this.flag = false;
    }

    public void onHeadYaw(float yaw) {
        this.headYaw = yaw;
    }

    public List<ReplayPacket> toPacket() {
        List<ReplayPacket> packets = new ArrayList<>();
        Vector3d velocity = Vector3d.zero();
        if (!flag && VELOCITY_FETCH) {
            Entity entity = fromId(entityId);
            // I don't want to track velocity data.
            // Also, I don't think anyone would be foolish enough to try to set Velocity without registering with Bukkit...
            this.location = new Location(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), entity.getLocation().getYaw(), entity.getLocation().getPitch());
            velocity = toVector3d(entity.getVelocity());
        }
        packets.add(new ReplayPacketImpl(new WrapperPlayServerSpawnEntity(entityId, uuid, entityType, location, headYaw, data, velocity)));
        if (!equipment.isEmpty()) {
            packets.add(new ReplayPacketImpl(new WrapperPlayServerEntityEquipment(entityId, equipment)));
        }
        return packets;
    }

    protected static Vector3d toVector3d(Vector vector) {
        return new Vector3d(vector.getX(), vector.getY(), vector.getZ());
    }

    protected Entity fromId(int entityId) {
        return SpigotConversionUtil.getEntityById(user.getBukkitWorld(), entityId);
    }
}