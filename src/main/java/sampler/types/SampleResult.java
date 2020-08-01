package sampler.types;

public class SampleResult {
    private boolean result;
    private String proof;

    public SampleResult(boolean result, String proof) {
        this.result = result;
        this.proof = proof;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }
}
