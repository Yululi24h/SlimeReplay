package me.koutachan.replay.replay;

import me.koutachan.replay.replay.impl.ReplayPacketImpl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
}