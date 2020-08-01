package bbc;

public class BBCConfig {

    public static final int VRF_STRING_OUTPUT_BASE = 10;
    public static final int SAMPLE_COMMITTEE_THRESHOLD = 1;

    public static class ApproverTags {
        public static final String INIT = "INIT";
        public static final String ECHO = "ECHO";
        public static final String OK = "OK";
    }

    // This is W
    public static int getNumberOfMinCorrectNodesInCommittee() {
        // TODO implement
        return 1;
    }

    // This is B
    public static int getNumberOfMaxByzantineNodes() {
        // TODO implement
        return 0;
    }

}
