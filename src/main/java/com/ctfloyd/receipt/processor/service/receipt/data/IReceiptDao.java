package com.ctfloyd.receipt.processor.service.receipt.data;

import java.util.Optional;

/**
 * Defines the interface for a receipt data access object. An interface is chosen due to stakeholders requirements that
 * the data source may change at a later point in time.
 */
public interface IReceiptDao {

    void savePoints(String receiptId, long points);
    Optional<Long> getPointsForReceiptId(String receiptId);

}
