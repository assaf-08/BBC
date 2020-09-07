package com.assafmanor.bbc.bbc;


public class BBCConfig {

    private static int f = 0;
    private static int n = 1;

    public static int SAMPLE_COMMITTEE_THRESHOLD = 2; // This is lambda
    public static final double EPSILON = 1.0 / 10.0;
    public static final double D = (Math.max((1.0 / SAMPLE_COMMITTEE_THRESHOLD), 0.0362) + ((EPSILON / 3.0) - (float) (1.0 / (3.0 * SAMPLE_COMMITTEE_THRESHOLD)))) / 2.0; // for choosing d between two values.
    public static int NUMBER_OF_ROUNDS = 50;

    public static final int VRF_STRING_OUTPUT_BASE = 10;

    public static final int EMPTY_VALUE = 2;

    public static class CoinTags {
        public static final String COIN_FIRST_TAG = "COIN_FIRST";
        public static final String COIN_SECOND_TAG = "COIN_SECOND";
    }

    public static class ApproverTags {
        public static final String INIT = "INIT";
        public static final String ECHO = "ECHO";
        public static final String OK = "OK";
    }

    public static class ApproverStage {
        public static int FIRST_CALL = 0;
        public static int SECOND_CALL = 1;
    }

    // This is W
    public static int getNumberOfMinCorrectNodesInCommittee() {
        return n - f; // TODO: this is temporary because currently committee is all nodes
//        int min_correct_node = (int) Math.ceil(((2.0 / 3.0) + 3 * D) * SAMPLE_COMMITTEE_THRESHOLD);
//        return min_correct_node;
    }

    // This is B
    public static int getNumberOfMaxByzantineNodes() {
        return f; // TODO: this is temporary because currently committee is all nodes
//        double lambad_coefficient = 1.0 / 3.0 - D;
//        int max_bizantine_node = (int) (lambad_coefficient * SAMPLE_COMMITTEE_THRESHOLD);
//        return max_bizantine_node;
    }

    public static void setNumberOfByzantineNodes(int numberOfByzantineNodes) {
        f = numberOfByzantineNodes;
    }

    public static void setNumberOfNodes(int numberOfNodes) {
        n = numberOfNodes;
        SAMPLE_COMMITTEE_THRESHOLD = numberOfNodes; // TODO: this is temporary because currently committee is all nodes
    }

    public static void setNumberOfRounds(int rounds) {
        NUMBER_OF_ROUNDS = rounds;
    }
}
