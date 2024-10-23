package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.GetReceiptPointsResponse;
import com.ctfloyd.receipt.processor.model.receipt.Item;
import com.ctfloyd.receipt.processor.model.receipt.ProcessReceiptResponse;
import com.ctfloyd.receipt.processor.model.receipt.Receipt;
import com.ctfloyd.receipt.processor.service.exception.ServiceException;
import com.ctfloyd.receipt.processor.service.metrics.IMetrics;
import com.ctfloyd.receipt.processor.service.receipt.data.IReceiptDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Test
    public void givenNullReceipt_whenProcessReceipt_expectInvalidInputError() {
        assertThrows(
                ServiceException.class,
                () -> receiptService.processReceipt(null),
                "Receipt must not be null."
        );
        // 2 increment metrics (error and call), 1 timing metric
        verify(metrics, times(2)).increment(anyString(), anyString());
        verify(metrics, times(1)).time(anyString(), anyString(), anyLong());
    }

    @Test
    public void givenValidReceipt_whenProcessReceipt_expectProcessReceiptResponse() {
        Receipt receipt = new Receipt.Builder()
                .withRetailer("M&M Corner Market")
                .withPurchaseDate(LocalDate.of(2022, 3, 20))
                .withPurchaseTime(LocalTime.of(14, 33))
                .withItem(buildItem("Gatorade", "2.25"))
                .withItem(buildItem("Gatorade", "2.25"))
                .withItem(buildItem("Gatorade", "2.25"))
                .withItem(buildItem("Gatorade", "2.25"))
                .withTotal("9.00")
                .build();

        ProcessReceiptResponse response = receiptService.processReceipt(receipt);
        assertNotNull(response, "Expected get points response to be non-null.");
        assertNotNull(response.getId(), "Expected response to have an id");

        // 1 increment metric (call), 1 timing metric
        verify(metrics, times(1)).increment(anyString(), anyString());
        verify(metrics, times(1)).time(anyString(), anyString(), anyLong());
    }

    private Item buildItem(String description, String price) {
        return new Item.Builder()
                .withShortDescription(description)
                .withPrice(price)
                .build();
    }

}
