package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.Item;
import com.ctfloyd.receipt.processor.model.receipt.Receipt;
import jakarta.annotation.Nonnull;

import java.time.LocalTime;

/**
 * The business logic for scoring receipts.
 */
public class Receipts {

    private static final int TOTAL_AMOUNT_IS_ROUND = 50;
    private static final int TOTAL_AMOUNT_IS_MULTIPLE_OF_QUARTERS = 25;
    private static final int EVERY_TWO_ITEMS = 5;
    private static final int PURCHASE_DATE_IS_ODD = 6;

    private static final int ITEM_DESCRIPTION_MULTIPLE_FACTOR = 3;
    private static final double ITEM_DESCRIPTION_PRICE_MULTIPLIER = 0.2;

    private static final LocalTime PURCHASE_TIME_WINDOW_START = LocalTime.of(14, 0); // 2:00 PM
    private static final LocalTime PURCHASE_TIME_WINDOW_END = LocalTime.of(16, 0);  // 4:00 PM
    private static final int PURCHASE_TIME_WITHIN_WINDOW = 10;

    public static int scoreReceipt(Receipt receipt) {
        if (receipt == null) {
            return 0;
        }

        int points = scoreAlphaNumericCharacters(receipt);
        points += scoreTotalAmountIsRound(receipt);
        points += scoreTotalAmountIsMultipleOfQuarters(receipt);
        points += scoreNumberOfItems(receipt);
        points += scoreItemDescription(receipt);
        points += scorePurchaseDate(receipt);
        points += scorePurchaseTime(receipt);
        return points;
    }

    public static int scoreAlphaNumericCharacters(@Nonnull Receipt receipt) {
        String retailer = receipt.getRetailer();
        if (retailer == null) {
            return 0;
        }

        int sum = 0;
        for (int i = 0; i < retailer.length(); i++) {
            char ch = retailer.charAt(i);
            if (Character.isAlphabetic(ch) || Character.isDigit(ch)) {
                sum++;
            }
        }
        return sum;
    }

    public static int scoreTotalAmountIsRound(@Nonnull Receipt receipt) {
        String strTotal = receipt.getTotal();
        if (strTotal == null) {
            return 0;
        }

        try {
            double total = Double.parseDouble(strTotal);
            if (Double.isNaN(total) || Double.isInfinite(total)) {
                return 0;
            }

            return total == (int) total ? TOTAL_AMOUNT_IS_ROUND : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static int scoreTotalAmountIsMultipleOfQuarters(@Nonnull Receipt receipt) {
        String strTotal = receipt.getTotal();
        if (strTotal == null) {
            return 0;
        }

        try {
            double total = Double.parseDouble(strTotal);
            return total % 0.25 == 0 ? TOTAL_AMOUNT_IS_MULTIPLE_OF_QUARTERS : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static int scoreNumberOfItems(@Nonnull Receipt receipt) {
        if (receipt.getItems() == null) {
            return 0;
        }

        return (receipt.getItems().size() / 2) * EVERY_TWO_ITEMS;
    }

    public static int scoreItemDescription(@Nonnull Receipt receipt) {
        if (receipt.getItems() == null) {
            return 0;
        }

        int sum = 0;
        for (Item item : receipt.getItems()) {
            if (item.getShortDescription() == null || item.getPrice() == null) {
                continue;
            }

            String description = item.getShortDescription();
            if (description == null) {
                continue;
            }

            String trimmedDescription = description.trim();
            if (trimmedDescription.length() % ITEM_DESCRIPTION_MULTIPLE_FACTOR == 0) {
                try {
                    sum += (int) Math.ceil(Double.parseDouble(item.getPrice()) * ITEM_DESCRIPTION_PRICE_MULTIPLIER);
                } catch (NumberFormatException ex) {
                    // Just swallow this error, could not convert price from String to Double.
                    // Null pointer exception being thrown is impossible as the null value is checked above.
                }

            }
        }
        return sum;
    }

    public static int scorePurchaseDate(@Nonnull Receipt receipt) {
        if (receipt.getPurchaseDate() == null) {
            return 0;
        }

        int dayOfMonth = receipt.getPurchaseDate().getDayOfMonth();
        return dayOfMonth % 2 == 1 ? PURCHASE_DATE_IS_ODD : 0;
    }

    public static int scorePurchaseTime(@Nonnull Receipt receipt) {
        LocalTime purchaseTime = receipt.getPurchaseTime();
        if (purchaseTime == null) {
            return 0;
        }


        if (purchaseTime.isAfter(PURCHASE_TIME_WINDOW_START) && purchaseTime.isBefore(PURCHASE_TIME_WINDOW_END)) {
            return PURCHASE_TIME_WITHIN_WINDOW;
        } else {
            return 0;
        }
    }

}
