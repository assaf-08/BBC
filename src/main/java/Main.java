import io.grpc.comm.BBCCommClient;
import io.grpc.comm.BBCCommServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Map<String, String> env = System.getenv();
        assert (env.containsKey("node_id"));
        String idStr = env.get("node_id");
        System.out.println("Starting node " + idStr);
        System.out.println("BBBBBB");
        int id = Integer.parseInt(idStr);
        BBCCommServer server = new BBCCommServer(id,8080);
        try {
            server.start();
        }
        catch (IOException e){
            assert false;
        }
        List<String> hosts = new ArrayList<>();
        hosts.add(id == 0 ? "node1" : "node0");
        BBCCommClient client = new BBCCommClient(id,hosts,8080);
        if(id==1) {
            try {
                server.blockUntilShutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(id==0){
            client.sendMSG("node1","LLLLBla Bla");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.sendMSG("node1","Bl55a Bla");
        }


    }
}
