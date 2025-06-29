package com.strilog.delivery.core;

public class ElapsedTime {

    private final long started;

    public ElapsedTime() {
        started = System.nanoTime();
    }

    public long getElapsedMillis() {
        return (System.nanoTime() - started) / 1_000_000;
    }
}
