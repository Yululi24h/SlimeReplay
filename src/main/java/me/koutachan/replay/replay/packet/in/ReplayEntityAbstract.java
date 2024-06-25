package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;

import java.util.List;

public abstract class ReplayEntityAbstract extends ReplayWrapper<ReplayEntityAbstract> {
    protected ClassType classType;

    protected int entityId;
    protected Vector3d position;

    public ReplayEntityAbstract() {

    }

    public ReplayEntityAbstract(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayEntityAbstract(ClassType classType, int entityId, Vector3d position) {
        this.classType = classType;
        this.entityId = entityId;
        this.position = position;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public void read() {
        this.entityId = readVarInt();
        this.position = new Vector3d(
                readDouble(),
                readDouble(),
                readDouble()
        );
    }

    @Override
    public void write() {
        writeByte(this.classType.ordinal());
        writeVarInt(this.entityId);
        writeDouble(this.position.getX());
        writeDouble(this.position.getY());
        writeDouble(this.position.getZ());
    }

    public int getEntityId() {
        return entityId;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public abstract Location getLocation();

    public abstract void setLocation(Location location);

    public abstract void setEntityMeta(List<EntityData> entityData);

    public static ReplayEntityAbstract of(ServerVersion version, Object byteBuf) {
        ClassType classType = ClassType.getByOrdinal(ByteBufHelper.readByte(byteBuf));
        if (classType == ClassType.OBJECT) {
            return new ReplaySpawnEntity(version, byteBuf);
        } else if (classType == ClassType.LIVING) {
            return new ReplaySpawnLivingEntity(version, byteBuf);
        } else if (classType == ClassType.PLAYER) {
            return new ReplaySpawnPlayer(version, byteBuf);
        }
        return null;
    }

    public enum ClassType {
        OBJECT,
        LIVING,
        PLAYER,
        PAINTING;

        public final static ClassType[] CLASS_TYPE = values();

        public static ClassType getByOrdinal(byte l) {
            return CLASS_TYPE[l];
        }
    }
}
