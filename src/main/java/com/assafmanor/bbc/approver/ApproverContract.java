package com.assafmanor.bbc.approver;

import com.assafmanor.bbc.bbc.BBCMetaData;

import java.util.Set;

public interface ApproverContract {
    Set<Integer> approve(Integer v, Integer round,Integer stage, BBCMetaData meta);
}
