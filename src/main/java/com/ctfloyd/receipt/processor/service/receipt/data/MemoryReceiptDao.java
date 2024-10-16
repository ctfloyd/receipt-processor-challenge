package com.ctfloyd.receipt.processor.service.receipt.data;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Accesses receipt data from memory.
 */
@Component
public class MemoryReceiptDao implements IReceiptDao {

    private final Map<String, Long> pointsByReceiptId = new ConcurrentHashMap<>();

    @Override
    public void savePoints(String receiptId, long points) {
        pointsByReceiptId.put(receiptId, points);
    }

    @Override
    public Optional<Long> getPointsForReceiptId(String receiptId) {
        return Optional.ofNullable(pointsByReceiptId.get(receiptId));
    }

}
