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

            long time = 0;
            long amari = 0;
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                long  dif;
                long now_time = System.currentTimeMillis();
                if (time == 0){
                    dif = 0;
                } else {
                    dif = now_time - time;
                }
                time = now_time;

                long _amari = dif % 50;
                //getLogger().info(" "+_amari);
                if (_amari > 25){
                    _amari = _amari - 50;
                }
                amari += _amari;
                //player.sendMessage("IKIteru! Time:" + dif);
                Double x = packet.getDoubles().read(0);
                Double y = packet.getDoubles().read(1);
                Double z = packet.getDoubles().read(2);
                player.sendMessage("x:" + x + " y:" + y + " z:" + z + " Time:" + dif + " amari:" + amari);
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
