package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.GetReceiptPointsResponse;
import com.ctfloyd.receipt.processor.model.receipt.ProcessReceiptResponse;
import com.ctfloyd.receipt.processor.model.receipt.Receipt;
import com.ctfloyd.receipt.processor.service.exception.ErrorCode;
import com.ctfloyd.receipt.processor.service.exception.ServiceException;
import com.ctfloyd.receipt.processor.service.metrics.IMetrics;
import com.ctfloyd.receipt.processor.service.receipt.data.IReceiptDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines the business and orchestration logic for processing receipts and persisting receipt point data.
 */
@Component
public class ReceiptService {

    private static final Class<?> CLAZZ = MethodHandles.lookup().lookupClass();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLAZZ);
    private static final String METRICS_NAMESPACE = CLAZZ.getSimpleName();

    private final IReceiptDao receiptDao;
    private final IMetrics metrics;

    public ReceiptService(IReceiptDao receiptDao, IMetrics metrics)  {
        this.receiptDao = Objects.requireNonNull(receiptDao, "ReceiptDao is required.");
        this.metrics = Objects.requireNonNull(metrics, "Metrics is required.");
    }

    public ProcessReceiptResponse processReceipt(Receipt receipt) {
        long start = System.currentTimeMillis();
        try {
            if (receipt == null) {
                String message = "Receipt must not be null.";
                LOGGER.warn(message);
                metrics.increment(METRICS_NAMESPACE, "NullReceipt.processReceipt");
                throw new ServiceException(ErrorCode.INVALID_INPUT, message);
            }

            String receiptId = UUID.randomUUID().toString();
            int points = Receipts.scoreReceipt(receipt);
            receiptDao.savePoints(receiptId, points);

            return new ProcessReceiptResponse.Builder()
                    .withId(receiptId)
                    .build();
        } catch (ServiceException ex) {
            throw ex; // already handled exception
        } catch (Exception ex) {
            String message = "An unexpected error occurred while process the receipt. %s";
            message = String.format(message, ex.getMessage());
            LOGGER.error(message, ex);
            metrics.increment(METRICS_NAMESPACE, "GenericError.processReceipt");
            throw new ServiceException(ErrorCode.GENERIC_ERROR, message);
        } finally {
            long end = System.currentTimeMillis();
            metrics.time(METRICS_NAMESPACE, "Time.processReceipt", end - start);
            metrics.increment(METRICS_NAMESPACE, "Call.processReceipt");
        }
    }

    public GetReceiptPointsResponse getReceiptPoints(String receiptId) {
        long start = System.currentTimeMillis();
        try {
            if (receiptId == null) {
                // TODO: Do more strict validation of the receipt id. The regex is given in the API specification.
                String message = "Receipt id must be specified.";
                LOGGER.warn(message);
                metrics.increment(METRICS_NAMESPACE, "InvalidReceiptId.getReceiptPoints");
                // The API specification demands only 404s are thrown from the input instead of 400s.
                throw new ServiceException(ErrorCode.NOT_FOUND, message);
            }

            Optional<Long> optionalPoints = receiptDao.getPointsForReceiptId(receiptId);
            if (optionalPoints.isPresent()) {
                return new GetReceiptPointsResponse.Builder()
                        .withPoints(optionalPoints.get())
                        .build();
            } else {
                String message = "Could not points for find receipt matching id (%s).";
                message = String.format(message, receiptId, Locale.ROOT);
                LOGGER.warn(message);
                metrics.increment(METRICS_NAMESPACE, "NotFound.getReceiptPoints");
                throw new ServiceException(ErrorCode.NOT_FOUND, message);
            }
        } catch (ServiceException ex) {
            throw ex; // already handled exception
        } catch (Exception ex) {
            String message = "An unexpected error occurred while getting points for receipt id (%s).";
            message = String.format(message, ex.getMessage());
            LOGGER.error(message, ex);
            metrics.increment(METRICS_NAMESPACE, "GenericError.getReceiptPoints");
            throw new ServiceException(ErrorCode.GENERIC_ERROR, message);
        } finally {
            long end = System.currentTimeMillis();
            metrics.time(METRICS_NAMESPACE, "Time.getReceiptPoints", end - start);
            metrics.increment(METRICS_NAMESPACE, "Call.getReceiptPoints");
        }
    }
}
