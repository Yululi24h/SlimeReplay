package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;

import java.util.ArrayList;
import java.util.List;

public class ReplayEntityPos extends ReplayWrapper<ReplayEntityPos> {
    protected int entityId;
    protected Vector3d pos;
    protected boolean onGround;

    public ReplayEntityPos(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayEntityPos(PacketSendEvent event) {
        WrapperPlayServerEntityRelativeMove relative = new WrapperPlayServerEntityRelativeMove(event);
        this.entityId = relative.getEntityId();
        this.pos = new Vector3d(
                relative.getDeltaX(),
                relative.getDeltaY(),
                relative.getDeltaZ()
        );
        this.onGround = relative.isOnGround();
    }

    public ReplayEntityPos(int entityId, Vector3d pos, boolean onGround) {
        this.entityId = entityId;
        this.pos = pos;
        this.onGround = onGround;
    }

    @Override
    public void read() {
        this.entityId = readInt();
        this.pos = new Vector3d(
                readDouble(),
                readDouble(),
                readDouble()
        );
        this.onGround = readBoolean();
    }

    @Override
    public void write() {
        writeInt(this.entityId);
        writeDouble(this.pos.getX());
        writeDouble(this.pos.getY());
        writeDouble(this.pos.getZ());
        writeBoolean(this.onGround);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityRelativeMove(
                this.entityId,
                this.pos.getX(),
                this.pos.getY(),
                this.pos.getZ(),
                this.onGround
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}
