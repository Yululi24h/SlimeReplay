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
import me.koutachan.replay.replay.user.replay.chain.ReplayChain;
import me.koutachan.replay.replay.user.replay.chain.ReplayChainFactory;
import me.koutachan.replay.replay.user.replay.chain.ReplayRunnerHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ReplayRunner {
    private final static long TICK_MILLISECONDS = 50L;
    private Timer timer;
    private final ReplayUser user;

    private long currentTick;
    private double speed = 1D;
    private boolean paused;
    private boolean enabled;

    private final ReplayRunnerHandler handler;
    private final ReplayChain lastChain;
    private final ReplayChain firstChain;

    public ReplayRunner(ReplayUser user, ReplayPacketContainer container) {
        this.user = user;
        ReplayChainFactory factory = ReplayChain.toContainer(container);
        this.firstChain = factory.firstChain;
        this.lastChain = factory.currentChain;
        Bukkit.getLogger().info(factory.count + " Replay Chain('s) loaded. Starting replay runner now.");
        this.handler = new ReplayRunnerHandler(user, this.firstChain);
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
                sendActionBar();
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
        long currentMillis = this.handler.getMillis();
        long lastMillis = this.lastChain.getMillis();
        this.user.getUser().sendMessage(Component.text(String.format("%s/%s", currentMillis, lastMillis)));
        //this.user.sendSilent(new WrapperPlayServerSystemChatMessage(true, Component.text(String.format("%s/%s", millis, lastPacket.getMillis()))));
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
            this.handler.onCompleteTeleport(new WrapperPlayClientTeleportConfirm(event).getTeleportId());
        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())){
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
            if (flying.hasPositionChanged()) {
                this.handler.onMove(flying.getLocation());
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

    public ReplayChain getFirstChain() {
        return firstChain;
    }

    public ReplayChain getLastChain() {
        return lastChain;
    }
}
