package com.assafmanor.bbc.approver;

public class ApproverMsg {
    private String tag;
    private Integer value;
    private int sender;

    public ApproverMsg(String tag, Integer value, int sender) {
        this.tag = tag;
        this.value = value;
        this.sender = sender;
    }
    public void setSender(int sender){
        this.sender = sender;
    }

    public int getSender(){
        return sender;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
