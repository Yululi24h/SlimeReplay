
package me.koutachan.replay.replay.packet;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface ReplayPacket extends Serializable {
    void write(DataOutputStream stream) throws IOException;

    void read(DataInputStream stream) throws IOException;

    List<PacketWrapper<?>> toPacket();

    ReplayWrapper<?> getReplayWrapper();

    long getMillis();

    boolean isGenerated();

    boolean isSupported();
}