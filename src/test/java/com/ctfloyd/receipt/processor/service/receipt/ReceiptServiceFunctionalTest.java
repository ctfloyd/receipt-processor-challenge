package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.GetReceiptPointsResponse;
import com.ctfloyd.receipt.processor.service.exception.ServiceException;
import com.ctfloyd.receipt.processor.service.metrics.IMetrics;
import com.ctfloyd.receipt.processor.service.receipt.data.IReceiptDao;
import com.ctfloyd.receipt.processor.service.receipt.data.MemoryReceiptDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * The functional test is testing the integration of the service with an in-memory database. Trivial in this
 * application, but it's a good a way to ensure a database integration works locally.
 */
public class ReceiptServiceFunctionalTest {

    private static final String RECEIPT_ID = UUID.randomUUID().toString();
    private static final int POINTS = 50;

    @Mock
    private IMetrics metrics;

    private IReceiptDao receiptDao;
    private ReceiptService receiptService;

    @BeforeEach
    public void setup() {
        openMocks(this);
        receiptDao = new MemoryReceiptDao();
        receiptService = new ReceiptService(
                receiptDao,
                metrics
        );
    }

    @Test
    public void givenStoredReceiptPoints_whenGetReceiptPoints_expectValueReturned() {
        receiptDao.savePoints(RECEIPT_ID, POINTS);
        GetReceiptPointsResponse response = receiptService.getReceiptPoints(RECEIPT_ID);
        assertNotNull(response, "Expected get points response to be non-null.");
        assertEquals(POINTS, response.getPoints(), "Expected points does not match returned points.");
    }

}
