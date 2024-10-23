package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.Item;
import com.ctfloyd.receipt.processor.model.receipt.Receipt;

import java.util.regex.Pattern;

/**
 * Validates that the supplied receipt model objects match the expected schema.
 */
public class ReceiptValidator {

    private static final Pattern RETAILER_PATTERN = Pattern.compile("^[\\w\\s\\-&]+$");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d+\\.\\d{2}$");
    private static final Pattern SHORT_DESCRIPTION_PATTERN = Pattern.compile("^[\\w\\s\\-]+$");
    private static final Pattern RECEIPT_ID_PATTERN = Pattern.compile("^\\S+$");

    public static boolean isValidId(String id) {
        if (id == null) {
            return false;
        }

        return RECEIPT_ID_PATTERN.matcher(id).matches();
    }

    public static boolean isValid(Receipt receipt) {
        if (receipt == null) {
            return false;
        }

        if (receipt.getPurchaseDate() == null) {
            return false;
        }

        if (receipt.getPurchaseTime() == null) {
            return false;
        }

        if (receipt.getRetailer() == null) {
            return false;
        }

        if (!RETAILER_PATTERN.matcher(receipt.getRetailer()).matches()) {
            return false;
        }

        if (receipt.getTotal() == null) {
            return false;
        }

        if (!AMOUNT_PATTERN.matcher(receipt.getTotal()).matches()) {
            return false;
        }

        // API specification mandates a receipt has at least one item.
        if (receipt.getItems() == null || receipt.getItems().isEmpty()) {
            return false;
        }

        for (Item item : receipt.getItems())  {
            if (item.getShortDescription() == null || item.getPrice() == null) {
                return false;
            }

            if (!SHORT_DESCRIPTION_PATTERN.matcher(item.getShortDescription()).matches()) {
                return false;
            }

            if (!AMOUNT_PATTERN.matcher(item.getPrice()).matches()) {
                return false;
            }
        }

        return true;
    }

}
