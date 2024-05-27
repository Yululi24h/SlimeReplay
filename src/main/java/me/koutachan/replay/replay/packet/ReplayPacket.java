package me.koutachan.replay.replay.packet;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class ReplayPacket implements Serializable {
    public ReplayPacket() {

    }

    public void write(DataOutputStream stream) throws IOException {
    }

    public void read(DataInputStream stream) throws IOException {
    }

    public abstract PacketWrapper<?> toPacket();

    public abstract boolean isGenerated();

    public boolean isSupported() {
        return true;
    }
}