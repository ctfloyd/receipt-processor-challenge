package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.GetReceiptPointsResponse;
import com.ctfloyd.receipt.processor.service.exception.ServiceException;
import com.ctfloyd.receipt.processor.service.metrics.IMetrics;
import com.ctfloyd.receipt.processor.service.receipt.data.IReceiptDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ReceiptServiceTest {

    @Mock
    private IReceiptDao receiptDao;

    @Mock
    private IMetrics metrics;

    private ReceiptService receiptService;

    @BeforeEach
    public void setup() {
        openMocks(this);

        receiptService = new ReceiptService(
                receiptDao,
                metrics
        );
    }

    @Test
    public void givenNullReceiptId_whenGetPointsForReceipt_expectNotFoundServiceException() {
        assertThrows(
                ServiceException.class,
                () -> receiptService.getReceiptPoints(null),
                "Receipt id must be specified."
        );
        // 2 increment metrics (error and call), 1 timing metric
        verify(metrics, times(2)).increment(anyString(), anyString());
        verify(metrics, times(1)).time(anyString(), anyString(), anyLong());
    }

    @Test
    public void givenNotFoundReceiptId_whenGetPointsForReceipt_expectNotFoundServiceException() {
        when(receiptDao.getPointsForReceiptId(anyString())).thenReturn(Optional.empty());
        assertThrows(
                ServiceException.class,
                () -> receiptService.getReceiptPoints("NOT_FOUND"),
                "Could not points for find receipt matching id (NOT_FOUND)."
        );
        // 2 increment metrics (error and call), 1 timing metric
        verify(metrics, times(2)).increment(anyString(), anyString());
        verify(metrics, times(1)).time(anyString(), anyString(), anyLong());
    }

    @Test
    public void givenHappyPathReceiptId_whenGetPointsForReceipt_expectPointsResponse() {
        long points = 30;
        when(receiptDao.getPointsForReceiptId(anyString())).thenReturn(Optional.of(points));
        GetReceiptPointsResponse response = receiptService.getReceiptPoints("VALID_RECEIPT_ID");
        assertNotNull(response, "Expected get points response to be non-null.");
        assertEquals(points, response.getPoints(), "Expected points does not match returned points.");

        // 1 increment metric (call), 1 timing metric
        verify(metrics, times(1)).increment(anyString(), anyString());
        verify(metrics, times(1)).time(anyString(), anyString(), anyLong());
    }

}
