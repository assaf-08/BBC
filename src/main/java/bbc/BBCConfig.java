package bbc;


public class BBCConfig {

    public static final int SAMPLE_COMMITTEE_THRESHOLD = 1; // this is lambda
    public static final double EPSILON = 1/3;
    public static final double D = (Math.max((1.0/SAMPLE_COMMITTEE_THRESHOLD), 0.0362) + ((EPSILON/3)-(float)(1/(3*SAMPLE_COMMITTEE_THRESHOLD))))/2; // for choosing d between two values.
    public static final int NUMBER_OF_ROUNDS = 100;

    public static final int VRF_STRING_OUTPUT_BASE = 10;


    public static final int EMPTY_VALUE = 2;

    public static class ApproverTags {
        public static final String INIT = "INIT";
        public static final String ECHO = "ECHO";
        public static final String OK = "OK";
    }

    // This is W
    public static int getNumberOfMinCorrectNodesInCommittee() {
        int min_correct_node = (int) Math.ceil(((2/3) + 3*D)* SAMPLE_COMMITTEE_THRESHOLD);
        return min_correct_node;
    }

    // This is B
    public static int getNumberOfMaxByzantineNodes() {
        int max_bizantine_node = (int) Math.floor(((1/3) - D) * SAMPLE_COMMITTEE_THRESHOLD);
        return max_bizantine_node;
    }

}
