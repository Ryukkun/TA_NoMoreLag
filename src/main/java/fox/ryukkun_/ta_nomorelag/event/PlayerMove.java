package fox.ryukkun_.ta_nomorelag.event;

import fox.ryukkun_.ta_nomorelag.TA_NoMoreLag;
import fox.ryukkun_.ta_nomorelag.players.PlayersData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    private static final Material start_block = Material.getMaterial( TA_NoMoreLag.get_plugin().getConfig().getString("start_block"));
    private static final Material finish_block = Material.getMaterial( TA_NoMoreLag.get_plugin().getConfig().getString("finish_block"));

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        Location from = e.getFrom();

        Block to1 = to.clone().add(0, -0.99, 0).getBlock();
        Block to2 = to.clone().add(0, -1.99, 0).getBlock();
            // ta start
        if (to1.getType().equals(start_block) && to2.getType().equals(Material.COMMAND)) {
            CommandBlock block2 = (CommandBlock) to2.getState();
            PlayersData.get_player(e.getPlayer()).start_ta(block2.getCommand(), to);

            // on_start のロック解除
        } else if (from.clone().add(0, -0.99, 0).getBlock().getType().equals(start_block) &&
                from.clone().add(0, -1.99, 0).getBlock().getType().equals(Material.COMMAND)) {
            PlayersData.get_player(e.getPlayer()).on_start = false;

            // ta stop
        } else if (to1.getType().equals(finish_block) && to2.getType().equals(Material.COMMAND)) {
            CommandBlock block2 = (CommandBlock)to2.getState();
            PlayersData.get_player(e.getPlayer()).stop_ta(block2.getCommand(), to);
        }
    }
}
