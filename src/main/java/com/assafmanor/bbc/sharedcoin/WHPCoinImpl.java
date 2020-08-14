package com.assafmanor.bbc.sharedcoin;

import com.assafmanor.bbc.bbc.BBCConfig;
import com.assafmanor.bbc.bbc.MetaData;
import com.assafmanor.bbc.io.grpc.comm.BBCCommContract;
import com.assafmanor.bbc.sampler.SamplerContract;
import com.assafmanor.bbc.sampler.types.SampleResult;
import com.assafmanor.bbc.vrf.VRFContract;
import com.assafmanor.bbc.vrf.types.CoinMessage;
import com.assafmanor.bbc.vrf.types.VRFResult;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class WHPCoinImpl implements SharedCoinContract {
    private final SamplerContract sampler;
    private final VRFContract vrf;
    private final BBCCommContract communicator;

    public WHPCoinImpl(SamplerContract sampler, VRFContract vrf, BBCCommContract communicator) {
        this.sampler = sampler;
        this.vrf = vrf;
        this.communicator = communicator;
    }

    @Override
    public int sharedCoin(int r, MetaData meta) {
        Set<Integer> firstSet = new HashSet<>();
        Set<Integer> secondtSet = new HashSet<>();
        VRFResult currentVrfResult = null;
        SampleResult sampleResult = sampler.sample(BBCConfig.CoinTags.COIN_FIRST_TAG, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
        if (sampleResult.getResult()) {
            currentVrfResult = vrf.calculate(r);
            communicator.broadcastCoinMsg(r, BBCConfig.CoinTags.COIN_FIRST_TAG, currentVrfResult, meta);
        }
        while (true) {
            CoinMessage coinMessage = communicator.popCoinMsg(r, meta);
            if (!isVrfResultValid(coinMessage, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD, r)) {
                continue;
            }

            if (coinMessage.getTag().equals(BBCConfig.CoinTags.COIN_FIRST_TAG)) {

                SampleResult secondSampleResult = sampler.sample(BBCConfig.CoinTags.COIN_SECOND_TAG, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
                if (secondSampleResult.getResult()) {
                    // Second committee logic
                    BigInteger Vj = new BigInteger(coinMessage.getVrfResult().getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE);
                    BigInteger Vi = currentVrfResult != null ? new BigInteger(currentVrfResult.getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE) : null;

                    if (Vi == null || Vj.compareTo(Vi) < 0) {
                        currentVrfResult = coinMessage.getVrfResult();
                    }
                    firstSet.add(coinMessage.getSenderID());
                    if (firstSet.size() == BBCConfig.getNumberOfMinCorrectNodesInCommittee()) {
                        communicator.broadcastCoinMsg(r, BBCConfig.CoinTags.COIN_SECOND_TAG, currentVrfResult, meta);
                    }

                }

            } else if (coinMessage.getTag().equals(BBCConfig.CoinTags.COIN_SECOND_TAG)) {

                BigInteger Vj = new BigInteger(coinMessage.getVrfResult().getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE);
                BigInteger Vi = currentVrfResult != null ? new BigInteger(currentVrfResult.getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE) : null;

                if (Vi == null || Vj.compareTo(Vi) < 0) {
                    currentVrfResult = coinMessage.getVrfResult();
                }
                secondtSet.add(coinMessage.getSenderID());
                if (secondtSet.size() == BBCConfig.getNumberOfMinCorrectNodesInCommittee()) {
                    BigInteger currentVrfOutput = new BigInteger(currentVrfResult.getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE);
                    return currentVrfOutput.testBit(0) ? 1 : 0; // TODO check if the this is the LSB

                }

            } else {
                // TODO throw exception.
                assert false;
                return 0;
            }

        }


    }

    private boolean isVrfResultValid(CoinMessage coinMsg, int threshold, int r) {
        assert coinMsg != null;
        assert sampler != null;
        boolean isSenderInCommittee = sampler.committeeValidate(coinMsg.getTag(), threshold, coinMsg.getSenderID(), coinMsg.getCommitteeProof());
        if (!isSenderInCommittee) {
            return false;
        }

        return vrf.verify(r, coinMsg.getVrfResult());
    }
}
