package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;



public final class TA_NoMoreLag extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup login

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.POSITION) {

            long time = System.currentTimeMillis();
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                long dif = System.currentTimeMillis() - time;
                time = System.currentTimeMillis();
                //player.sendMessage("IKIteru! Time:" + dif);
                Double x = packet.getDoubles().read(0);
                Double y = packet.getDoubles().read(1);
                Double z = packet.getDoubles().read(2);
                player.sendMessage("x:" + x + " y:" + y + " z:" + z + " Time:" + dif);
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
