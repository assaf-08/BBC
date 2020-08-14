package com.assafmanor.bbc.io.grpc.comm;

import com.assafmanor.bbc.approver.ApproverMsg;
import com.assafmanor.bbc.io.grpc.comm.communicationlayer.BBCCommClient;
import com.assafmanor.bbc.io.grpc.comm.communicationlayer.BBCCommServer;
import com.assafmanor.bbc.vrf.types.CoinMessage;
import com.assafmanor.bbc.vrf.types.VRFResult;
import com.assafmanor.bbc.bbc.MetaData;

import java.io.IOException;

public class BBCCommImpl implements BBCCommContract {
    private final BBCCommClient client;
    private final BBCCommServer server;

    public BBCCommImpl(int nodeID, int serverPort) {
        this.client = new BBCCommClient(nodeID);
        this.server = new BBCCommServer(nodeID, serverPort);

    }

    @Override
    public void broadcastApproveMsg(int round, String tag, Integer value, MetaData meta) {
        client.broadcastApproveMsg(round, tag, value, meta);
    }

    @Override
    public void broadcastCoinMsg(int round, String tag, VRFResult vrfResult, MetaData meta) {
        client.broadcastCoinMsg(round, tag, vrfResult, meta);
    }

    @Override
    public CoinMessage popCoinMsg(int round, MetaData meta) {
        return this.server.popCoinMsg(meta, round);
    }

    @Override
    public ApproverMsg popApproverMsg(int r, MetaData meta) {
        return this.server.popApproveMsg(meta, r);
    }

    @Override
    public void addNodeToBroadcastList(String host, int port) {
        this.client.addNodeToBroadcastList(host, port);
    }

    @Override
    public void startServer() throws IOException {
        this.server.start();
    }
}
