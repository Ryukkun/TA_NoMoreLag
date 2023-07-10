package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import java.util.ArrayList;

public class GetPing {
    public static ArrayList<TAUnit> waiting = new ArrayList<>();

    public static void want_ping(TAUnit unit) {
        GetPing.waiting.add(unit);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.KEEP_ALIVE);
        packet.getLongs().write(0, System.currentTimeMillis());

        try {
            manager.sendServerPacket(unit.ta_player.player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
