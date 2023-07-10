package fox.ryukkun_.ta_nomorelag.players;

import com.comphenix.protocol.PacketType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayersData {
    public static HashMap<Player, TAPlayer> players_map = new HashMap<>();

    public static void add_player(Player player) {
        if (!players_map.containsKey(player)) {
            players_map.put(player, new TAPlayer(player));
        }
    }

    public static TAPlayer get_player(Player player) {
        PlayersData.add_player(player);
        return players_map.get(player);
    }
}
