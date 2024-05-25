package me.koutachan.replay.replay;

import me.koutachan.replay.replay.impl.ReplayPacketImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReplayPacketContainer {
    private List<ReplayPacket> packets = new ArrayList<>();

    public ReplayPacketContainer() {

    }

    public void write(OutputStream stream) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(stream);
        for (ReplayPacket packet : packets) {
            outputStream.writeInt(1);
            packet.write(outputStream);
        }
        outputStream.writeInt(-1);
    }

    public void addPacket(ReplayPacket packet) {
        packets.add(packet);
    }

    public static ReplayPacketContainer read(InputStream stream) {
        ReplayPacketContainer con = new ReplayPacketContainer();
        try (DataInputStream dis = new DataInputStream(stream)) {
            while (dis.readInt() != 0) {
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
}