package com.assafmanor.bbc.comm;

import com.assafmanor.bbc.approver.ApproverMsg;
import com.assafmanor.bbc.vrf.types.CoinMessage;
import com.assafmanor.bbc.vrf.types.VRFResult;
import com.assafmanor.bbc.bbc.MetaData;

import java.io.IOException;

public interface BBCCommContract {


    void broadcastApproveMsg(int round,int stage, String tag, Integer value, MetaData meta, int sender);

    void broadcastCoinMsg(int round, String tag, VRFResult vrfResult, MetaData meta);

    CoinMessage popCoinMsg(int round, MetaData meta);

    ApproverMsg popApproverMsg(int round,int stage, MetaData meta);

    void addNodeToBroadcastList(String host, int port);

    void startServer() throws IOException;

}