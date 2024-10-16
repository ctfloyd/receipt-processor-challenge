package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.GetReceiptPointsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Defines the REST endpoints for interacting with receipt objects.
 */
@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = Objects.requireNonNull(receiptService, "ReceiptService is required.");
    }

    @GetMapping("/{receiptId}/points")
    public GetReceiptPointsResponse getReceiptPoints(@PathVariable("receiptId") String receiptId) {
        return receiptService.getReceiptPoints(receiptId);
    }
}
