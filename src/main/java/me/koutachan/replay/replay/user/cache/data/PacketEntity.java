package me.koutachan.replay.replay.user.cache.data;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.ReplayUser;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.*;

public class PacketEntity extends BasePacketEntity {
    public static boolean VELOCITY_FETCH = true;
    public static UUID EMPTY_UUID = new UUID(0L, 0L);

    protected UUID uuid;
    protected EntityType entityType;
    protected Location location;
    protected float headYaw;
    protected int data;
    protected Vector3d velocity;

    protected Map<EquipmentSlot, ItemStack> equipments = new HashMap<>();
    protected List<EntityData> entityData = new ArrayList<>();
    protected boolean flag;

    public PacketEntity(ReplayUser user, int entityId, UUID uuid, EntityType entityType, Location location, float headYaw, int data, Vector3d velocity) {
        super(user, entityId);
        this.uuid = uuid;
        this.entityType = entityType;
        this.location = location;
        this.headYaw = headYaw;
        this.data = data;
        this.velocity = velocity;
    }

    public PacketEntity(ReplayUser user, WrapperPlayServerSpawnEntity entity) {
        super(user, entity.getEntityId());
        this.uuid = entity.getUUID().orElse(EMPTY_UUID);
        this.entityType = entity.getEntityType();
        Vector3d position = entity.getPosition();
        this.location = new Location(position.getX(), position.getY(), position.getZ(), entity.getYaw(), entity.getPitch());
        this.data = entity.getData();
        this.velocity = entity.getVelocity().orElse(new Vector3d(-1.0D, -1.0D, -1.0D));
    }

    @Override
    public void onVelocity() {
        this.flag = true;
    }

    @Override
    public void onMove(double deltaX, double deltaY, double deltaZ) {
        this.location.setPosition(new Vector3d(deltaX + location.getX(), deltaY + location.getY(), deltaZ + location.getZ()));
    }

    @Override
    public void onRotation(float yaw, float pitch) {
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);
    }

    @Override
    public void onPosition(Location location) {
        this.location = location;
        this.flag = false;
    }

    @Override
    public void onEntityMetaUpdate(List<EntityData> entityData) {
        this.entityData = entityData;
    }

    @Override
    public void onHeadYaw(float yaw) {
        this.headYaw = yaw;
    }

    @Override
    public void onEquipmentUpdate(List<Equipment> equipments) {
        for (Equipment equipment : equipments) {
            this.equipments.put(equipment.getSlot(), equipment.getItem());
        }
    }

    public ReplayEntityAbstract toPacket() {
        /*List<ReplayPacket> packets = new ArrayList<>();
        Vector3d velocity = Vector3d.zero();
        if (flag && VELOCITY_FETCH) {
            Entity entity = fromId(entityId);
            if (entity != null) {
                // I don't want to track velocity data.
                // Also, I don't think anyone would be foolish enough to try to set Velocity without registering with Bukkit...
                this.location = new Location(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), entity.getLocation().getYaw(), entity.getLocation().getPitch());
                velocity = toVector3d(entity.getVelocity());
            }
        }
        //packets.add(new ReplayPacketImpl(new WrapperPlayServerSpawnEntity(entityId, uuid, entityType, location, headYaw, data, velocity)));
        return addEntityPacket(packets);*/
        return null;
    }

    protected List<ReplayPacket> addEntityPacket(List<ReplayPacket> packets) {
        if (!equipments.isEmpty()) {

            //packets.add(new ReplayPacketImpl(new WrapperPlayServerEntityEquipment(entityId, getEquipments())));
        }
        //packets.add(new ReplayPacketImpl(new WrapperPlayServerEntityMetadata(entityId, entityData)));
        //packets.add(new ReplayPacketImpl(new WrapperPlayServerEntityHeadLook(entityId, headYaw)));
        return packets;
    }

    public int getEntityId() {
        return entityId;
    }

    protected static Vector3d toVector3d(Vector vector) {
        return new Vector3d(vector.getX(), vector.getY(), vector.getZ());
    }

    public List<Equipment> getEquipments() {
        List<Equipment> equipments = new ArrayList<>();
        this.equipments.forEach(((slot, item) -> equipments.add(new Equipment(slot, item))));
        return equipments;
    }

    protected Entity fromId(int entityId) {
        return SpigotConversionUtil.getEntityById(user.getBukkitWorld(), entityId);
    }
}