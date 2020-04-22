package sampler;

public class DummySamplerImpl implements SamplerContract {
    @Override
    public boolean sample(String s,int i) {
        return true;
    }
}
