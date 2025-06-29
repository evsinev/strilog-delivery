package com.strilog.delivery.receiver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PathParameters {

    private static final Logger LOG = LoggerFactory.getLogger(PathParameters.class);

    private final List<String> params;
    private final String       uri;

    public PathParameters(String aUri) {
        uri = aUri;
        List<String>    list = new ArrayList<>();
        StringTokenizer st   = new StringTokenizer(aUri, "/");
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        params = list;
    }

    public String getLast() {
        return getFromEnd(0);
    }

    public String getFromEnd(int aPosition, String aDefault) {
        try {
            return params.get(params.size() - 1 - aPosition);
        } catch (Exception e) {
            LOG.error("Cannot get {} position", aPosition, e);
            return aDefault;
        }
    }

    public String getFromEnd(int aPosition) {
        try {
            return params.get(params.size() - 1 - aPosition);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to get parameter -" + aPosition + " from " + uri + " " + params, e);
        }
    }

    public long getLastLong() {
        return Long.parseLong(getLast());
    }
}