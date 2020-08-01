package vrf;

import vrf.types.VRFResult;

public class DummyVRFImpl implements VRFContract {
    @Override
    public VRFResult calculate(int r) {
        return new VRFResult("0", "1");

    }

    @Override
    public boolean verify(int x, VRFResultContract vrfResult) {
        return true;
    }

    @Override
    public void setSecretKey(String key) {

    }
}
