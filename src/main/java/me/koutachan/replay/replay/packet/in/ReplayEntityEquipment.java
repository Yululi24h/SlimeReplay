package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;

import java.util.ArrayList;
import java.util.List;

public class ReplayEntityEquipment extends ReplayWrapper<ReplayEntityEquipment> {
    private int entityId;
    private List<Equipment> equipments = new ArrayList<>();

    public ReplayEntityEquipment(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayEntityEquipment(PacketSendEvent event) {
        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(event);
        this.entityId = wrapper.getEntityId();
        this.equipments = wrapper.getEquipment();
    }

    @Override
    public void read() {
        this.entityId = readVarInt();
        int length = readVarInt();
        for (int i = 0; i < length; i++) {
            this.equipments.add(new Equipment(
                    EquipmentSlot.getById(this.serverVersion, readVarInt()),
                    readItemStack()
            ));
        }
    }

    @Override
    public void write() {
        writeVarInt(this.entityId);
        writeVarInt(this.equipments.size());
        for (Equipment equipment : this.equipments) {
            writeVarInt(equipment.getSlot().getId(this.serverVersion));
            writeItemStack(equipment.getItem());
        }
    }

    public int getEntityId() {
        return entityId;
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityEquipment(
                this.entityId,
                this.equipments
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}
