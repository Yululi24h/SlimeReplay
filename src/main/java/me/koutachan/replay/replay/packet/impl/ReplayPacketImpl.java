package me.koutachan.replay.replay.packet.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.exception.PacketProcessException;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.buffer.ByteBufOutputStream;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import me.koutachan.replay.replay.packet.ReplayPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class ReplayPacketImpl extends ReplayPacket {
    public static ServerVersion VERSION = PacketEvents.getAPI().getServerManager().getVersion();

    private PacketWrapper<?> packet;
    private boolean generated;

    public ReplayPacketImpl(PacketWrapper<?> packet) {
        this.packet = packet;
    }

    public ReplayPacketImpl() {

    }

    @Override
    public void read(DataInputStream stream) throws IOException {
        super.read(stream);
        this.generated = true;
        packet = setVersion(createFakeWrapper(stream));
        /*ByteBufHelper.release(packet.getBuffer());
        packet.buffer = null;*/
    }

    private PacketWrapper<?> setVersion(PacketWrapper<?> packet) {
        try {
            Field serverVersion = PacketWrapper.class.getDeclaredField("serverVersion");
            boolean accessible = serverVersion.isAccessible();
            if (!accessible)
                serverVersion.setAccessible(true);
            serverVersion.set(packet, VERSION);
            serverVersion.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return packet;
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        super.write(stream);
        // This only work for native packet
        stream.writeBoolean(packet.getPacketTypeData().getPacketType().getSide() == PacketSide.SERVER);
        stream.writeInt(packet.getServerVersion().getProtocolVersion());
        stream.writeInt(packet.getNativePacketId());
        stream.writeUTF(packet.getClass().getName());
        /*if (packet.buffer != null) {
            ByteBufHelper.release(packet.buffer);
            packet.buffer = null;
        }*/
        packet.buffer = PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().buffer();
        packet.write();
        byte[] packetToBytes = ByteBufHelper.copyBytes(packet.buffer);
        ByteBufHelper.release(packet.buffer);
        stream.writeInt(packetToBytes.length);
        stream.write(packetToBytes);
        /*try (ByteBufInputStream inputStream = new ByteBufInputStream(packet.getBuffer(), true)) {
            int len = ByteBufHelper.readableBytes(packet.getBuffer());
            stream.writeInt(len);
            for (int b = 0; b < len; b++) {
                int le = inputStream.read();
                //System.out.println("b ==" + b + " len= " + len + " pos==" + le);
                stream.write(le);
            }
        }*/
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
                PacketTypeCommon packetType = PacketType.Play.Server.getById(protocolVersion.toClientVersion(), nativePacketId);
                if (packetType != null) {// Fix Packet Id {
                    nativePacketId = packetType.getId(VERSION.toClientVersion());
                }
                return (PacketWrapper<?>) clazz.getConstructor(PacketSendEvent.class).newInstance(new FakePacketSendEvent(nativePacketId, protocolVersion, byteBuf));
            } else {
                PacketTypeCommon packetType = PacketType.Play.Client.getById(protocolVersion.toClientVersion(), nativePacketId);
                if (packetType != null) // Fix Packet Id
                    nativePacketId = packetType.getId(VERSION.toClientVersion());
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
            int readBytes = stream.read(bytes);
            if (bytes.length != readBytes)
                throw new IllegalStateException("byteLens=" + bytes.length + " readBytes=" + readBytes);
            outputStream.write(bytes);
        }
        return byteBuf;
    }

    public static class FakePacketSendEvent extends PacketSendEvent {
        // Not smart hacking
        public FakePacketSendEvent(int packetID, ServerVersion serverVersion, Object byteBuf) throws PacketProcessException {
            super(packetID, null, serverVersion, null, new User(null, ConnectionState.PLAY, null, null), null, byteBuf);
        }
    }

    public static class FakePacketReceiveEvent extends PacketReceiveEvent {
        // Not smart hacking
        public FakePacketReceiveEvent(int packetID, ServerVersion serverVersion, Object byteBuf) throws PacketProcessException {
            super(packetID, null, serverVersion, null, new User(null, ConnectionState.PLAY, null, null), null, byteBuf);
        }
    }

    public PacketTypeCommon getType() {
        return packet.getPacketTypeData().getPacketType();
    }

    @Override
    public boolean isGenerated() {
        return generated;
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
