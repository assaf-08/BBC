package com.assafmanor.bbc.vrf.types;

public class CoinMessage {
    private String tag;
    private VRFResult vrfResult;
    private int senderID;
    private String committeeProof;
    private int round;

    public CoinMessage(String tag, VRFResult vrfResult, int senderID, String committeeProof, int round) {
        this.tag = tag;
        this.vrfResult = vrfResult;
        this.senderID = senderID;
        this.committeeProof = committeeProof;
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getCommitteeProof() {
        return committeeProof;
    }

    public void setCommitteeProof(String committeeProof) {
        this.committeeProof = committeeProof;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public VRFResult getVrfResult() {
        return vrfResult;
    }

    public void setVrfResult(VRFResult vrfResult) {
        this.vrfResult = vrfResult;
    }
}
