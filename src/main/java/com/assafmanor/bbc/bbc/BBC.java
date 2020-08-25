package com.assafmanor.bbc.bbc;

import com.assafmanor.bbc.approver.ApproverContract;
import com.assafmanor.bbc.sharedcoin.SharedCoinContract;

import java.util.Set;

import static com.assafmanor.bbc.bbc.BBCConfig.EMPTY_VALUE;

public class BBC {


    private final ApproverContract approver;
    private final SharedCoinContract sharedCoin;

    public BBC(ApproverContract approver, SharedCoinContract sharedCoin) {
        this.approver = approver;
        this.sharedCoin = sharedCoin;
    }


    public int propose(int v, MetaData meta) {
        int estimate = v;
        int decision = EMPTY_VALUE;
        Integer propose;
        for (int r = 0; r < BBCConfig.NUMBER_OF_ROUNDS; r++) {
            Set<Integer> vals = approver.approve(estimate, r, BBCConfig.ApproverStage.FIRST_CALL, meta);
            if (vals.size() == 1) {
                propose = vals.toArray(new Integer[0])[0];
            } else {
                propose = EMPTY_VALUE;
            }

            int c = sharedCoin.sharedCoin(r, meta);
            Set<Integer> proposals = approver.approve(propose, r, BBCConfig.ApproverStage.SECOND_CALL, meta);
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

    public void nonBlockingPropose(int v, MetaData meta,NonBlockingProposeCallback callback){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int result = propose(v,meta);
                callback.onProposeDone(result);
            }
        });
        thread.start();

    }

}
