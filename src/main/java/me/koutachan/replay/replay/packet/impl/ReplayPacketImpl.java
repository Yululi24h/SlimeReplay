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

public class ReplayPacketImpl extends ReplayPacket {
    private ReplayWrapper<?> packet;
    private boolean generated;

    public ReplayPacketImpl(ReplayWrapper<?> packet) {
        this.packet = packet;
    }

    public ReplayPacketImpl() {

    }

    @Override
    public void read(DataInputStream stream) throws IOException {
        super.read(stream);
        this.generated = true;
        packet = createFakeWrapper(stream);
        /*ByteBufHelper.release(packet.getBuffer());
        packet.buffer = null;*/
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        super.write(stream);
        stream.writeInt(packet.getServerVersion().getProtocolVersion());
        stream.writeUTF(packet.getClass().getName());
        packet.buffer = PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().buffer();
        packet.write();
        try (ByteBufInputStream inputStream = new ByteBufInputStream(packet.getBuffer(), true)) {
            int len = ByteBufHelper.readableBytes(packet.getBuffer());
            stream.writeInt(len);
            for (int b = 0; b < len; b++) {
                stream.write(inputStream.read());
            }
        }
    }

    @Override
    public PacketWrapper<?> toPacket() {
        return packet;
    }

    public static ReplayWrapper<?> createFakeWrapper(DataInputStream stream) throws IOException {
        try {
            ServerVersion protocolVersion = ServerVersion.getById(stream.readInt());
            Class<?> clazz = Class.forName(stream.readUTF());
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
    public String toString() {
        return "ReplayPacketImpl{" +
                "packet=" + packet +
                '}';
    }
}
