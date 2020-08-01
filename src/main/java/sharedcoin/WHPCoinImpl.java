package sharedcoin;

import bbc.BBCConfig;
import io.grpc.comm.BBCCommContract;
import sampler.SamplerContract;
import sampler.types.SampleResult;
import vrf.VRFContract;
import vrf.types.CoinMessage;
import vrf.types.VRFResult;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class WHPCoinImpl implements SharedCoinContract {
    private final SamplerContract sampler;
    private VRFContract vrf;
    private BBCCommContract communicator;

    public WHPCoinImpl(SamplerContract sampler) {
        this.sampler = sampler;
    }

    @Override
    public boolean sharedCoin(int r) {
        Set<Integer> firstSet = new HashSet<>();
        Set<Integer> secondtSet = new HashSet<>();
        VRFResult currentVrfResult = null;
        SampleResult sampleResult = sampler.sample(SharedCoinConfig.COIN_FIRST_TAG, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
        if (sampleResult.getResult()) {
            currentVrfResult = vrf.calculate(r);
            communicator.broadcastCoinMsg(SharedCoinConfig.COIN_FIRST_TAG, currentVrfResult);
        }
        while (true) {
            CoinMessage coinMessage = communicator.popCoinMsg();
            if (!isVrfResultValid(coinMessage, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD, r)) {
                continue;
            }
            if (coinMessage.getRound() < r) {
                // TODO: check if the is a correct behaviour
                continue;
            }
            if (coinMessage.getTag().equals(SharedCoinConfig.COIN_FIRST_TAG)) {

                SampleResult secondSampleResult = sampler.sample(SharedCoinConfig.COIN_SECOND_TAG, BBCConfig.SAMPLE_COMMITTEE_THRESHOLD);
                if (secondSampleResult.getResult()) {
                    // Second committee logic
                    BigInteger Vj = new BigInteger(coinMessage.getVrfResult().getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE);
                    BigInteger Vi = currentVrfResult != null ? new BigInteger(currentVrfResult.getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE) : null;

                    if (Vi == null || Vj.compareTo(Vi) < 0) {
                        currentVrfResult = coinMessage.getVrfResult();
                    }
                    firstSet.add(coinMessage.getSenderID());
                    if (firstSet.size() == BBCConfig.getNumberOfMinCorrectNodesInCommittee()) {
                        communicator.broadcastCoinMsg(SharedCoinConfig.COIN_SECOND_TAG, currentVrfResult);
                    }

                }

            } else if (coinMessage.getTag().equals(SharedCoinConfig.COIN_SECOND_TAG)) {

                BigInteger Vj = new BigInteger(coinMessage.getVrfResult().getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE);
                BigInteger Vi = currentVrfResult != null ? new BigInteger(currentVrfResult.getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE) : null;

                if (Vi == null || Vj.compareTo(Vi) < 0) {
                    currentVrfResult = coinMessage.getVrfResult();
                }
                secondtSet.add(coinMessage.getSenderID());
                if (secondtSet.size() == BBCConfig.getNumberOfMinCorrectNodesInCommittee()) {
                    BigInteger currentVrfOutput = new BigInteger(currentVrfResult.getVRFOutput(), BBCConfig.VRF_STRING_OUTPUT_BASE);
                    return currentVrfOutput.testBit(0); // TODO check if the this is the LSB

                }

            } else {
                // TODO throw exception.
                assert false;
                return false;
            }

        }


    }

    private boolean isVrfResultValid(CoinMessage coinMsg, int threshold, int r) {
        boolean isSenderInCommittee = sampler.committeeValidate(coinMsg.getTag(), threshold, coinMsg.getSenderID(), coinMsg.getCommitteeProof());
        if (!isSenderInCommittee) {
            return false;
        }

        return vrf.verify(r, coinMsg.getVrfResult());
    }
}
