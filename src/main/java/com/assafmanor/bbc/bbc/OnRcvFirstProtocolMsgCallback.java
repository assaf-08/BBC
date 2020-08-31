package com.assafmanor.bbc.bbc;


public interface OnRcvFirstProtocolMsgCallback {
    void onReceiveFirstProtocolMsg(MetaData meta, int height);
}
