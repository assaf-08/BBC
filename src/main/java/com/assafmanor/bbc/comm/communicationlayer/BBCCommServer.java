package com.assafmanor.bbc.comm.communicationlayer;

import com.assafmanor.bbc.approver.ApproverMsg;
import com.assafmanor.bbc.bbc.BBCConfig;
import com.assafmanor.bbc.bbc.BBCMetaData;
import com.assafmanor.bbc.bbc.OnRcvFirstProtocolMsgCallback;
import com.assafmanor.bbc.comm.ApproveMsg;
import com.assafmanor.bbc.comm.BBCCommGrpc;
import com.assafmanor.bbc.comm.CoinMsg;
import com.assafmanor.bbc.comm.Response;
import com.assafmanor.bbc.vrf.types.CoinMessage;
import com.assafmanor.bbc.vrf.types.VRFResult;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BBCCommServer {

    // Meta -> Round -> CoinMsg
    private final HashMap<BBCMetaData, HashMap<Integer, BlockingQueue<CoinMsg>>> coinMsgs = new HashMap<>();
    // Meta->Round->Stage->ApproveMsg
    private final HashMap<BBCMetaData, HashMap<Integer, HashMap<Integer, BlockingQueue<ApproveMsg>>>> approveMsgs = new HashMap<>();

    private final static Logger LOGGER = Logger.getLogger(BBCCommServer.class.getName());
    private final Server server;
    private final int id;


    public BBCCommServer(int id, int port, OnRcvFirstProtocolMsgCallback callback) {
        this.id = id;
        server = ServerBuilder.forPort(port).addService(new BBCService(callback)).build();

    }

    public CoinMessage popCoinMsg(BBCMetaData meta, int r) {
        HashMap<Integer, BlockingQueue<CoinMsg>> sessionMap;
        BlockingQueue<CoinMsg> roundQueue;
        LOGGER.log(Level.FINEST, "Start pop coin");
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

        CoinMsg rawMsg = null;
        try {
            rawMsg = roundQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        VRFResult vrfResult = new VRFResult(rawMsg.getVrfResult().getVrfOutput(), rawMsg.getVrfResult().getVrfOutput());
        LOGGER.log(Level.FINEST, "End Pop coin");
        return new CoinMessage(rawMsg.getTag(), vrfResult, rawMsg.getHeader().getSenderId(), rawMsg.getCommitteeData().getProof(), rawMsg.getHeader().getRound());

    }

    public ApproverMsg popApproveMsg(BBCMetaData meta, int r, int stage) {
        LOGGER.log(Level.FINEST, "Start of approver debug...");
        LOGGER.log(Level.FINEST, "Start pop approve round " + r + " Meta Channel " + meta.getChannel() + " Cid " + meta.getCid() + " CidSeries " + meta.getCidSeries());
        HashMap<Integer, HashMap<Integer, BlockingQueue<ApproveMsg>>> sessionMap;
        HashMap<Integer, BlockingQueue<ApproveMsg>> stageMap;
        BlockingQueue<ApproveMsg> msgQueue;
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
            stageMap = sessionMap.get(r);
            while (stageMap.get(stage) == null) {
                try {
                    approveMsgs.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            msgQueue = stageMap.get(stage);
        }
        ApproveMsg rawMsg = null;
        try {
            rawMsg = msgQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.FINEST, "End pop approve");
        return new ApproverMsg(rawMsg.getTag(), rawMsg.getValue(), rawMsg.getHeader().getSenderId());
    }


    private class BBCService extends BBCCommGrpc.BBCCommImplBase {
        private OnRcvFirstProtocolMsgCallback onRcvFirstProtocolMsgCallback;

        public BBCService(OnRcvFirstProtocolMsgCallback callback) {
            this.onRcvFirstProtocolMsgCallback = callback;
        }

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
            BBCMetaData meta = new BBCMetaData(request.getHeader().getMeta().getChannel(), request.getHeader().getMeta().getCid(), request.getHeader().getMeta().getCidSeries());
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
                LOGGER.log(Level.FINEST, "Recieved COIN message from " + request.getHeader().getSenderId() +
                        " Round: " + request.getHeader().getRound() + " Phase: " + request.getTag() + "\nCurrent session Coin size is: " +
                        roundQueue.size());
            }


        }

        private void addApproveMsg(ApproveMsg request) {
            BBCMetaData meta = new BBCMetaData(request.getHeader().getMeta().getChannel(), request.getHeader().getMeta().getCid(), request.getHeader().getMeta().getCidSeries());

            if (this.onRcvFirstProtocolMsgCallback != null && request.getHeader().getRound() == 0
                    && request.getStage() == BBCConfig.ApproverStage.FIRST_CALL &&
                    request.getTag().equals(BBCConfig.ApproverTags.INIT)) {
                this.onRcvFirstProtocolMsgCallback.onReceiveFirstProtocolMsg(meta, request.getHeader().getHeight());

            }

            int round = request.getHeader().getRound();
            int stage = request.getStage();
            HashMap<Integer, HashMap<Integer, BlockingQueue<ApproveMsg>>> sessionMap;
            HashMap<Integer, BlockingQueue<ApproveMsg>> stageMap;
            BlockingQueue<ApproveMsg> msgQueue;
            synchronized (approveMsgs) {
                if (!approveMsgs.containsKey(meta)) {
                    approveMsgs.put(meta, new HashMap<>());
                }
                sessionMap = approveMsgs.get(meta);
                if (!sessionMap.containsKey(round)) {
                    sessionMap.put(round, new HashMap<>());

                }
                stageMap = sessionMap.get(round);
                if (!stageMap.containsKey(stage)) {
                    stageMap.put(stage, new LinkedBlockingQueue<>());
                }
                msgQueue = stageMap.get(stage);
                msgQueue.add(request);
                approveMsgs.notifyAll();
            }
            LOGGER.log(Level.FINEST, "Recieved Approve message from " + request.getHeader().getSenderId() +
                    " Round: " + request.getHeader().getRound() + " Value: " + request.getValue() + " Phase: " + request.getTag() + "\nCurrent session approve queue size is: " +
                    msgQueue.size());

        }


    }

    public void start() {
        try {
            server.start();
        } catch (IOException e) {
            LOGGER.warning("Error while starting server. " + e.toString());
        }
    }

    public void shutdown() {
        server.shutdown();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
