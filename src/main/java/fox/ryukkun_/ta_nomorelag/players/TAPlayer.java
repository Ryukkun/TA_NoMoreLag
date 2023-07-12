package fox.ryukkun_.ta_nomorelag.players;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import fox.ryukkun_.ta_nomorelag.Packet;
import fox.ryukkun_.ta_nomorelag.TAUnit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TAPlayer {
    public Player player;
    public final List<Packet> cache_packets = new CopyOnWriteArrayList<>();
    public HashMap<String, TAUnit> running_ta = new HashMap<>();
    public long last_packet_time = 0;
    public TAPlayer(Player player) {
        this.player = player;
    }

    public void start_ta(String name, Location pos) {
        TAUnit ta = new TAUnit(name, this);
        boolean find = false;

        for (Packet _packet : this.cache_packets) {
            if (!find) {
                if (!_packet.type.equals(PacketType.Play.Client.LOOK)) {
                    if (_packet.x == pos.getX() && _packet.y == pos.getY() && _packet.z == pos.getZ()) {
                        find = true;
                    }
                }
            }
            if (find) {
                ta.add_count(_packet);
            }
        }

        if (find) {
            running_ta.put(name, ta);
            String message = ">>> " + name + "&r 計測カイジ";
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public void stop_ta(String name, Location pos) {
        if (running_ta.containsKey(name)) {
            for (Packet _packet : this.cache_packets) {
                if (!_packet.type.equals(PacketType.Play.Client.LOOK)) {
                    if (_packet.x == pos.getX()  &&  _packet.y == pos.getY()  &&  _packet.z == pos.getZ()) {
                        running_ta.get(name).finish(_packet);
                        running_ta.remove(name);
                        break;
                    }
                }
            }
        }
    }



    public void add_packet(PacketContainer packet) {
        Packet ta_packet = new Packet(packet, this.last_packet_time);
        this.cache_packets.add(ta_packet);
        this.last_packet_time = ta_packet.time;
        for (TAUnit unit : this.running_ta.values()){
                unit.add_count(ta_packet);
        }
        this.clear_packet(ta_packet.time);

    }

    public void clear_packet(long now_time) {
        this.cache_packets.removeIf(packet -> (now_time - 5000) > packet.time);
    }
}

