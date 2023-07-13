package fox.ryukkun_.ta_nomorelag.event;

import fox.ryukkun_.ta_nomorelag.players.PlayersData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayersData.get_player(event.getPlayer()).player = event.getPlayer();
    }
}
