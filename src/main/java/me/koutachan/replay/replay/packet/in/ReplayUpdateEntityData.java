package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;

import java.util.ArrayList;
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
        writeInt(this.entityId);
        writeEntityMetadata(this.entityData);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPacket() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityMetadata(
                this.entityId,
                this.entityData
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getUntilPacket() {
        return null;
    }
}
