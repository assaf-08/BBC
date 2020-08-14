package com.assafmanor.bbc.vrf.types;

import com.assafmanor.bbc.vrf.VRFResultContract;

public class VRFResult implements VRFResultContract {
    private final String output;
    private final String proof;

    public VRFResult(String output, String proof) {
        this.output = output;
        this.proof = proof;
    }


    @Override
    public String getVRFOutput() {
        return this.output;
    }

    @Override
    public String getVRFProof() {
        return this.proof;
    }
}
