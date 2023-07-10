package fox.ryukkun_.ta_nomorelag;

import fox.ryukkun_.ta_nomorelag.players.TAPlayer;

public class TAUnit {
    public TAPlayer ta_player;
    public String name;
    public int phase = 0;
    public int count = 0;
    public long start_time = -1;
    public int start_ping = -1;
    public long stop_time = -1;
    public int stop_ping = -1;

    public TAUnit(String name, TAPlayer ta_player) {
        this.name = name;
        this.ta_player = ta_player;
    }

    public void add_count(Packet packet) {
        if (this.phase == 0) {
            if (40 < packet.duration && packet.duration < 60) {
                this.phase++;
                this.start_time = packet.time;
                GetPing.want_ping(this);
            } else {
                this.count++;
            }
        }
    }
}
