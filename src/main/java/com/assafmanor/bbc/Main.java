package com.assafmanor.bbc;

import com.assafmanor.bbc.approver.ApproverContract;
import com.assafmanor.bbc.approver.ApproverImpl;
import com.assafmanor.bbc.bbc.*;
import com.assafmanor.bbc.comm.BBCCommContract;
import com.assafmanor.bbc.comm.BBCCommImplBuilder;
import com.assafmanor.bbc.sampler.DummySamplerImpl;
import com.assafmanor.bbc.sharedcoin.SharedCoinContract;
import com.assafmanor.bbc.sharedcoin.WHPCoinImpl;
import com.assafmanor.bbc.test.TestUtils;
import com.assafmanor.bbc.vrf.DummyVRFImpl;

import java.util.HashSet;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
//        Logger.setLevel(Logger.INFO);
//        System.out.println("D = " + String.valueOf(BBCConfig.D));
//        System.out.println("W = " + String.valueOf(BBCConfig.getNumberOfMinCorrectNodesInCommittee()) + " B = " + String.valueOf(BBCConfig.getNumberOfMaxByzantineNodes()) + " LAMBDA = " + String.valueOf(BBCConfig.SAMPLE_COMMITTEE_THRESHOLD));

//        runCoin();
//        System.out.println("D = " + String.valueOf(BBCConfig.D));
//        System.out.println("W = " + String.valueOf(BBCConfig.getNumberOfMinCorrectNodesInCommittee()) + " B = " + String.valueOf(BBCConfig.getNumberOfMaxByzantineNodes()));
//        runCoinTermination();
//        runApprover();
        runApproverTermination();
//        runRandomApproverTermination();
//        runPropose();
//        runProposeTermination();
//        runNonBlockingPropose();


    }

    private static void runCoin() {
        BBCConfig.setNumberOfNodes(3);
        BBCConfig.setNumberOfByzantineNodes(0);
        System.out.println("W = " + String.valueOf(BBCConfig.getNumberOfMinCorrectNodesInCommittee()) + " B = " + String.valueOf(BBCConfig.getNumberOfMaxByzantineNodes()));
        Integer nodeID = TestUtils.getNodeId(true);

        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();
        communicator.startServer();
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
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();

        communicator.startServer();
//        communicator.addNodeToBroadcastList(nodeID == 0 ? "node1" : "node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);

        SharedCoinContract coin = new WHPCoinImpl(new DummySamplerImpl(), new DummyVRFImpl(), communicator);
        for (int i = 0; i < 10000; i++) {
            Integer res = coin.sharedCoin(i, TestUtils.createDummyMeta());
            System.out.println("Coin result: " + res.toString() + " " + i);
        }

    }

    private static void runApprover() {
        Integer nodeID = TestUtils.getNodeId(true);

        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();
        communicator.startServer();
//        communicator.addNodeToBroadcastList(nodeID == 0 ? "node1" : "node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        HashSet<Integer> approveResult = (HashSet<Integer>) approver.approve(0, 0, 0, TestUtils.createDummyMeta());
        System.out.println(approveResult.toString());
    }

    private static void runApproverTermination() {
        Integer nodeID = TestUtils.getNodeId(true);
        BBCConfig.setNumberOfByzantineNodes(0);
        BBCConfig.setNumberOfNodes(4);
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();
        communicator.startServer();
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node3", TestUtils.TEST_PORT);

        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        for (int i = 0; i < 10000; i++) {
            HashSet<Integer> approveResult = (HashSet<Integer>) approver.approve(1, i, 0, new BBCMetaData(0, i, 0,0));
            System.out.println(approveResult.toString() + " Round: " + i);
        }
        communicator.shutdownServer();
    }

    private static void runRandomApproverTermination() {
        Integer nodeID = TestUtils.getNodeId(true);
        BBCConfig.setNumberOfByzantineNodes(0);
        BBCConfig.setNumberOfNodes(4);

        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();
        communicator.startServer();

        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node3", TestUtils.TEST_PORT);


        Random rand = new Random();
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        for (int i = 0; i < 10000; i++) {
            HashSet<Integer> approveResult = (HashSet<Integer>) approver.approve(rand.nextInt(3), i, 0, new BBCMetaData(i, i, i,i));
            System.out.println(approveResult.toString() + " Round: " + i);
        }
    }

    private static void runRandomApproverTermination0() {
        Integer nodeID = TestUtils.getNodeId(true);
        BBCConfig.setNumberOfByzantineNodes(0);
        BBCConfig.setNumberOfNodes(4);

        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();
        communicator.startServer();

        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node3", TestUtils.TEST_PORT);


        Random rand = new Random();
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        for (int i = 0; i < 10000; i++) {
            HashSet<Integer> approveResult = (HashSet<Integer>) approver.approve(rand.nextInt(3), i, 0, new BBCMetaData(i, i, i,i));
            System.out.println(approveResult.toString() + " Round: " + i);
        }
    }

    private static void runPropose() {
        int[] proposals = new int[]{0, 0, 1};
        Integer nodeID = TestUtils.getNodeId(true);
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();
        communicator.startServer();
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
        BBC bbc = new BBCBuilder(nodeID, TestUtils.TEST_PORT, 3, 0).setCommunicator(communicator).build();
        int proposal = proposals[nodeID];
        int result = bbc.propose(proposal, TestUtils.createDummyMeta());
        System.out.println("BBC result: " + result);

    }

    private static void runProposeTermination() {
        int[] proposals = new int[]{1, 1, 1, 0};
        Integer nodeID = TestUtils.getNodeId(true);
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();
        communicator.startServer();
        communicator.addNodeToBroadcastList("node0", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node1", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node2", TestUtils.TEST_PORT);
        communicator.addNodeToBroadcastList("node3", TestUtils.TEST_PORT);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApproverContract approver = new ApproverImpl(new DummySamplerImpl(), communicator, nodeID);
        SharedCoinContract coin = new WHPCoinImpl(new DummySamplerImpl(), new DummyVRFImpl(), communicator);
        BBC bbc = new BBCBuilder(nodeID, TestUtils.TEST_PORT, 4, 1).setCommunicator(communicator).build();
        int proposal = proposals[nodeID];
        for (int i = 0; i < 100; i++) {
            int result = bbc.propose(proposal, new BBCMetaData(i, i, i,i));
            assert result == 1;
            System.out.println("BBC Round: " + i + " BBC result: " + result);
            if (i == 50 && nodeID == 2) {
                return;
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    static int nonBlockingResult = 1000;

    private static void runNonBlockingPropose() {
        int[] proposals = new int[]{0, 0, 1};
        Integer nodeID = TestUtils.getNodeId(true);
        System.out.println("Node " + nodeID.toString() + " Has started");
        BBCCommContract communicator = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(TestUtils.TEST_PORT).build();
        communicator.startServer();
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
        BBC bbc = new BBCBuilder(nodeID, TestUtils.TEST_PORT, 3, 0).setCommunicator(communicator).build();
        int proposal = proposals[nodeID];

//         bbc.propose(proposal, TestUtils.createDummyMeta());
        bbc.nonBlockingPropose(proposal, TestUtils.createDummyMeta(), new NonBlockingProposeCallback() {
            @Override
            public void onProposeDone(int bbcResult) {
                nonBlockingResult = bbcResult;
                System.out.println("BBC result: " + nonBlockingResult);

            }
        });


    }


}
