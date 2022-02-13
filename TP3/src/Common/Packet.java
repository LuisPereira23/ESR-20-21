package Common;

import java.io.Serializable;
import java.util.List;

public class Packet implements Serializable {
    private String srcIp; // Where it comes from
    private String destIp; // Where it's going to
    private List<List<String>> paths; // Paths it will take
    private String content; // Message to be delivered, in this case an animation, could be an image
    private int packetId; // What is the id of this packet

    public Packet(String srcIp, String destIp, List<List<String>> paths, String content, int packetId) {
        this.srcIp = srcIp;
        this.destIp = destIp;
        this.paths = paths;
        this.content = content;
        this.packetId = packetId;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public List<List<String>> getPath() {
        return paths;
    }

    public void setPath(List<List<String>> paths) {
        this.paths = paths;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
