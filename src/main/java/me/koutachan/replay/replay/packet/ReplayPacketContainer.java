package me.koutachan.replay.replay.packet;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReplayPacketContainer {
    public static int REPLAY_VERSION = 1;

    private ServerVersion version;
    private final List<ReplayPacket> packets;
    private boolean versionFlag;

    public ReplayPacketContainer() {
        this(new ArrayList<>(), null, false);
    }

    public ReplayPacketContainer(List<ReplayPacket> packets,  ServerVersion version, boolean versionFlag) {
        this.packets = packets;
        this.version = version;
        this.versionFlag = versionFlag;

    }

    public void write(OutputStream stream) throws IOException {
        write(stream, false);
    }

    public void write(OutputStream stream, boolean append) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(stream);
        for (ReplayPacket packet : this.packets) {
            if (!this.versionFlag && !append) {
                outputStream.write(REPLAY_VERSION);
                this.version = packet.getServerVersion();
                outputStream.writeInt(this.version.getProtocolVersion()); // For performance reasons, the server version will be saved only at the beginning.
                this.versionFlag = true;
            }
            outputStream.writeBoolean(true);
            packet.write(outputStream);
        }
    }

    public void addPacket(ReplayPacket packet) {
        this.packets.add(packet);
    }

    public void addPacket(List<ReplayPacket> packets) {
        for (ReplayPacket packet : packets) {
            addPacket(packet);
        }
    }

    public static ReplayPacketContainer read(InputStream stream) {
        ReplayPacketContainer con = new ReplayPacketContainer();
        try (DataInputStream dis = new DataInputStream(stream)) {
            if (!con.versionFlag) {
                int replayVersion = dis.readInt();
                if (replayVersion != REPLAY_VERSION) {
                    throw new IllegalStateException("This replay file was recorded on an older version and is not compatible. Please use a tool to convert older replay files to a newer version. (version=" + replayVersion + " cur=" + REPLAY_VERSION + ")");
                }
                con.version = ServerVersion.getById(dis.readInt());
                con.versionFlag = true;
            }
            while (dis.readBoolean()) {
                ReplayPacket packet = new ReplayPacketImpl(con.version);
                packet.read(dis);
                con.addPacket(packet);
            }
        } catch (EOFException ex) {
            /* Ignored */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public List<ReplayPacket> getPackets() {
        return packets;
    }

    public ReplayPacket get(int pos) {
        return packets.get(pos);
    }

    public int size() {
        return packets.size();
    }

    public boolean isEmpty() {
        return packets.isEmpty();
    }

    public void clear() {
        packets.clear();
    }

    public boolean isVersionFlag() {
        return versionFlag;
    }

    public void setVersionFlag(boolean versionFlag, ServerVersion version) {
        this.versionFlag = versionFlag;
        this.version = version;
    }

    public ServerVersion getServerVersion() {
        return version;
    }

    public ReplayPacketContainer copy() {
        return new ReplayPacketContainer(new ArrayList<>(this.packets), this.version, this.versionFlag);
    }
}