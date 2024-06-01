package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.List;
import java.util.UUID;

public class PlayerSelfData extends ReplayWrapper<PlayerSelfData> {
    private int entityId;
    private Location pos;
    private UUID uuid;
    private float headYaw;
    //private UserProfile userProfile;
    //private ItemStack itemUse;
    //private List<PotionType> potionTypes = new ArrayList<>();
    private List<EntityData> entityData;
    private Dimension dimension;

    public PlayerSelfData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    @Override
    public void read() {
        this.entityId = readInt();
        this.pos = new Location(
                readDouble(),
                readDouble(),
                readDouble(),
                readFloat(),
                readFloat()
        );
        this.headYaw = readFloat();
        this.uuid = readUUID();
        this.entityData = readEntityMetadata();
        this.dimension = readDimension();
    }

    @Override
    public void write() {
        writeInt(this.entityId);
        writeDouble(this.pos.getX());
        writeDouble(this.pos.getY());
        writeDouble(this.pos.getZ());
        writeFloat(this.pos.getYaw());
        writeFloat(this.pos.getPitch());
        writeFloat(this.headYaw);
        writeUUID(this.uuid);
        writeEntityMetadata(this.entityData);
        writeDimension(this.dimension);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPacket() {
        return null;
    }

    @Override
    public List<PacketWrapper<?>> getUntilPacket() {
        return null;
    }
}
