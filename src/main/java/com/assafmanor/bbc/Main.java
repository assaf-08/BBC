package com.assafmanor.bbc;

import com.assafmanor.bbc.approver.ApproverContract;
import com.assafmanor.bbc.approver.ApproverImpl;
import com.assafmanor.bbc.bbc.BBC;
import com.assafmanor.bbc.bbc.BBCBuilder;
import com.assafmanor.bbc.bbc.BBCConfig;
import com.assafmanor.bbc.bbc.MetaData;
import com.assafmanor.bbc.comm.BBCCommContract;
import com.assafmanor.bbc.comm.BBCCommImpl;
import com.assafmanor.bbc.sampler.DummySamplerImpl;
import com.assafmanor.bbc.sharedcoin.SharedCoinContract;
import com.assafmanor.bbc.sharedcoin.WHPCoinImpl;
import com.assafmanor.bbc.test.TestUtils;
import com.assafmanor.bbc.vrf.DummyVRFImpl;

import java.io.IOException;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
//        Logger.setLevel(Logger.INFO);
        System.out.println("D = " + String.valueOf(BBCConfig.D));
        System.out.println("W = " + String.valueOf(BBCConfig.getNumberOfMinCorrectNodesInCommittee()) + " B = " + String.valueOf(BBCConfig.getNumberOfMaxByzantineNodes()) + " LAMBDA = " + String.valueOf(BBCConfig.SAMPLE_COMMITTEE_THRESHOLD));
//        runCoin();
//        runCoinTermination();
//        runApprover();
        runApproverTermination();
//        runPropose();
//        runTermiationTest();
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
//        communicator.addNodeToBroadcastList(nodeID == 0 ? "node1" : "node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);

        SharedCoinContract coin = new WHPCoinImpl(new DummySamplerImpl(), new DummyVRFImpl(), communicator);
        Integer res = coin.sharedCoin(0, TestUtils.createDummyMeta());
        System.out.println("Coin result: " + res.toString());
    }

    private static void runCoinTermination() {

        Integer nodeID = TestUtils.getNodeId(true);

        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImpl(nodeID, TestUtils.TEST_PORT);
        try {
            communicator.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
//        communicator.addNodeToBroadcastList(nodeID == 0 ? "node1" : "node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);

        SharedCoinContract coin = new WHPCoinImpl(new DummySamplerImpl(), new DummyVRFImpl(), communicator);
        for (int i = 0; i < 1000; i++) {
            Integer res = coin.sharedCoin(i, TestUtils.createDummyMeta());
            System.out.println("Coin result: " + res.toString() + " " + i);
        }

    }

    private static void runApprover() {
        Integer nodeID = TestUtils.getNodeId(true);
        if (nodeID >= 2) {
            return;
        }
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImpl(nodeID, TestUtils.TEST_PORT);
        try {
            communicator.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
//        communicator.addNodeToBroadcastList(nodeID == 0 ? "node1" : "node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        HashSet<Integer> approveResult = (HashSet<Integer>) approver.approve(0, 0, TestUtils.createDummyMeta());
        System.out.println(approveResult.toString());
    }

    private static void runApproverTermination() {
        Integer nodeID = TestUtils.getNodeId(true);

        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImpl(nodeID, TestUtils.TEST_PORT);
        try {
            communicator.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        for (int i = 0; i < 1000; i++) {
            HashSet<Integer> approveResult = (HashSet<Integer>) approver.approve(0, i, TestUtils.createDummyMeta());
            System.out.println(approveResult.toString()+" Round: "+i);
        }
    }


    private static void runPropose() {
        int[] proposals = new int[]{0, 0, 1};
        Integer nodeID = TestUtils.getNodeId(true);
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImpl(nodeID, TestUtils.TEST_PORT);
        try {
            communicator.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        SharedCoinContract coin = new WHPCoinImpl(new DummySamplerImpl(), new DummyVRFImpl(), communicator);
        BBC bbc = new BBCBuilder(nodeID, TestUtils.TEST_PORT).setCommunicator(communicator).build();
        int proposal = proposals[nodeID];
        int result = bbc.propose(proposal, TestUtils.createDummyMeta());
        System.out.println("BBC result: " + result);
        assert result == 0;
    }

    private static void runTerminationTest() {
        int[] proposals = new int[]{0, 0, 1};
        Integer nodeID = TestUtils.getNodeId(true);
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImpl(nodeID, TestUtils.TEST_PORT);
        try {
            communicator.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        SharedCoinContract coin = new WHPCoinImpl(new DummySamplerImpl(), new DummyVRFImpl(), communicator);
        BBC bbc = new BBCBuilder(nodeID, TestUtils.TEST_PORT).setCommunicator(communicator).build();
        int proposal = proposals[nodeID];
        for (int i = 0; i < 1000; i++) {
            int result = bbc.propose(proposal, new MetaData(i, i, i));
            System.out.println("Round: " + i + " BBC result: " + result);
        }

    }
}
