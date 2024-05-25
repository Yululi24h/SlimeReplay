package me.koutachan.replay.commands;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.impl.ReplayPacketImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;

public class PacketTestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ReplayPacketImpl impl = new ReplayPacketImpl(new WrapperPlayServerKeepAlive(10));
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(outputStream);
            impl.write(stream);
            ReplayPacketImpl im = new ReplayPacketImpl();
            im.read(new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray())));
            sender.sendMessage("Id: " + ((WrapperPlayServerKeepAlive) im.toPacket()).getId());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
