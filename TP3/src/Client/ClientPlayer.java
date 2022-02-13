package Client;

import Common.Packet;
import Common.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClientPlayer extends Thread{
    private static final int FRAME_RATE = 15;
    private final List<Pair<Integer,String>> frames;

    public ClientPlayer(){
        frames = new ArrayList<>();
    }

    public synchronized void  AddFrame(Packet p){
        frames.add(new Pair<>(p.getPacketId(),p.getContent()));
    }

    private synchronized Pair<Integer,String> GetFrame(){
        frames.sort(Comparator.comparingInt(Pair::getFirst));
        return frames.size() > 0 ? frames.remove(0) : null;
    }

    public void run() {
        try{
            boolean buffering = false;
            while(true){
                Thread.sleep(1000/FRAME_RATE);
                var frame = GetFrame();
                if(frame != null) {
                    System.out.println(frame.getSecond());
                    buffering = false;
                }
                else if(!buffering){
                    System.out.println("Buffering...");
                    buffering = true;
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
