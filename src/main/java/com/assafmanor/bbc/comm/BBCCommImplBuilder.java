package com.assafmanor.bbc.comm;

import com.assafmanor.bbc.bbc.OnRcvFirstProtocolMsgCallback;

public class BBCCommImplBuilder {
    private int nodeID;
    private int serverPort;
    private OnRcvFirstProtocolMsgCallback callback;

    public BBCCommImplBuilder setNodeID(int nodeID) {
        this.nodeID = nodeID;
        return this;
    }

    public BBCCommImplBuilder setServerPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public BBCCommImplBuilder setOnFirstProtocolMsgCallback(OnRcvFirstProtocolMsgCallback callback) {
        this.callback = callback;
        return this;
    }

    public BBCCommImpl build() {
        return new BBCCommImpl(nodeID, serverPort, callback);
    }
}