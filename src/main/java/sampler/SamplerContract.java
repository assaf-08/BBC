package sampler;

import sampler.types.SampleResult;

public interface SamplerContract {

    public SampleResult sample(String tag, int threshold);

    public boolean committeeValidate(String tag,int threshold,int nodeID,String proof);

}
