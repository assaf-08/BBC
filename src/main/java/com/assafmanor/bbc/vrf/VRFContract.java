package com.assafmanor.bbc.vrf;

import com.assafmanor.bbc.vrf.types.VRFResult;

public interface VRFContract {

    VRFResult calculate(int r);

    boolean verify(int r, VRFResultContract vrfResult);

    void setSecretKey(String key);
}

