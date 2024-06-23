package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Deprecated
public class ReplayPlayerSelfData extends ReplayWrapper<ReplayPlayerSelfData> {
    private int entityId;
    private Location pos;
    private UUID uuid;
    private float headYaw;
    private List<EntityData> entityData;
    private Dimension dimension;

    public ReplayPlayerSelfData(ServerVersion version, Object byteBuf) {
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
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        //int entityId, UUID uuid, Location location, List<EntityData> entityMetadata
        packets.add(new WrapperPlayServerSpawnPlayer(
                this.entityId,
                this.uuid,
                this.pos,
                this.entityData
        ));
        //packets.add(new WrapperPlayServerSpawnPlayer());

        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}