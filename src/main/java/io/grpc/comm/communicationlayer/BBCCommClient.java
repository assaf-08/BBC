package io.grpc.comm.communicationlayer;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.comm.*;
import io.grpc.comm.BBCCommGrpc.BBCCommBlockingStub;
import vrf.types.VRFResult;
import bbc.MetaData;

import java.util.HashMap;

public class BBCCommClient {
    private final int id;
    private final HashMap<String, BBCCommBlockingStub> blockingStubs;

    public BBCCommClient(int id) {
        this.id = id;
        blockingStubs = new HashMap<>();

    }

    public void addNodeToBroadcastList(String host, int port) {
        Channel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        blockingStubs.put(host, BBCCommGrpc.newBlockingStub(channel));
    }


    public void broadcastApproveMsg(int round, String tag, Integer value, MetaData meta) {
        ApproveMsg approveMsg = ApproveMsg.newBuilder().setHeader(createMsgHeader(meta, round)).setTag(tag).setValue(value).build();
        blockingStubs.forEach((node_id, stub) -> {
                    try {
                        Response returnCode = stub.sendApproveMsg(approveMsg);
                    } catch (StatusRuntimeException e) {
                        assert (false);
                    }
                }
        );
    }


    public void broadcastCoinMsg(int round, String tag, VRFResult vrfResult, MetaData meta) {
        VRFMsg vrfMsg = VRFMsg.newBuilder().setVrfOutput(vrfResult.getVRFOutput()).setVrfProof(vrfResult.getVRFProof()).build();
        CoinMsg coinMsg = CoinMsg.newBuilder().setHeader(createMsgHeader(meta, round)).setTag(tag).setVrfResult(vrfMsg).build();
        blockingStubs.forEach((node_id, stub) -> {
                    try {
                        Response returnCode = stub.sendCoinMsg(coinMsg);
                    } catch (StatusRuntimeException e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
        );
    }

    private MsgHeader createMsgHeader(MetaData meta, int round) {
        Meta rawMeta = Meta.newBuilder().setChannel(meta.getChannel()).setCid(meta.getCid()).setCidSeries(meta.getCidSeries()).build();
        MsgHeader msgHeader = MsgHeader.newBuilder().setSenderId(this.id).setMeta(rawMeta).build();
        return msgHeader;
    }
}
