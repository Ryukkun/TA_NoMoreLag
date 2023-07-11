package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class GetPing {
    public static ArrayList<TAUnit> waiting = new ArrayList<>();

    public static void set_event() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(TA_NoMoreLag.get_plugin(), ListenerPriority.LOWEST, PacketType.Play.Client.KEEP_ALIVE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Long id = event.getPacket().getLongs().read(0);
                int ping = (int) (System.currentTimeMillis() - id);
                //event.getPlayer().sendMessage(Integer.valueOf(ping).toString());

                for (TAUnit unit : GetPing.waiting) {
                    if (unit.ta_player.player == event.getPlayer()) {

                        if (unit._start_packet_send_time == id) {
                            unit.set_ping(ping, true);
                            GetPing.waiting.remove(unit);
                            event.setCancelled(true);
                            break;

                        } else if (unit._stop_packet_send_time == id) {
                            unit.set_ping(ping, false);
                            GetPing.waiting.remove(unit);
                            event.setCancelled(true);
                            break;

                        }
                    }
                }
            }
        });
        manager.addPacketListener(new PacketAdapter(TA_NoMoreLag.get_plugin(), ListenerPriority.NORMAL, PacketType.Play.Server.KEEP_ALIVE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                long id = packet.getLongs().read(0);
                //event.getPlayer().sendMessage("send : " + packet.getLongs().read(0));
                for (TAUnit unit : GetPing.waiting) {
                    if (unit.ta_player.player == event.getPlayer()) {
                        long nowtime = System.currentTimeMillis();

                        if (unit._start_packet_send_time == id) {
                            unit._start_packet_send_time = nowtime;
                            packet.getLongs().write(0, nowtime);
                            break;

                        } else if (unit._stop_packet_send_time == id) {
                            unit._stop_packet_send_time = nowtime;
                            packet.getLongs().write(0, nowtime);
                            break;
                        }
                    }
                }
            }
        });
        //new WaitingChecker().runTaskTimer(TA_NoMoreLag.get_plugin(), 0L, 100L);
    }

    public static void want_ping(TAUnit unit, boolean start) {
        long nowtime = System.currentTimeMillis();
        if (start) {
            unit._start_packet_send_time = nowtime;
        } else {
            unit._stop_packet_send_time = nowtime;
        }
        GetPing.waiting.add(unit);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.KEEP_ALIVE);
        packet.getLongs().write(0, nowtime);

        try {
            manager.sendServerPacket(unit.ta_player.player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class WaitingChecker extends BukkitRunnable {
        @Override
        public void run() {
            long now_time = System.currentTimeMillis();
            for (int i = 0; i < GetPing.waiting.size(); i++) {
                TAUnit unit = GetPing.waiting.get(i);
                if (unit.start_ping == -1 && 5000 < (now_time - unit._start_packet_send_time)){
                    unit.set_ping(((CraftPlayer) unit.ta_player.player).getHandle().ping, true);
                    GetPing.waiting.remove(unit);
                    i--;

                } else if (unit.stop_ping == -1 && 5000 < (now_time - unit._stop_packet_send_time)) {
                    unit.set_ping(0, false);
                    GetPing.waiting.remove(unit);
                    i--;
                }
            }
        }

    }
}