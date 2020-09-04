package com.assafmanor.bbc.comm;

import com.assafmanor.bbc.approver.ApproverMsg;
import com.assafmanor.bbc.vrf.types.CoinMessage;
import com.assafmanor.bbc.vrf.types.VRFResult;
import com.assafmanor.bbc.bbc.BBCMetaData;

import java.io.IOException;

public interface BBCCommContract {


    void broadcastApproveMsg(int round, int stage, String tag, Integer value, BBCMetaData meta);

    void broadcastCoinMsg(int round, String tag, VRFResult vrfResult, BBCMetaData meta);

    CoinMessage popCoinMsg(int round, BBCMetaData meta);

    ApproverMsg popApproverMsg(int round, int stage, BBCMetaData meta);

    void addNodeToBroadcastList(String host, int port);

    void startServer();

    void shutdownServer();

}