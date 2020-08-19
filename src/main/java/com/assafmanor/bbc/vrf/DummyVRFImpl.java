package com.assafmanor.bbc.vrf;

import com.assafmanor.bbc.vrf.types.VRFResult;

import java.util.Random;

import static java.lang.Integer.parseInt;

public class DummyVRFImpl implements VRFContract {

    public static String randomGenerator(String seed) {
        int seed_int = parseInt(seed);
        Random generator = new Random(seed_int);
        int num = generator.nextInt()*(-1) ;

        return String.valueOf(num);
    }

    @Override
    public VRFResult calculate(int r) {
        String generate = String.valueOf(r);
        String output = randomGenerator(generate);
        return new VRFResult(output, generate);

    }

    @Override
    public boolean verify(int x, VRFResultContract vrfResult) {
        return randomGenerator(String.valueOf(x)).equals(vrfResult.getVRFProof());
    }

    @Override
    public void setSecretKey(String key) {

    }
}
