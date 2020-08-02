package approver;

import bbc.BBCConfig;
import io.grpc.comm.BBCCommContract;
import sampler.SamplerContract;
import sampler.types.SampleResult;

import java.lang.reflect.Array;
import java.util.HashMap;
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
    public Set<Integer> approve(Integer v) {
        HashSet<Integer> retSet = new HashSet<>();
        int[] numberOReceivedINIT = new int[2];
        int[] numberOReceivedECHO = new int[2];
        int[] numberOReceivedOK = new int[2];
        boolean sentOkMsg = false;
        SampleResult sampleResult = sampler.sample(BBCConfig.ApproverTags.INIT, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
        if (sampleResult.getResult()) {
            communicator.broadcastApproveMsg(BBCConfig.ApproverTags.INIT, nodeID);
        }
        while (true) {
            ApproverMsg approverMsg = communicator.popApproverMsg();
            assert approverMsg.getValue() < 2 && approverMsg.getValue() >= 0;

            if (approverMsg.getTag().equals(BBCConfig.ApproverTags.INIT)) {
                // TODO validate sender is in Committee ?
                numberOReceivedINIT[approverMsg.getValue()]++;

                if (numberOReceivedINIT[approverMsg.getValue()] == BBCConfig.getNumberOfMaxByzantineNodes() + 1) {
                    String sampleTag = BBCConfig.ApproverTags.ECHO + "_" + v.toString(); // TODO make better
                    sampleResult = sampler.sample(sampleTag, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
                    if (sampleResult.getResult()) {
                        communicator.broadcastApproveMsg(BBCConfig.ApproverTags.ECHO, approverMsg.getValue());
                    }
                }
            }

            if (approverMsg.getTag().equals(BBCConfig.ApproverTags.ECHO)) {
                // TODO validate sender is in Committee ?
                numberOReceivedECHO[approverMsg.getValue()]++;

                if (numberOReceivedECHO[approverMsg.getValue()] == BBCConfig.getNumberOfMinCorrectNodesInCommittee()) {
                    sampleResult = sampler.sample(BBCConfig.ApproverTags.OK, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
                    if (sampleResult.getResult() && !sentOkMsg) {
                        communicator.broadcastApproveMsg((BBCConfig.ApproverTags.OK), approverMsg.getValue());
                        sentOkMsg = true;
                    }

                }
            }

            if (approverMsg.getTag().equals(BBCConfig.ApproverTags.OK)) {
                // TODO validate sender is in Committee ?
                numberOReceivedOK[approverMsg.getValue()]++;
                retSet.add(approverMsg.getValue());
                if (numberOReceivedOK[approverMsg.getValue()] == BBCConfig.getNumberOfMinCorrectNodesInCommittee()) {
                    return retSet;

                }
            }


        }
    }
}
