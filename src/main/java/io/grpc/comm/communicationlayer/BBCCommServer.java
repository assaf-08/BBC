package io.grpc.comm.communicationlayer;

import approver.ApproverMsg;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.comm.*;
import io.grpc.stub.StreamObserver;
import vrf.types.CoinMessage;
import vrf.types.VRFResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import bbc.MetaData;

public class BBCCommServer {

    // Meta -> Round -> CoinMsg
    private final HashMap<MetaData, HashMap<Integer, BlockingQueue<CoinMsg>>> coinMsgs = new HashMap<>();
    private final HashMap<MetaData, HashMap<Integer, BlockingQueue<ApproveMsg>>> approveMsgs = new HashMap<>();

    private final static Logger LOGGER = Logger.getLogger(BBCCommServer.class.getName());
    private final Server server;
    private final int id;

    public BBCCommServer(int id, int port) {
        this.id = id;
        server = ServerBuilder.forPort(port).addService(new BBCService()).build();
    }

    public CoinMessage popCoinMsg(MetaData meta, int r) {
        HashMap<Integer, BlockingQueue<CoinMsg>> sessionMap;
        BlockingQueue<CoinMsg> roundQueue;
        LOGGER.log(Level.INFO,"Start pop coin");
        synchronized (coinMsgs) {
            while (coinMsgs.get(meta) == null) {
                try {
                    coinMsgs.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sessionMap = coinMsgs.get(meta);
            while (sessionMap.get(r) == null) {
                try {
                    coinMsgs.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            roundQueue = sessionMap.get(r);

        }

        CoinMsg rawMsg = roundQueue.poll();
        VRFResult vrfResult = new VRFResult(rawMsg.getVrfResult().getVrfOutput(), rawMsg.getVrfResult().getVrfOutput());
        LOGGER.log(Level.INFO,"End Pop coin");
        return new CoinMessage(rawMsg.getTag(), vrfResult, rawMsg.getHeader().getSenderId(), rawMsg.getCommitteeData().getProof(), rawMsg.getHeader().getRound());

    }

    public ApproverMsg popApproveMsg(MetaData meta, int r) {
        HashMap<Integer, BlockingQueue<ApproveMsg>> sessionMap;
        BlockingQueue<ApproveMsg> roundQueue;
        synchronized (approveMsgs) {
            while (approveMsgs.get(meta) == null) {
                try {
                    approveMsgs.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sessionMap = approveMsgs.get(meta);
            while (sessionMap.get(r) == null) {
                try {
                    approveMsgs.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            roundQueue = sessionMap.get(r);

        }
        ApproveMsg rawMsg = roundQueue.poll();

        return new ApproverMsg(rawMsg.getTag(), rawMsg.getValue());
    }


    private class BBCService extends BBCCommGrpc.BBCCommImplBase {

        @Override
        public void sendCoinMsg(CoinMsg request, StreamObserver<Response> responseObserver) {
            addCoinMsg(request);
            responseObserver.onNext(Response.newBuilder().setReturnStatus(200).build());
            responseObserver.onCompleted();
        }

        @Override
        public void sendApproveMsg(ApproveMsg request, StreamObserver<Response> responseObserver) {
            addApproveMsg(request);
            responseObserver.onNext(Response.newBuilder().setReturnStatus(200).build());
            responseObserver.onCompleted();
        }

        private void addCoinMsg(CoinMsg request) {
            MetaData meta = new MetaData(request.getHeader().getMeta().getChannel(), request.getHeader().getMeta().getCid(), request.getHeader().getMeta().getCid());
            int round = request.getHeader().getRound();
            HashMap<Integer, BlockingQueue<CoinMsg>> sessionMap;
            BlockingQueue<CoinMsg> roundQueue;
            synchronized (coinMsgs) {
                if (!coinMsgs.containsKey(meta)) {
                    coinMsgs.put(meta, new HashMap<>());
                }
                sessionMap = coinMsgs.get(meta);
                if (!sessionMap.containsKey(round)) {
                    sessionMap.put(round, new LinkedBlockingQueue<>());
                }
                roundQueue = sessionMap.get(round);
                roundQueue.add(request);
                coinMsgs.notifyAll();
            }

            System.out.println(String.valueOf(id) + ": received COIN message"); // TODO delete

        }

        private void addApproveMsg(ApproveMsg request) {
            MetaData meta = new MetaData(request.getHeader().getMeta().getChannel(), request.getHeader().getMeta().getCid(), request.getHeader().getMeta().getCid());
            int round = request.getHeader().getRound();
            HashMap<Integer, BlockingQueue<ApproveMsg>> sessionMap;
            BlockingQueue<ApproveMsg> roundQueue;
            synchronized (approveMsgs) {
                if (!approveMsgs.containsKey(meta)) {
                    approveMsgs.put(meta, new HashMap<>());
                }
                sessionMap = approveMsgs.get(meta);
                if (!sessionMap.containsKey(round)) {
                    sessionMap.put(round, new LinkedBlockingQueue<>());
                }
                roundQueue = sessionMap.get(round);
                roundQueue.add(request);
                approveMsgs.notifyAll();
            }
            System.out.println(String.valueOf(id) + ": received APPROVE message"); // TODO delete

        }


    }

    public void start() throws IOException {
        server.start();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
