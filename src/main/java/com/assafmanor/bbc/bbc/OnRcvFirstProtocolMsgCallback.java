package com.assafmanor.bbc.bbc;


public interface OnRcvFirstProtocolMsgCallback {
    void onReceiveFirstProtocolMsg(BBCMetaData meta, int height);
}
