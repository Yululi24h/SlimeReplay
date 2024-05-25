package me.koutachan.replay.replay.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.exception.PacketProcessException;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.buffer.ByteBufInputStream;
import com.github.retrooper.packetevents.netty.buffer.ByteBufOutputStream;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeConstant;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import me.koutachan.replay.replay.ReplayPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ReplayPacketImpl extends ReplayPacket {
    private PacketWrapper<?> packet;

    public ReplayPacketImpl(PacketWrapper<?> packet) {
        this.packet = packet;
    }

    public ReplayPacketImpl() {

    }

    @Override
    public void read(DataInputStream stream) throws IOException {
        super.read(stream);
        packet = createFakeWrapper(stream);
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        super.write(stream);
        // This only work for native packet
        stream.writeBoolean(packet.getPacketTypeData().getPacketType().getSide() == PacketSide.SERVER);
        stream.writeInt(packet.getServerVersion().getProtocolVersion());
        stream.writeInt(packet.getNativePacketId());
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

    public static PacketWrapper<?> createFakeWrapper(DataInputStream stream) throws IOException {
        try {
            boolean serverSide = stream.readBoolean();
            ServerVersion protocolVersion = ServerVersion.getById(stream.readInt());
            int nativePacketId = stream.readInt();
            Class<?> clazz = Class.forName(stream.readUTF());
            Object byteBuf = readByteBuf(stream);
            if (serverSide) {
                return (PacketWrapper<?>) clazz.getConstructor(PacketSendEvent.class).newInstance(new FakePacketSendEvent(nativePacketId, protocolVersion, byteBuf));
            } else {
                return (PacketWrapper<?>) clazz.getConstructor(PacketReceiveEvent.class).newInstance(new FakePacketReceiveEvent(nativePacketId, protocolVersion, byteBuf));
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public static Object readByteBuf(DataInputStream stream) throws IOException {
        Object byteBuf = PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().buffer();
        try (ByteBufOutputStream outputStream = new ByteBufOutputStream(byteBuf)) {
            byte[] bytes = new byte[stream.readInt()];
            if (bytes.length != stream.read(bytes))
                throw new IllegalStateException();
            outputStream.write(bytes);
        }
        return byteBuf;
    }

    public static class FakePacketSendEvent extends PacketSendEvent {
        // Not smart hacking/
        public FakePacketSendEvent(int packetID, ServerVersion serverVersion, Object byteBuf) throws PacketProcessException {
            super(packetID, null, serverVersion, null, new User(null, ConnectionState.PLAY, null, null), null, byteBuf);
        }
    }

    public static class FakePacketReceiveEvent extends PacketReceiveEvent {
        // Not smart hacking/
        public FakePacketReceiveEvent(int packetID, ServerVersion serverVersion, Object byteBuf) throws PacketProcessException {
            super(packetID, null, serverVersion, null, new User(null, ConnectionState.PLAY, null, null), null, byteBuf);
        }
    }

    public PacketTypeCommon getType() {
        return packet.getPacketTypeData().getPacketType();
    }

    public WrapperPlayServerChunkData asChunkData() {
        return (WrapperPlayServerChunkData) packet;
    }

    @Override
    public String toString() {
        return "ReplayPacketImpl{" +
                "packet=" + packet +
                '}';
    }
}
