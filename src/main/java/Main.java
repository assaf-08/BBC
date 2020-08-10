import approver.ApproverContract;
import approver.ApproverImpl;
import io.grpc.comm.BBCCommContract;
import io.grpc.comm.BBCCommImpl;
import io.grpc.comm.communicationlayer.BBCCommClient;
import io.grpc.comm.communicationlayer.BBCCommServer;
import sampler.DummySamplerImpl;
import sharedcoin.SharedCoinContract;
import sharedcoin.WHPCoinImpl;
import test.TestUtils;
import vrf.DummyVRFImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        runCoin();
//        runApprover();
//        Map<String, String> env = System.getenv();
//        assert (env.containsKey("node_id"));
//        String idStr = env.get("node_id");
//        System.out.println("Starting node " + idStr);
//        System.out.println("BBBBBB");
//        int id = Integer.parseInt(idStr);
//        BBCCommServer server = new BBCCommServer(id,8080);
//        try {
//            server.start();
//        }
//        catch (IOException e){
//            assert false;
//        }
//        List<String> hosts = new ArrayList<>();
//        hosts.add(id == 0 ? "node1" : "node0");
//        BBCCommClient client = new BBCCommClient(id,hosts,8080);
//        if(id==1) {
//            try {
//                server.blockUntilShutdown();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if(id==0){
//            client.sendMSG("node1","LLLLBla Bla");
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            client.sendMSG("node1","Bl55a Bla");
//        }
//

    }

    private static void runCoin() {

        Integer nodeID = TestUtils.getNodeId(true);
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImpl(nodeID, TestUtils.TEST_PORT);
        try {
            communicator.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        communicator.addNodeToBroadcastList(nodeID == 0 ? "node1" : "node0", TestUtils.TEST_PORT);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedCoinContract coin = new WHPCoinImpl(new DummySamplerImpl(), new DummyVRFImpl(), communicator);
        Integer res = coin.sharedCoin(0, TestUtils.createDummyMeta());
        System.out.println("Coin result: " + res.toString());
    }

    private static void runApprover() {
        Integer nodeID = TestUtils.getNodeId(true);
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImpl(nodeID, TestUtils.TEST_PORT);
        try {
            communicator.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        communicator.addNodeToBroadcastList(nodeID == 0 ? "node1" : "node0", TestUtils.TEST_PORT);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        approver.approve(0,0, TestUtils.createDummyMeta());

    }
}
