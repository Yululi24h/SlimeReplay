package me.koutachan.replay.commands;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.ReplayUserContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        sender.sendMessage("Recording...! processing...");
        ReplayUser replayUser = ReplayUserContainer.getUser(((Player) sender).getUniqueId());
        if (replayUser != null) {
            replayUser.getEntities().remove();
            sender.sendMessage("Completed! ");
            replayUser.getEntities().spawn();
            sender.sendMessage("Completed!.. ");
            replayUser.startRecord(new File("C:\\Users\\rin11\\Desktop\\monopoly - コピー (2)\\test.slime"));
        }

        return true;
    }
}