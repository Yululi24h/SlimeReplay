package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;

import java.util.ArrayList;
import java.util.List;

public class ReplayUpdateEntityData extends ReplayWrapper<ReplayUpdateEntityData> {
    private int entityId;
    private List<EntityData> entityData;

    public ReplayUpdateEntityData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayUpdateEntityData(PacketSendEvent event) {
        WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(event);
        this.entityId = wrapper.getEntityId();
        this.entityData = wrapper.getEntityMetadata();
    }

    public ReplayUpdateEntityData(int entityId, List<EntityData> entityData) {
        super();
        this.entityId = entityId;
        this.entityData = entityData;
    }

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

    public int getEntityId() {
        return entityId;
    }

    public List<EntityData> getEntityData() {
        return entityData;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityMetadata(
                this.entityId,
                this.entityData
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}
