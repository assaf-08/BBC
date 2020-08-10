package test;

import bbc.MetaData;

import java.util.Map;

public class TestUtils {

    //Public
    static public final int TEST_PORT = 8080;


    // Private
    static private final String ENV_NODE_ID_KEY = "node_id";


    public static int getNodeId(boolean fail_on_error) {
        Map<String, String> env = System.getenv();
        assert !fail_on_error || (env.containsKey(ENV_NODE_ID_KEY));
        String idStr = env.get(ENV_NODE_ID_KEY);
        assert !fail_on_error || (idStr != null);
        int id = Integer.parseInt(idStr);
        assert !fail_on_error || (id >= 0);
        return id;
    }

    public static MetaData createDummyMeta(){
        return new MetaData(0,0,0);
    }
}
