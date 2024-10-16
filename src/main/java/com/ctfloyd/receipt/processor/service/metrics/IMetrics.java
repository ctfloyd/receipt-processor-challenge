package com.ctfloyd.receipt.processor.service.metrics;

public interface IMetrics {

    void time(String namespace, String name, long timeInMillis);
    void increment(String namespace, String name);

}
