package fox.ryukkun_.ta_nomorelag;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;

public class Packet {
    public PacketContainer packet;
    public double x;
    public double y;
    public double z;
    public PacketType type;
    public long time;
    public long duration;
    public Packet(PacketContainer packet, long last_time) {
        this.packet = packet;
        this.time = System.currentTimeMillis();
        this.type = packet.getType();
        if (this.type != PacketType.Play.Client.LOOK){
            this.x = packet.getDoubles().read(0);
            this.y = packet.getDoubles().read(1);
            this.z = packet.getDoubles().read(2);
        }
        if (last_time == 0) {
            this.duration = 0;
        } else{
            this.duration = this.time - last_time;
        }
        if (duration < 10 && !packet.getType().equals(PacketType.Play.Client.LOOK)){
            Bukkit.getServer().getLogger().info("x:"+this.x + " y:"+this.y + " z:"+this.z);
        }
    }
}
