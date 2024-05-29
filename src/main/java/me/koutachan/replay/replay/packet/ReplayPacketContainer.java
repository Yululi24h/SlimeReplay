package me.koutachan.replay.replay.packet;

import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReplayPacketContainer {
    private final List<RecordPacket> packets;

    public ReplayPacketContainer() {
        this(new ArrayList<>());
    }

    public ReplayPacketContainer(List<RecordPacket> packets) {
        this.packets = packets;
    }

    public void write(OutputStream stream) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(stream);
        for (RecordPacket packet : packets) {
            outputStream.writeBoolean(true);
            packet.write(outputStream);
        }
    }

    public void addPacket(ReplayPacket packet, long milliseconds) {
        packets.add(new RecordPacket(packet, milliseconds));
    }

    public void addPacket(List<ReplayPacket> packets, long milliseconds) {
        for (ReplayPacket packet : packets) {
            addPacket(packet, milliseconds);
        }
    }

    public static ReplayPacketContainer read(InputStream stream) {
        ReplayPacketContainer con = new ReplayPacketContainer();
        try (DataInputStream dis = new DataInputStream(stream)) {
            while (dis.readBoolean()) {
                ReplayPacket packet = new ReplayPacketImpl();//(dis.readInt());
                packet.read(dis);
                con.addPacket(packet, dis.readLong());
            }
        } catch (EOFException ex) {
            /* Ignored */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public RecordPacket get(int pos) {
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