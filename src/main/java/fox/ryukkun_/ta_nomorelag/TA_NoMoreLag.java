package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fox.ryukkun_.ta_nomorelag.event.StartTA;
import fox.ryukkun_.ta_nomorelag.players.PlayersData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;



public final class TA_NoMoreLag extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup login

        getServer().getPluginManager().registerEvents(new StartTA(), this);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.POSITION, PacketType.Play.Client.LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                PlayersData.get_player(player).add_packet(packet);
            }
        });

        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.KEEP_ALIVE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                for (TAUnit unit : GetPing.waiting) {
                    if (unit.ta_player.player == event.getPlayer()) {
                        int ping = (int)(System.currentTimeMillis() - packet.getLongs().read(0));
                        unit.ta_player.player.sendMessage( Integer.valueOf(ping).toString() );
                        //GetPing.waiting.remove(unit);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
