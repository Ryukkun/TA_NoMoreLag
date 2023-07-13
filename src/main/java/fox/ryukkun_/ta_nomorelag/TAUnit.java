package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import fox.ryukkun_.ta_nomorelag.players.TAPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;


public class TAUnit {
    public TAPlayer ta_player;
    public Player player;
    public String name;
    public boolean is_finished = false;
    public boolean is_counting = true;
    private final ArrayList<Packet> counted_packet = new ArrayList<>();
    public long start_time = -1;
    public ArrayList<Integer> _start_pings = new ArrayList<>();
    public int start_ping_result = -1;
    public long stop_time = -1;
    public  ArrayList<Integer> _stop_pings = new ArrayList<>();
    public int stop_ping_result = -1;
    private int count;
    private long time_ms;

    public TAUnit(String name, TAPlayer ta_player) {
        this.name = name;
        this.ta_player = ta_player;
        this.player = ta_player.player;
    }

    public void add_count(Packet packet) {
        if (this.is_counting && !this.is_finished) {
            if (45 < packet.duration && packet.duration < 60) {
                this.is_counting = false;
                this.start_time = packet.time;
                GetPing.want_ping(this, true);

            } else {
                counted_packet.add(packet);

            }
        }
    }

    private boolean all_green(){
        return (is_finished && (start_ping_result != -1 && stop_ping_result != -1 || is_counting));
    }

    private int get_tick(){
        int tick = 0;

        boolean _afk;
        boolean afk = false;
        Packet last_packet = null;
        for (Packet packet : counted_packet){
            _afk = false;
            if (last_packet != null) {
                if (!last_packet.type.equals(PacketType.Play.Client.LOOK) && !packet.type.equals(PacketType.Play.Client.LOOK)) {
                    if (last_packet.x == packet.x && last_packet.y == packet.y && last_packet.z == packet.z) {
                        _afk = true;
                    }
                }
            }

            if (afk || _afk){
                tick += 19;
                //Bukkit.getServer().getLogger().info("+19 " + packet.x + " " + packet.y + " " + packet.z );
            }
            afk = _afk;
            last_packet = packet;
            tick++;
        }


        long duration = 0;
        if (!is_counting) {
            duration = (stop_time - (stop_ping_result / 2)) - (start_time - (start_ping_result / 2));
        }

        count = tick;
        time_ms = duration;
        //Bukkit.getServer().getLogger().info("count:"+tick+" +timems:"+duration);
        tick += duration / 50;

        if (20 < (duration % 50)) {
            tick++;
        }

        return tick;
    }


    private String calc_time(int tick){
        int time = tick * 50;
        int ms = time % 1000;
        time /= 1000;
        int hour = time / 3600;
        int min = time / 60 % 60;
        int sec = time % 60;


        //Bukkit.getServer().getLogger().info("start:"+start_time+" ping:"+start_ping_result+" stop:"+stop_time+" ping"+stop_ping_result);
        String s_min = int_to_string(min);
        String s_sec = int_to_string(sec);
        String s_ms = Integer.valueOf(ms).toString();

        if (ms == 0){
            s_ms = "00"+s_ms;
        } else if (ms == 50){
            s_ms = "0"+s_ms;
        }

        if (hour == 0) {
            return s_min + ":" + s_sec + "." + s_ms;
        } else {
            return hour + ":" + s_min + ":" + s_sec + "." + s_ms;
        }
    }

    private static String int_to_string(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return Integer.valueOf(i).toString();
        }
    }


    public void set_ping(PingData ping){
        // this is async
        // pingが最後に得られる値だから タイム計測とかもここに書いちゃお

        if (ping.start) {
            _start_pings.add(ping.ping);
            if (_start_pings.size() <= 2 && 200 < ping.ping) {
                GetPing.want_ping(this, true);
                return;
            } else {
                this.start_ping_result = Math.max( Collections.min(_start_pings), ((CraftPlayer)player).getHandle().ping );
            }

        } else {
            _stop_pings.add(ping.ping);
            if (_stop_pings.size() <= 2 && 200 < ping.ping) {
                GetPing.want_ping(this, false);
                return;
            } else {
                this.stop_ping_result = Math.min( Collections.min(_stop_pings), ((CraftPlayer)player).getHandle().ping );
            }
        }

        if (this.all_green()) {
//            Bukkit.getScheduler().scheduleSyncDelayedTask(
//                TA_NoMoreLag.get_plugin(),
//                this::_finish
//            );
            this._finish();
        }
    }

    public void _finish(){
        int tick = get_tick();
        String time = calc_time(tick);

        String message = ">>> §n%1$s §rが、%2$s §rをクリアしました！ [%3$s]";
        BaseComponent[] txt = TextComponent.fromLegacyText(String.format(message ,
                player.getName(),
                ChatColor.translateAlternateColorCodes('&', name),
                time));

        //builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("count:"+count+" +timems:"+time_ms+"\nstart:"+start_time+" ping:"+start_ping_result+" stop:"+stop_time+" ping"+stop_ping_result)));
        Bukkit.getServer().getLogger().info(TextComponent.toPlainText(txt));
        for (Player op: Bukkit.getServer().getOnlinePlayers()){
            op.spigot().sendMessage(txt);
        }
    }



    public void finish(Packet packet) {
        if (!is_finished) {
            is_finished = true;

            boolean find = false;
            Packet _packet;
            for (int i = 0; i < counted_packet.size(); i++){
                _packet = counted_packet.get(i);
                if (!find && packet.equals(_packet)){
                    find = true;
                }

                if (find){
                    counted_packet.remove(i);
                    i--;
                }
            }

            if (!is_counting) {
                this.stop_time = packet.time;
                GetPing.want_ping(this, false);
            } else {
                this._finish();
            }
        }
    }
}
