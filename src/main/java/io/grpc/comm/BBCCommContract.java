package io.grpc.comm;

import approver.ApproverMsg;
import vrf.types.CoinMessage;
import vrf.types.VRFResult;
import bbc.MetaData;

import java.io.IOException;

public interface BBCCommContract {


    void broadcastApproveMsg(int round, String tag, Integer value, MetaData meta);

    void broadcastCoinMsg(int round, String tag, VRFResult vrfResult, MetaData meta);

    CoinMessage popCoinMsg(int round, MetaData meta);

    ApproverMsg popApproverMsg(int round, MetaData meta);

    void addNodeToBroadcastList(String host, int port);

    void startServer() throws IOException;

}