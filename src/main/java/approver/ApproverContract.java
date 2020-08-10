package approver;

import bbc.MetaData;

import java.util.Set;

public interface ApproverContract {
    Set<Integer> approve(Integer v, Integer round, MetaData meta);
}
