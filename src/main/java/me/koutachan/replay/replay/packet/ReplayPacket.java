package me.koutachan.replay.replay.packet;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public abstract class ReplayPacket implements Serializable {
    public ReplayPacket() {

    }

    public void write(DataOutputStream stream) throws IOException {
    }

    public void read(DataInputStream stream) throws IOException {
    }

    public abstract List<PacketWrapper<?>> toPacket();

    public abstract ReplayWrapper<?> getReplayWrapper();

    public abstract boolean isGenerated();

    public boolean isSupported() {
        return true;
    }
}