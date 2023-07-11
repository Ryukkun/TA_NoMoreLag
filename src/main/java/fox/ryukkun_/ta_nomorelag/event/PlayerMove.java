package fox.ryukkun_.ta_nomorelag.event;

import fox.ryukkun_.ta_nomorelag.players.PlayersData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location to = e.getTo();

        Block to1 = to.clone().add(0, -0.99, 0).getBlock();
        Block to2 = to.clone().add(0, -1.99, 0).getBlock();
        if (to1.getType().equals(Material.JUKEBOX) && to2.getType().equals(Material.COMMAND)) {
            CommandBlock block2 = (CommandBlock)to2.getState();
            PlayersData.get_player(e.getPlayer()).start_ta(block2.getCommand(), to);

        } else if (to1.getType().equals(Material.NOTE_BLOCK) && to2.getType().equals(Material.COMMAND)) {
            CommandBlock block2 = (CommandBlock)to2.getState();
            PlayersData.get_player(e.getPlayer()).stop_ta(block2.getCommand(), to);
        }
    }
}
