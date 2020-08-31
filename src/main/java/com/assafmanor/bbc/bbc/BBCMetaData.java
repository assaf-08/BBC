package com.assafmanor.bbc.bbc;

import java.util.Objects;

public class BBCMetaData {
    private final int channel;
    private final int cid;
    private final int cidSeries;

    public BBCMetaData(int channel, int cid, int cidSeries) {
        this.channel = channel;
        this.cid = cid;
        this.cidSeries = cidSeries;
    }

    public int getChannel() {
        return channel;
    }

    public int getCid() {
        return cid;
    }

    public int getCidSeries() {
        return cidSeries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BBCMetaData metaData = (BBCMetaData) o;
        return channel == metaData.channel &&
                cid == metaData.cid &&
                cidSeries == metaData.cidSeries;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, cid, cidSeries);
    }
}
