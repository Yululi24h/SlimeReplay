package me.koutachan.replay.replay;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import me.koutachan.replay.replay.impl.ReplayPacketImpl;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PacketReader {
    public ReplayPacketContainer read(InputStream stream) {
        ReplayPacketContainer con = new ReplayPacketContainer();
        try (DataInputStream dis = new DataInputStream(stream)) {
            while (dis.readInt() != 0) {
                ReplayPacket packet = new ReplayPacketImpl();//(dis.readInt());
                packet.read(dis);
                con.addPacket(packet);
            }
        } catch (EOFException ex) {
            /* Ignored */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}