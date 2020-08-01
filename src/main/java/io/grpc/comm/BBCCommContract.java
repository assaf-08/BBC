package io.grpc.comm;

import approver.ApproverMsg;
import vrf.types.CoinMessage;
import vrf.types.VRFResult;

public interface BBCCommContract {
    void broadcastCoinMsg(String tag, VRFResult vrfResult);

    void broadcastApproveMsg(String tag, Integer value);


    CoinMessage popCoinMsg();

    ApproverMsg popApproverMsg();

}
