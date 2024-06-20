package me.koutachan.replay.replay.user.replay;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.ReplayPacketContainer;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;

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
    private boolean enabled;

    private boolean confirmed;

    private final ReplayPacketContainer container;
    private final ReplayRunnerHandler handler;

    public ReplayRunner(ReplayUser user, ReplayPacketContainer container) {
        this.user = user;
        this.container = container;
        this.handler = new ReplayRunnerHandler(user, ReplayChain.fromContainer(container));
    }

    public void start() {
        this.enabled = true;
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!paused) {
                    processPacket();
                }
            }
        }, TICK_MILLISECONDS, TICK_MILLISECONDS);
        //processPacket();
    }

    public long calculateTicks() {
        return (long) (TICK_MILLISECONDS * this.speed);
    }

    public void processPacket() {
        this.handler.nextChain(calculateTicks());
    }

    public void sendActionBar() {
        //TODO:
        long millis = this.handler.getMillis();
        ReplayPacket lastPacket = this.container.get(this.container.size() - 1); // We can calculate eliminated time

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

    public ReplayPacketContainer getContainer() {
        return container;
    }
}
