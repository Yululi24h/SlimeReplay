package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

public abstract class ReplayWrapper<T extends PacketWrapper<T>> extends PacketWrapper<T> implements ReplayWrapperToPacket {
    protected static ServerVersion CURRENT_VERSION = PacketEvents.getAPI().getServerManager().getVersion();

    public ReplayWrapper() {
        this(CURRENT_VERSION);
    }

    public ReplayWrapper(ServerVersion version) {
        super(version.toClientVersion(), version, -1);
    }

    public ReplayWrapper(ServerVersion version, Object byteBuf) {
        this(version);
        if (byteBuf != null) {
            this.buffer = byteBuf;
            read();
        }
    }

    public abstract boolean isSupportedVersion(ServerVersion version);

    protected void writeWrapper(PacketWrapper<?> wrapper) {
        wrapper.buffer = this.buffer;
        wrapper.write();
    }

    protected <U extends PacketWrapper<U>> U readWrapper(U wrapper) {
        wrapper.buffer = this.buffer;
        wrapper.read();
        return wrapper;
    }

    /*public boolean isSupportedVersion() {
        return getPacketTypeData().getNativePacketId() != -1;
    }*/
}