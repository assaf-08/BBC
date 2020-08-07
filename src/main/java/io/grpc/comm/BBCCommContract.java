package io.grpc.comm;

import approver.ApproverMsg;
import bbc.BBCContract;
import vrf.types.CoinMessage;
import vrf.types.VRFResult;

public interface BBCCommContract {

    // TODO add session ID for all API

    void broadcastApproveMsg(String tag, Integer value);

    void broadcastCoinMsg(int round,String tag, VRFResult vrfResult);

    CoinMessage popCoinMsg();

    ApproverMsg popApproverMsg();

//    void addNodeToBroadcastList() // TODO

}