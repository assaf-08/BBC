package bbc;

import approver.ApproverContract;
import sharedcoin.SharedCoinContract;

import java.util.Set;
import java.util.logging.Logger;

import static bbc.BBCConfig.EMPTY_VALUE;

public class BBCImpl implements BBCContract {


    private final ApproverContract approver;
    private final SharedCoinContract sharedCoin;

    public BBCImpl(ApproverContract approver, SharedCoinContract sharedCoin) {
        this.approver = approver;
        this.sharedCoin = sharedCoin;
    }

    @Override
    public int propose(int v, MetaData meta) {
        int estimate = v;
        int decision = EMPTY_VALUE;
        Integer propose;
        for (int r = 0; r < BBCConfig.NUMBER_OF_ROUNDS; r++) {
            Set<Integer> vals = approver.approve(estimate, r, meta);
            if (vals.size() == 1) {
                propose = vals.toArray(new Integer[0])[0];
            } else {
                propose = EMPTY_VALUE;
            }

            int c = sharedCoin.sharedCoin(r, meta);
            Set<Integer> proposals = approver.approve(propose, r, meta);
            if (proposals.size() == 1) {
                Integer proposalsOnlyElement = proposals.toArray(new Integer[0])[0];
                if (proposalsOnlyElement != EMPTY_VALUE) {
                    estimate = proposalsOnlyElement;

                    if (decision == EMPTY_VALUE) {
                        decision = proposalsOnlyElement;
                    }
                } else { // Proposal only element is empty
                    estimate = c;
                }

            } else {
                for (Integer p : proposals) {
                    if (p != EMPTY_VALUE) {
                        estimate = p;
                        break;
                    }

                }
            }


        }
        return decision;
    }
}
