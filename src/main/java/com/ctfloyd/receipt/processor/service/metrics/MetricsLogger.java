package com.ctfloyd.receipt.processor.service.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Logs any metrics that are received through the service's configured logging implementation.
 */
@Component("metrics")
public class MetricsLogger implements IMetrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void time(String namespace, String name, long timeInMillis) {
        LOGGER.info("{}-{} | {}", namespace, name, timeInMillis);
    }

    @Override
    public void increment(String namespace, String name) {
        LOGGER.info("{}-{} | 1", namespace, name);
    }
}
