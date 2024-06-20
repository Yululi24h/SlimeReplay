package me.koutachan.replay.replay.packet.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.buffer.ByteBufInputStream;
import com.github.retrooper.packetevents.netty.buffer.ByteBufOutputStream;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ReplayPacketImpl implements ReplayPacket {
    private long millis;
    private ReplayWrapper<?> packet;
    private boolean generated;

    public ReplayPacketImpl(ReplayWrapper<?> packet, long millis) {
        this.packet = packet;
        this.millis = millis;
    }

    public ReplayPacketImpl() {

    }

    @Override
    public void read(DataInputStream stream) throws IOException {
        this.generated = true;
        this.packet = createFakeWrapper(stream);
        this.millis = stream.readLong();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(this.packet.getServerVersion().getProtocolVersion());
        System.out.println("Clazz1: " + this.packet.getClass().getName());
        stream.writeUTF(this.packet.getClass().getName());
        this.packet.buffer = PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().buffer();
        this.packet.write();
        try (ByteBufInputStream inputStream = new ByteBufInputStream(this.packet.getBuffer(), true)) {
            int len = ByteBufHelper.readableBytes(this.packet.getBuffer());
            stream.writeInt(len);
            for (int b = 0; b < len; b++) {
                stream.write(inputStream.read());
            }
        }
        stream.writeLong(this.millis);
    }

    @Override
    public List<PacketWrapper<?>> toPacket() {
        return packet.getPackets();
    }

    @Override
    public ReplayWrapper<?> getReplayWrapper() {
        return packet;
    }

    @Override
    public long getMillis() {
        return millis;
    }

    public static ReplayWrapper<?> createFakeWrapper(DataInputStream stream) throws IOException {
        try {
            ServerVersion protocolVersion = ServerVersion.getById(stream.readInt());
            String  str = stream.readUTF();
            System.out.println("Clazz: " + str);

            Class<?> clazz = Class.forName(str);
            Object byteBuf = readByteBuf(stream);
            return (ReplayWrapper<?>) clazz.getConstructor(ServerVersion.class, Object.class).newInstance(protocolVersion, byteBuf);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public static Object readByteBuf(DataInputStream stream) throws IOException {
        Object byteBuf = PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().buffer();
        try (ByteBufOutputStream outputStream = new ByteBufOutputStream(byteBuf)) {
            byte[] bytes = new byte[stream.readInt()];
            int readBytes = stream.read(bytes);
            if (bytes.length != readBytes)
                throw new IllegalStateException("byteLens=" + bytes.length + " readBytes=" + readBytes);
            outputStream.write(bytes);
        }
        return byteBuf;
    }

    public PacketTypeCommon getType() {
        return packet.getPacketTypeData().getPacketType();
    }

    @Override
    public boolean isGenerated() {
        return generated;
    }

    @Override
    public boolean isSupported() {
        return packet.isSupportedVersion(PacketEvents.getAPI().getServerManager().getVersion());
    }

    @Override
    public String toString() {
        return "ReplayPacketImpl{" +
                "packet=" + packet +
                '}';
    }
}
