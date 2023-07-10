package fox.ryukkun_.ta_nomorelag.players;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import fox.ryukkun_.ta_nomorelag.Packet;
import fox.ryukkun_.ta_nomorelag.TAUnit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;


public class TAPlayer {
    public Player player;
    public ArrayList<Packet> cache_packets = new ArrayList<>();
    public HashMap<String, TAUnit> running_ta = new HashMap<>();
    public long last_packet_time = 0;
    public TAPlayer(Player player) {
        this.player = player;
    }

    public void start_ta(String name, Location pos) {
        TAUnit ta = new TAUnit(name, this);
        boolean find = false;
        for (Packet _packet : this.cache_packets) {
            PacketContainer packet = _packet.packet;
            if (!find) {
                if (packet.getType().equals(PacketType.Play.Client.POSITION_LOOK) || packet.getType().equals(PacketType.Play.Client.POSITION)) {
                    if (packet.getDoubles().read(0).equals(pos.getX()) &&
                            packet.getDoubles().read(1).equals(pos.getY()) &&
                            packet.getDoubles().read(2).equals(pos.getZ())
                    ) {
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
        }
    }


    public void add_packet(PacketContainer packet) {
        Packet ta_packet = new Packet(packet, this.last_packet_time);
        this.cache_packets.add(ta_packet);
        this.last_packet_time = ta_packet.time;
        for (TAUnit unit : this.running_ta.values()){
            if (unit.phase == 0){
                unit.add_count(ta_packet);
            }
        }
        this.clear_packet(ta_packet.time);
    }

    public void clear_packet(long now_time) {
        this.cache_packets.removeIf(packet -> (now_time - 1000) > packet.time);
    }
}

