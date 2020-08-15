package com.assafmanor.bbc.bbc;

import com.assafmanor.bbc.approver.ApproverContract;
import com.assafmanor.bbc.approver.ApproverImpl;
import com.assafmanor.bbc.comm.BBCCommContract;
import com.assafmanor.bbc.comm.BBCCommImpl;
import com.assafmanor.bbc.sampler.DummySamplerImpl;
import com.assafmanor.bbc.sampler.SamplerContract;
import com.assafmanor.bbc.sharedcoin.SharedCoinContract;
import com.assafmanor.bbc.sharedcoin.WHPCoinImpl;
import com.assafmanor.bbc.vrf.DummyVRFImpl;
import com.assafmanor.bbc.vrf.VRFContract;

public class BBCBuilder {

    private BBCCommContract communicator;
    private VRFContract vrf;
    private SamplerContract sampler;

    private int nodeID;

    public BBCBuilder(int nodeID,int bbcPort) {
        this.nodeID = nodeID;
        this.communicator=new BBCCommImpl(nodeID,bbcPort);
        this.vrf= new DummyVRFImpl();
        this.sampler=new DummySamplerImpl();
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


    public BBC createBBCImpl() {
        SharedCoinContract sharedCoin=new WHPCoinImpl(sampler,vrf,communicator);
        ApproverContract approver = new ApproverImpl(sampler,communicator,nodeID);
        return new BBC(approver, sharedCoin);
    }
}