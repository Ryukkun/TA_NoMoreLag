package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import fox.ryukkun_.ta_nomorelag.players.TAPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;


public class TAUnit {
    public TAPlayer ta_player;
    public String name;
    public boolean is_finished = false;
    public boolean is_counting = true;
    public int count = 0;
    public long start_time = -1;
    public ArrayList<PingData> _start_pings = new ArrayList<>();
    public int start_ping_result = -1;
    public long stop_time = -1;
    public  ArrayList<PingData> _stop_pings = new ArrayList<>();
    public int stop_ping_result = -1;
    private Packet last_packet = null;
    private boolean afk = false;

    public TAUnit(String name, TAPlayer ta_player) {
        this.name = name;
        this.ta_player = ta_player;
    }

    public void add_count(Packet packet) {
        if (this.is_counting) {
            if (45 < packet.duration && packet.duration < 60) {
                this.is_counting = false;
                this.start_time = packet.time;
                GetPing.want_ping(this, true);

            } else {

                // afk時のカウントできるだけする マイナスにはならない
                boolean _afk = false;
                if (last_packet != null) {
                    if (last_packet.type != PacketType.Play.Client.LOOK  &&  packet.type != PacketType.Play.Client.LOOK){
                        if (last_packet.x == packet.x  &&  last_packet.y == packet.y  &&  last_packet.z == packet.z) {
                            _afk = true;
                        }
                    }
                }
                if (afk || _afk) {
                    count += 19;
                    Bukkit.getServer().getLogger().info("kita! +19");
                }
                count++;
                afk = _afk;
                last_packet = packet;
            }
        }
    }

    private boolean all_green(){
        return (start_ping_result != -1 && stop_ping_result != -1 && is_finished);
    }

    private int get_tick(){
        int tick = count;
        long duration = (stop_time - (stop_ping_result / 2)) - (start_time - (start_ping_result / 2));
        tick += duration / 50;

        Bukkit.getServer().getLogger().info("count:"+count+" +timems:"+duration);
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


        Bukkit.getServer().getLogger().info("time:"+time+" start:"+start_time+" ping:"+start_ping_result+" stop:"+stop_time+" ping"+stop_ping_result);
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

    public static String int_to_string(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return Integer.valueOf(i).toString();
        }
    }


    public void set_ping(PingData ping){
        // pingが最後に得られる値だから タイム計測とかもここに書いちゃお

        if (ping.start) {
            _start_pings.add(ping);
        } else {
            _stop_pings.add(ping);
        }

        if (this.all_green()) {
            int tick = get_tick();
            String time = calc_time(tick);

            String message = ">>> &n" + ta_player.player.getName() + "&r が、" + name + "&rをクリアしました！ [" + time + "]";
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
            ta_player.running_ta.remove(name);

        }
    }

    public void finish(Packet packet) {
        if (!is_finished) {
            is_finished = true;
            this.stop_time = packet.time;
            GetPing.want_ping(this, false);
        }
    }
}
