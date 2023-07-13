package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


class PingData {
    public boolean start;
    public long send_time;
    private static final Random random = new Random();
    public long id;
    public TAUnit ta;
    public Player player;
    public int ping;
    public PingData (TAUnit ta, boolean start) {
        this.ta = ta;
        this.player = ta.ta_player.player;
        this.start = start;
        this.id = random.nextLong();

    }

    public void set_ping(int ping){
        // this is async
        this.ping = ping;
        ta.set_ping(this);
    }
}


public class GetPing {
    public static final List<PingData> waiting = new CopyOnWriteArrayList<>();
    public static final List<PingData> _cancel_list = new CopyOnWriteArrayList<>();

    public static void set_event() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(TA_NoMoreLag.get_plugin(), ListenerPriority.NORMAL, PacketType.Play.Client.KEEP_ALIVE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                // this is async function
                Long id = event.getPacket().getLongs().read(0);
                long now_time = System.currentTimeMillis();

                for (PingData unit : GetPing.waiting) {
                    if (unit.player.equals(event.getPlayer()) && unit.id == id) {
                        unit.set_ping((int) (now_time - unit.send_time));
                        GetPing.waiting.remove(unit);
                        break;
                    }
                }

                for (PingData unit : GetPing._cancel_list){
                    if (unit.player.equals(event.getPlayer()) && unit.id == id) {
                        GetPing._cancel_list.remove(unit);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        });
        manager.addPacketListener(new PacketAdapter(TA_NoMoreLag.get_plugin(), ListenerPriority.NORMAL, PacketType.Play.Server.KEEP_ALIVE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                // this is sync function
                PacketContainer packet = event.getPacket();
                long id = packet.getLongs().read(0);

                for (PingData unit : GetPing.waiting) {
                    if (unit.player == event.getPlayer() && unit.id == id) {

                        unit.send_time = System.currentTimeMillis();
                        break;
                    }
                }
            }
        });
        new WaitingChecker().runTaskTimer(TA_NoMoreLag.get_plugin(), 0L, 100L);
    }

    public static void want_ping(TAUnit unit, boolean start) {
        PingData data = new PingData(unit, start);
        GetPing.waiting.add(data);
        GetPing._cancel_list.add(data);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.KEEP_ALIVE);
        packet.getLongs().write(0, data.id);

        try {
            manager.sendServerPacket(data.player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class WaitingChecker extends BukkitRunnable {
        @Override
        public void run() {
            long now_time = System.currentTimeMillis();
            for (int i = 0; i < GetPing.waiting.size(); i++) {
                PingData unit = GetPing.waiting.get(i);

                if (5000 < (now_time - unit.send_time)) {
                    unit.set_ping(5000);
                    GetPing.waiting.remove(unit);
                    i--;
                }
            }

            for (int i = 0; i < GetPing._cancel_list.size(); i++) {
                PingData unit = GetPing._cancel_list.get(i);
                if (60000 < (now_time - unit.send_time)) {
                    GetPing._cancel_list.remove(unit);
                    i--;
                }
            }
        }
    }
}