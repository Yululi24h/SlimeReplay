package me.koutachan.replay.replay.user.replay;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTeleportConfirm;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.user.ReplayUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ReplayRunner {
    private final static long TICK_MILLISECONDS = 50L;
    private Timer timer;

    private ReplayUser user;

    private long currentTick;
    private double speed = 1000D;
    private boolean paused;
    private int rows;
    private boolean enabled;

    private boolean confirmed;

    private boolean test = false;

    private final ReplayPacketContainer container;
    private final ReplayChunkHandler chunkHandler;

    public ReplayRunner(ReplayUser user, ReplayPacketContainer container) {
        this.user = user;
        this.container = container;
        this.chunkHandler = new ReplayChunkHandler(user, 4);

    }

    public void start() {
        enabled = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!paused) {
                    currentTick += calculateTicks();
                }
                processPacket();
            }
        }, TICK_MILLISECONDS, TICK_MILLISECONDS);
        //processPacket();
    }

    public long calculateTicks() {
        return (long) (TICK_MILLISECONDS * speed);
    }

    public void processPacket() {
        while (true) {
            if (rows >= container.size())
                return;
            ReplayPacketContainer.RecordPacket recordPacket = container.get(rows);
            if (recordPacket == null || recordPacket.getMilliseconds() > currentTick)
                return;
            rows++;
            /*PacketWrapper<?> packetWrapper = recordPacket.getPacket().toPacket();
            if (packetWrapper instanceof WrapperPlayServerPlayerPositionAndLook) {
                chunkHandler.onPosition((WrapperPlayServerPlayerPositionAndLook) packetWrapper);
            }
            if (packetWrapper instanceof ReplayChunkData) {
                ReplayChunkData chunkData = (ReplayChunkData) packetWrapper;
                chunkHandler.addChunk(chunkData);
                return;
            } else if (packetWrapper instanceof WrapperPlayServerUnloadChunk) {
                WrapperPlayServerUnloadChunk chunkData = (WrapperPlayServerUnloadChunk) packetWrapper;
                chunkHandler.removeChunk(chunkData);
                return;

            }*/
            System.out.println("Sent=" + recordPacket.getPacket().toPacket().getClass().getName() + " sentMs=" + currentTick);
            user.sendSilent(recordPacket.getPacket());
        }
    }

    public void onReceivedPacket(ReplayUser user, PacketReceiveEvent event) {
        if (!enabled)
            return;
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Client.KEEP_ALIVE || packetType == PacketType.Play.Client.PONG)
            return;
        System.err.println("type=" + packetType.getName());
        if (packetType == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation confirmation = new WrapperPlayClientWindowConfirmation(event);
            if (confirmation.isAccepted()) {
                return;
            }
        }

        if (packetType == PacketType.Play.Client.TELEPORT_CONFIRM) {
            chunkHandler.onCompleted(new WrapperPlayClientTeleportConfirm(event));
        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            chunkHandler.addDeltaMovement(new WrapperPlayClientPlayerFlying(event));
        }
        event.setCancelled(true);
    }

    public void onSendPacket(ReplayUser user, PacketSendEvent event) {
        if (!enabled)
            return;
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Server.KEEP_ALIVE || packetType == PacketType.Play.Server.PING)
            return;
        if (packetType == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            WrapperPlayServerWindowConfirmation confirmation = new WrapperPlayServerWindowConfirmation(event);
            if (confirmation.isAccepted()) {
                return;
            }
        }
        if (packetType == PacketType.Play.Server.CHAT_MESSAGE || packetType == PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) {
            return;
        }
        event.setCancelled(true);
    }

    public static ReplayRunner ofFile(ReplayUser user, File file) {
        try {
            return new ReplayRunner(user, ReplayPacketContainer.read(new FileInputStream(file)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
