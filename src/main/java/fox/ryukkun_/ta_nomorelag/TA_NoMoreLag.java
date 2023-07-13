package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fox.ryukkun_.ta_nomorelag.event.PlayerMove;
import fox.ryukkun_.ta_nomorelag.players.PlayersData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;



public final class TA_NoMoreLag extends JavaPlugin {
    private static Plugin plugin;
    @Override
    public void onEnable() {
        // Plugin startup login
        plugin = this;

        getServer().getPluginManager().registerEvents(new PlayerMove(), this);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.POSITION, PacketType.Play.Client.LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                PlayersData.get_player(player).add_packet(packet);
            }
        });

        GetPing.set_event();
        PlayersData.set_event();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin get_plugin(){
        return plugin;
    }
}
