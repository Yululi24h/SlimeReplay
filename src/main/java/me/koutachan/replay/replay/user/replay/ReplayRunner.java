package me.koutachan.replay.replay.user.replay;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import me.koutachan.replay.replay.user.ReplayUser;

public class ReplayRunner {

    public void onReceivedPacket(ReplayUser user, PacketReceiveEvent event) {
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Client.KEEP_ALIVE || packetType == PacketType.Play.Client.PONG)
            return;
        if (packetType == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation confirmation = new WrapperPlayClientWindowConfirmation(event);
            if (confirmation.isAccepted()) {
                return;
            }
        }
        event.setCancelled(true);
    }

    public void onSendPacket(ReplayUser user, PacketSendEvent event) {
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Server.KEEP_ALIVE || packetType == PacketType.Play.Server.PING)
            return;
        if (packetType == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            WrapperPlayServerWindowConfirmation confirmation = new WrapperPlayServerWindowConfirmation(event);
            if (confirmation.isAccepted()) {
                return;
            }
        }
        event.setCancelled(true);
    }
}
