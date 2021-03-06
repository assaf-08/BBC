package com.assafmanor.bbc.approver;

import com.assafmanor.bbc.bbc.BBCConfig;
import com.assafmanor.bbc.bbc.BBCMetaData;
import com.assafmanor.bbc.comm.BBCCommContract;
import com.assafmanor.bbc.sampler.SamplerContract;
import com.assafmanor.bbc.sampler.types.SampleResult;

import java.util.HashSet;
import java.util.Set;

public class ApproverImpl implements ApproverContract {
    private final SamplerContract sampler;
    private final BBCCommContract communicator;
    private final int nodeID;

    public ApproverImpl(SamplerContract sampler, BBCCommContract communicator, int nodeID) {
        this.sampler = sampler;
        this.communicator = communicator;
        this.nodeID = nodeID;
    }

    @Override
    public Set<Integer> approve(Integer v, Integer round, Integer stage, BBCMetaData meta) {
        HashSet<Integer> retSet = new HashSet<>();
        int[] numberOReceivedINIT = new int[3];
        int[] numberOReceivedECHO = new int[3];
        int numberOReceivedOK = 0;
        Set<Pair<Integer,Integer>> nodesSentINIT = new HashSet<>();
        Set<Pair<Integer,Integer>> nodesSentECHO = new HashSet<>();
        Set<Pair<Integer,Integer>> nodesSentOK = new HashSet<>();

        boolean sentOkMsg = false;
        SampleResult sampleResult = sampler.sample(BBCConfig.ApproverTags.INIT, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
        if (sampleResult.getResult()) {
            communicator.broadcastApproveMsg(round, stage, BBCConfig.ApproverTags.INIT, v, meta);

        }
        while (true) {
            ApproverMsg approverMsg = communicator.popApproverMsg(round, stage, meta);
            assert approverMsg.getValue() <= 2 && approverMsg.getValue() >= 0;
            // **** Init phase *** //
            Pair<Integer,Integer> senderValuePair = new Pair<Integer,Integer>(approverMsg.getSender(),approverMsg.getValue());
            if (approverMsg.getTag().equals(BBCConfig.ApproverTags.INIT) &&
                    ((sampler.committeeValidate(BBCConfig.ApproverTags.INIT, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD, this.nodeID, sampleResult.getProof())))
                && !nodesSentINIT.contains(senderValuePair)) {
                nodesSentINIT.add(senderValuePair);
                numberOReceivedINIT[approverMsg.getValue()]++;

                if (numberOReceivedINIT[approverMsg.getValue()] == BBCConfig.getNumberOfMaxByzantineNodes() + 1) {
                    String sampleTag = BBCConfig.ApproverTags.ECHO + "_" + v.toString();
                    sampleResult = sampler.sample(sampleTag, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
                    if (sampleResult.getResult()) {

                        communicator.broadcastApproveMsg(round, stage, BBCConfig.ApproverTags.ECHO, approverMsg.getValue(), meta);
                    }
                }
            }

            // **** Echo phase *** //

            if (approverMsg.getTag().equals(BBCConfig.ApproverTags.ECHO)
                    && ((sampler.committeeValidate(BBCConfig.ApproverTags.ECHO, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD, this.nodeID, sampleResult.getProof())))
                && !nodesSentECHO.contains(senderValuePair)) {
                nodesSentECHO.add(senderValuePair);
                numberOReceivedECHO[approverMsg.getValue()]++;

                if (numberOReceivedECHO[approverMsg.getValue()] == BBCConfig.getNumberOfMinCorrectNodesInCommittee() && !sentOkMsg) {
                    sampleResult = sampler.sample(BBCConfig.ApproverTags.OK, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
                    if (sampleResult.getResult()) {
                        communicator.broadcastApproveMsg(round, stage, BBCConfig.ApproverTags.OK, approverMsg.getValue(), meta);
                        sentOkMsg = true;
                    }

                }
            }

            // **** Ok phase *** //


            if (approverMsg.getTag().equals(BBCConfig.ApproverTags.OK) &&
                    ((sampler.committeeValidate(BBCConfig.ApproverTags.OK, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD, this.nodeID, sampleResult.getProof())))
                && !nodesSentOK.contains(senderValuePair)) {
                // TODO validate sender is in Committee ?
                nodesSentOK.add(senderValuePair);
                numberOReceivedOK++;

                retSet.add(approverMsg.getValue());
                if (numberOReceivedOK == BBCConfig.getNumberOfMinCorrectNodesInCommittee()) {
                    return retSet;

                }
            }


        }
    }
}
