package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.List;

public class ReplayUpdateEntityData extends ReplayWrapper<ReplayUpdateEntityData> {
    private int entityId;
    private List<EntityData> entityData;

    @Override
    public void read() {
        this.entityId = readInt();
        this.entityData = readEntityMetadata();
    }

    @Override
    public void write() {
        writeInt(entityId);
        writeEntityMetadata(entityData);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
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
