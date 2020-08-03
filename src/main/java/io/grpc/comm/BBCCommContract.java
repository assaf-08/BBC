package io.grpc.comm;

import approver.ApproverMsg;
import bbc.BBCContract;
import vrf.types.CoinMessage;
import vrf.types.VRFResult;

public interface BBCCommContract {

    // TODO add session ID for all API

    void broadcastApproveMsg(String tag, Integer value);

    // TODO: per round f
    void broadcastCoinMsg(String tag, VRFResult vrfResult);

    CoinMessage popCoinMsg();

    ApproverMsg popApproverMsg();

}