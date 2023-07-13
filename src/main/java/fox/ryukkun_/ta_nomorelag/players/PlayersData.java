package fox.ryukkun_.ta_nomorelag.players;

import fox.ryukkun_.ta_nomorelag.TA_NoMoreLag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;

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

    public static void set_event(){
        new OfflineChecker().runTaskTimer(TA_NoMoreLag.get_plugin(), 0L, 24000L);
    }

    public static class OfflineChecker extends BukkitRunnable {
        @Override
        public void run() {
            for (Iterator<Player> i = players_map.keySet().iterator(); i.hasNext();){
                Player player = i.next();
                OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
                if (!op.isOnline() && 1200 * 1000 < (System.currentTimeMillis() - op.getLastPlayed())){
                    i.remove();
                }
            }
        }
    }
}
