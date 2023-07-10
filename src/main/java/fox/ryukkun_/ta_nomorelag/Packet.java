package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.events.PacketContainer;

public class Packet {
    public PacketContainer packet;
    public long time;
    public long duration;
    public Packet(PacketContainer packet, long last_time) {
        this.packet = packet;
        this.time = System.currentTimeMillis();
        if (last_time == 0) {
            this.duration = 0;
        } else{
            this.duration = this.time - last_time;
        }

    }
}
