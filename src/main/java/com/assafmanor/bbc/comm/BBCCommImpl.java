package com.assafmanor.bbc.comm;

import com.assafmanor.bbc.approver.ApproverMsg;
import com.assafmanor.bbc.bbc.OnRcvFirstProtocolMsgCallback;
import com.assafmanor.bbc.comm.communicationlayer.BBCCommClient;
import com.assafmanor.bbc.comm.communicationlayer.BBCCommServer;
import com.assafmanor.bbc.vrf.types.CoinMessage;
import com.assafmanor.bbc.vrf.types.VRFResult;
import com.assafmanor.bbc.bbc.BBCMetaData;

import java.io.IOException;

public class BBCCommImpl implements BBCCommContract {
    private final BBCCommClient client;
    private final BBCCommServer server;

    public BBCCommImpl(int nodeID, int serverPort, OnRcvFirstProtocolMsgCallback callback) {
        this.client = new BBCCommClient(nodeID);
        this.server = new BBCCommServer(nodeID, serverPort, callback);

    }

    @Override
    public void broadcastApproveMsg(int round, int stage, String tag, Integer value, BBCMetaData meta) {
        assert stage <= 1;
        client.broadcastApproveMsg(round, stage, tag, value, meta);
    }

    @Override
    public void broadcastCoinMsg(int round, String tag, VRFResult vrfResult, BBCMetaData meta) {
        client.broadcastCoinMsg(round, tag, vrfResult, meta);
    }

    @Override
    public CoinMessage popCoinMsg(int round, BBCMetaData meta) {
        return this.server.popCoinMsg(meta, round);
    }

    @Override
    public ApproverMsg popApproverMsg(int r, int stage, BBCMetaData meta) {
        return this.server.popApproveMsg(meta, r, stage);
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
