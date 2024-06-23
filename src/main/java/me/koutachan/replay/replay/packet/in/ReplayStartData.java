package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.ArrayList;
import java.util.List;

public class ReplayStartData extends ReplayWrapper<ReplayStartData> {
    private List<ReplayChunkData> chunkData;
    private List<ReplayUpdateLightData> lightData;
    private List<ReplayEntityAbstract> entityData;

    private int entityId; //TODO: Renaem to Source Id.
    private Dimension dimension;
    private Location location;

    public ReplayStartData(ReplayUser user) {
    }

    public ReplayStartData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayStartData(List<ReplayChunkData> chunkData, List<ReplayEntityAbstract> entityData, Dimension dimension, Location location) {
        this.chunkData = chunkData;
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14) && this.serverVersion.isOlderThanOrEquals(ServerVersion.V_1_17)) {
            this.lightData = new ArrayList<>();
            for (ReplayChunkData val : this.chunkData) {
                if (val.getLightData() != null) {
                    this.lightData.add(new ReplayUpdateLightData(val.getX(), val.getZ(), val.getLightData()));
                }
            }
        }
        this.entityData = entityData;
        this.dimension = dimension;
        this.location = location;
}

    @Override
    public void read() {
        int chunkSize = readVarInt();
        this.chunkData = new ArrayList<>();
        for (int i = 0; i < chunkSize; i++) {
            this.chunkData.add(new ReplayChunkData(this.serverVersion, this.buffer));
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14) && this.serverVersion.isOlderThanOrEquals(ServerVersion.V_1_17)) {
            this.lightData = new ArrayList<>();
            int lightSize = readVarInt();
            for (int i = 0; i < lightSize; i++) {
                this.lightData.add(new ReplayUpdateLightData(this.serverVersion, this.buffer));
            }
        }
        int entitySize = readVarInt();
        this.entityData = new ArrayList<>();
        for (int i = 0; i < entitySize; i++) {
            this.entityData.add(ReplayEntityAbstract.of(this.serverVersion, this.buffer));
        }
        //this.playerSelf = new ReplayPlayerSelfData(this.serverVersion, this.buffer);
        this.dimension = readDimension();
        this.location = new Location(
                readDouble(),
                readDouble(),
                readDouble(),
                readFloat(),
                readFloat()
        );
        //this.dimension = readDimension();
    }

    @Override
    public void write() {
        writeVarInt(this.chunkData.size());
        for (ReplayChunkData chunkData : this.chunkData) {
            writeWrapper(chunkData);
        }
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14) && this.serverVersion.isOlderThanOrEquals(ServerVersion.V_1_17)) {
            writeVarInt(this.lightData.size());
            for (ReplayUpdateLightData lightData : this.lightData) {
                writeWrapper(lightData);
            }
        }
        writeVarInt(this.entityData.size());
        for (ReplayEntityAbstract entityData : this.entityData) {
            writeWrapper(entityData);
        }
        writeDimension(this.dimension);
        writeDouble(this.location.getX());
        writeDouble(this.location.getY());
        writeDouble(this.location.getZ());
        writeFloat(this.location.getYaw());
        writeFloat(this.location.getPitch());
    }

    public List<ReplayChunkData> getChunkData() {
        return chunkData;
    }

    public List<ReplayUpdateLightData> getLightData() {
        return lightData;
    }

    public List<ReplayEntityAbstract> getEntityData() {
        return entityData;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        return null;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}