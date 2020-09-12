package com.assafmanor.bbc.comm.communicationlayer;

import com.assafmanor.bbc.comm.*;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import com.assafmanor.bbc.vrf.types.VRFResult;
import com.assafmanor.bbc.bbc.BBCMetaData;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BBCCommClient {
    private final int id;
    private final HashMap<String, BBCCommGrpc.BBCCommFutureStub> nonBlockingStubs;

    private final static Logger LOGGER = Logger.getLogger(BBCCommServer.class.getName());

    public BBCCommClient(int id) {
        this.id = id;
        nonBlockingStubs = new HashMap<>();

    }

    public void addNodeToBroadcastList(String host, int port) {
        Channel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        nonBlockingStubs.put(host, BBCCommGrpc.newFutureStub(channel));
    }


    public void broadcastApproveMsg(int round, int stage, String tag, Integer value, BBCMetaData meta) {
        assert stage <= 1;
        LOGGER.log(Level.FINEST, "Broadcasting approve message Round: " + round + " Tag: " + tag);
        ApproveMsg approveMsg = ApproveMsg.newBuilder().setHeader(createMsgHeader(meta, round)).setTag(tag).setValue(value).setStage(stage).build();
        nonBlockingStubs.forEach((node_addr, stub) -> {
                        ListenableFuture<Response> returnCode = stub.withWaitForReady().sendApproveMsg(approveMsg);
                }
        );
    }


    public void broadcastCoinMsg(int round, String tag, VRFResult vrfResult, BBCMetaData meta) {
        LOGGER.log(Level.FINEST, "Broadcasting coin message Round: " + round + " Tag: " + tag);
        VRFMsg vrfMsg = VRFMsg.newBuilder().setVrfOutput(vrfResult.getVRFOutput()).setVrfProof(vrfResult.getVRFProof()).build();
        CoinMsg coinMsg = CoinMsg.newBuilder().setHeader(createMsgHeader(meta, round)).setTag(tag).setVrfResult(vrfMsg).build();
        nonBlockingStubs.forEach((node_id, stub) -> {
                        ListenableFuture<Response> returnCode = stub.withWaitForReady().sendCoinMsg(coinMsg);
                }
        );
    }

    private MsgHeader createMsgHeader(BBCMetaData meta, int round) {
        Meta rawMeta = Meta.newBuilder().setChannel(meta.getChannel()).setCid(meta.getCid()).setCidSeries(meta.getCidSeries()).build();
        MsgHeader msgHeader = MsgHeader.newBuilder().setSenderId(this.id).setMeta(rawMeta).setRound(round).setHeight(meta.getHeight()).build();
        return msgHeader;
    }
}
