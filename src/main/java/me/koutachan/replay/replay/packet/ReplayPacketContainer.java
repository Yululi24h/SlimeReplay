package me.koutachan.replay.replay.packet;

import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReplayPacketContainer {
    private final List<ReplayPacket> packets;

    public ReplayPacketContainer() {
        this(new ArrayList<>());
    }

    public ReplayPacketContainer(List<ReplayPacket> packets) {
        this.packets = packets;
    }

    public void write(OutputStream stream) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(stream);
        for (ReplayPacket packet : this.packets) {
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
            while (dis.readBoolean()) {
                ReplayPacket packet = new ReplayPacketImpl();//(dis.readInt());
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

    public void clear() {
        packets.clear();
    }

    public ReplayPacketContainer copy() {
        return new ReplayPacketContainer(new ArrayList<>(this.packets));
    }
}