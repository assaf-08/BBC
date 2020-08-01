package sampler;

import sampler.types.SampleResult;

public class DummySamplerImpl implements SamplerContract {
    @Override
    public SampleResult sample(String s, int i) {
        return new SampleResult(true,"0");

    }

    @Override
    public boolean committeeValidate(String tag, int threshold, int nodeID, String proof) {
        return true;
    }
}
