package com.assafmanor.bbc.sampler;

import com.assafmanor.bbc.sampler.types.SampleResult;

public interface SamplerContract {

    // TODO add round.

    SampleResult sample(String tag, int threshold);

    boolean committeeValidate(String tag, int threshold, int nodeID, String proof);

}
