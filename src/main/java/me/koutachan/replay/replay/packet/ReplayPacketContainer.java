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
        for (ReplayPacket packet : packets) {
            outputStream.writeBoolean(true);
            packet.write(outputStream);
        }
    }

    public void addPacket(ReplayPacket packet) {
        packets.add(packet);
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
        return new ReplayPacketContainer(new ArrayList<>(packets));
    }

    public static class RecordPacket {
        private final ReplayPacket packet;
        private final long milliseconds;

        public RecordPacket(ReplayPacket packet, long milliseconds) {
            this.packet = packet;
            this.milliseconds = milliseconds;
        }

        public ReplayPacket getPacket() {
            return packet;
        }

        public long getMilliseconds() {
            return milliseconds;
        }

        public void write(DataOutputStream stream) throws IOException {
            packet.write(stream);
            stream.writeLong(milliseconds);
        }
    }
}