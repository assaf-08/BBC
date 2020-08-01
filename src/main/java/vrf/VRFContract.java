package vrf;

import vrf.types.VRFResult;

public interface VRFContract {

    VRFResult calculate(int r);

    boolean verify(int r, VRFResultContract vrfResult);

    void setSecretKey(String key);
}

