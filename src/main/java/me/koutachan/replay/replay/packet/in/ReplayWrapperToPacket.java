package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.List;

public interface ReplayWrapperToPacket {
    List<PacketWrapper<?>> getPacket();

    List<PacketWrapper<?>> getUntilPacket();
}