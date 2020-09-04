package com.assafmanor.bbc.bbc;

import com.assafmanor.bbc.approver.ApproverContract;
import com.assafmanor.bbc.approver.ApproverImpl;
import com.assafmanor.bbc.comm.BBCCommContract;
import com.assafmanor.bbc.comm.BBCCommImplBuilder;
import com.assafmanor.bbc.sampler.DummySamplerImpl;
import com.assafmanor.bbc.sampler.SamplerContract;
import com.assafmanor.bbc.sharedcoin.SharedCoinContract;
import com.assafmanor.bbc.sharedcoin.WHPCoinImpl;
import com.assafmanor.bbc.vrf.DummyVRFImpl;
import com.assafmanor.bbc.vrf.VRFContract;

public class BBCBuilder {

    private BBCCommContract communicator;
    private BBCCommImplBuilder bbcCommImplBuilder;
    private OnRcvFirstProtocolMsgCallback onRcvFirstProtocolMsgCallback;
    private VRFContract vrf;
    private SamplerContract sampler;

    private int nodeID;

    public BBCBuilder(int nodeID, int bbcPort, int n, int f) {
        this.nodeID = nodeID;
        this.bbcCommImplBuilder = new BBCCommImplBuilder().setNodeID(nodeID).setServerPort(bbcPort).setOnFirstProtocolMsgCallback(null);
        this.vrf = new DummyVRFImpl();
        this.sampler = new DummySamplerImpl();

        // TODO: this is very very bad design because BBCConfig is static and thus shared between all BBC instances.
        BBCConfig.setNumberOfByzantineNodes(f);
        BBCConfig.setNumberOfNodes(n);
    }

    public BBCBuilder setCommunicator(BBCCommContract communicator) {
        this.communicator = communicator;
        return this;
    }

    public BBCBuilder setSampler(SamplerContract sampler) {
        this.sampler = sampler;
        return this;
    }

    public BBCBuilder setVRF(VRFContract vrf) {
        this.vrf = vrf;
        return this;
    }

    public BBCBuilder setOnRcvFirstProtocolMsgCallback(OnRcvFirstProtocolMsgCallback callback) {
        this.onRcvFirstProtocolMsgCallback = callback;
        this.bbcCommImplBuilder.setOnFirstProtocolMsgCallback(callback);
        return this;
    }

    public BBC build() {
        BBCCommContract bbcComm = this.communicator == null ? this.bbcCommImplBuilder.build() : this.communicator;
        SharedCoinContract sharedCoin = new WHPCoinImpl(sampler, vrf, bbcComm);
        ApproverContract approver = new ApproverImpl(sampler, bbcComm, nodeID);
        return new BBC(approver, sharedCoin, bbcComm);
    }
}