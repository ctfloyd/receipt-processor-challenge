package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.Item;
import com.ctfloyd.receipt.processor.model.receipt.Receipt;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ReceiptsTest {

    @Test
    public void scoreAlphaNumericCharacters_givenNullRetailer_expectZero() {
        Receipt receipt = new Receipt();
        assertEquals(0, Receipts.scoreAlphaNumericCharacters(receipt));
    }

    @Test
    public void scoreAlphaNumericCharacters_givenValidRetailer_expectCorrectPoints() {
        Receipt receipt = new Receipt();
        receipt.setRetailer("ABC-123"); // 6 points;
        assertEquals(6, Receipts.scoreAlphaNumericCharacters(receipt));
    }

    @Test
    public void scoreTotalAmountIsRound_givenNullTotal_expectZero() {
        Receipt receipt = new Receipt();
        receipt.setTotal(null);
        assertEquals(0, Receipts.scoreTotalAmountIsRound(receipt));
    }

    @Test
    public void scoreTotalAmountIsRound_givenItIsRound_expectPoints() {
        Receipt receipt = new Receipt();
        receipt.setTotal("45.00");
        assertEquals(50, Receipts.scoreTotalAmountIsRound(receipt));
    }

    @Test
    public void scoreTotalAmountIsRound_givenItIsNotRound_expectZero() {
        Receipt receipt = new Receipt();
        receipt.setTotal("45.12");
        assertEquals(0, Receipts.scoreTotalAmountIsRound(receipt));
    }

    @Test
    public void scoreTotalAmountIsMultipleOfQuarters_givenNull_expectZero() {
        Receipt receipt = new Receipt();
        receipt.setTotal(null);
        assertEquals(0, Receipts.scoreTotalAmountIsMultipleOfQuarters(receipt));
    }

    @Test
    public void scoreTotalAmountIsMultipleOfQuarters_givenMultipleOfQuarters_expectPoints() {
        Receipt receipt = new Receipt();
        receipt.setTotal("6.25");
        assertEquals(25, Receipts.scoreTotalAmountIsMultipleOfQuarters(receipt));
    }

    @Test
    public void scoreTotalAmountIsMultipleOfQuarters_givenNotMultipleOfQuarters_expectZeroPoints() {
        Receipt receipt = new Receipt();
        receipt.setTotal("6.32");
        assertEquals(0, Receipts.scoreTotalAmountIsMultipleOfQuarters(receipt));
    }

    /**
     * NOTE: A test for null items collection is omitted. It is not possible for the items collection to be null as
     * the model initializes an immutable reference to an array list upon all creations of receipts.
     */
    @Test
    public void scoreNumberOfItems_givenOneItem_expectZeroPoints() {
        Receipt receipt = new Receipt();
        Item item = buildItem("An item 1", "1.00");
        receipt.addItem(item);

        assertEquals(0, Receipts.scoreNumberOfItems(receipt));
    }

    @Test
    public void scoreNumberOfItems_givenTwoItems_expectPoints() {
        Receipt receipt = new Receipt();
        Item itemOne = buildItem("An item 1", "1.00");
        Item itemTwo = buildItem("An item 2", "2.00");
        receipt.addItem(itemOne);
        receipt.addItem(itemTwo);

        assertEquals(5, Receipts.scoreNumberOfItems(receipt));
    }

    @Test
    public void scoreNumberOfItems_givenManyItems_expectManyPoints() {
        // Seven items - 3 * 5 points = 15
        Receipt receipt = new Receipt();
        for (int i = 0; i < 7; i++) {
            receipt.addItem(buildItem("An item", "1.00"));
        }

        assertEquals(15, Receipts.scoreNumberOfItems(receipt));
    }

    @Test
    public void scoreItemDescription_givenNullShortDescription_expectItemSkipped() {
        Receipt receipt = new Receipt();
        receipt.addItem(buildItem(null, "1.00"));

        assertEquals(0, Receipts.scoreItemDescription(receipt));
    }

    @Test
    public void scoreItemDescription_givenNullPrice_expectItemSkipped() {
        Receipt receipt = new Receipt();
        receipt.addItem(buildItem("description", null));

        assertEquals(0, Receipts.scoreItemDescription(receipt));
    }

    @Test
    public void scoreItemDescription_givenInvalidPrice_expectItemSkipped() {
        Receipt receipt = new Receipt();
        receipt.addItem(buildItem("123456", "invalid price"));

        assertEquals(0, Receipts.scoreItemDescription(receipt));
    }

    @Test
    public void scoreItemDescription_givenNotMultipleOfThree_expectNoPoints() {
        Receipt receipt = new Receipt();
        receipt.addItem(buildItem("description", "invalid price"));

        assertEquals(0, Receipts.scoreItemDescription(receipt));
    }

    @Test
    public void scoreItemDescription_givenMultipleOfThree_expectPoints() {
        Receipt receipt = new Receipt();
        receipt.addItem(buildItem("123456", "20.50"));

        // 20.50 * 0.2 = 4.1
        // ceil(4.1) = 5
        assertEquals(5, Receipts.scoreItemDescription(receipt));
    }

    @Test
    public void scorePurchaseDate_givenNullPurchaseDate_expectZeroPoints() {
        Receipt receipt = new Receipt();
        receipt.setPurchaseDate(null);

        assertEquals(0, Receipts.scorePurchaseDate(receipt));
    }

    @Test
    public void scorePurchaseDate_givenPurchaseDateDayIsOdd_expectPoints() {
        Receipt receipt = new Receipt();
        receipt.setPurchaseDate(LocalDate.of(2024, 10 ,21));

        assertEquals(6, Receipts.scorePurchaseDate(receipt));
    }

    @Test
    public void scorePurchaseDate_givenPurchaseDateDayIsEven_expectZeroPoints() {
        Receipt receipt = new Receipt();
        receipt.setPurchaseDate(LocalDate.of(2024, 10, 22));

        assertEquals(0, Receipts.scorePurchaseDate(receipt));
    }

    @Test
    public void scorePurchaseTime_givenNullPurchaseTime_expectZeroPoints() {
        Receipt receipt = new Receipt();
        receipt.setPurchaseTime(null);

        assertEquals(0, Receipts.scorePurchaseTime(receipt));
    }

    @Test
    public void scorePurchaseTime_givenPurchaseTimeOutsideOfWindow_expectZeroPoints() {
        Receipt receipt = new Receipt();
        receipt.setPurchaseTime(LocalTime.of(9, 0, 0));

        assertEquals(0, Receipts.scorePurchaseTime(receipt));
    }

    @Test
    public void scorePurchaseTime_givenPurchaseTimeInsideWindow_expectPoints() {
        Receipt receipt = new Receipt();
        receipt.setPurchaseTime(LocalTime.of(15, 0, 0));

        assertEquals(10, Receipts.scorePurchaseTime(receipt));
    }

    @Test
    public void scoreReceipt_givenNullReceipt_expectZeroPoints() {
        assertEquals(0, Receipts.scoreReceipt(null));
    }

    /**
     * Total Points: 28
     * Breakdown:
     *      6 points - retailer name has 6 characters
     *     10 points - 4 items (2 pairs @ 5 points each)
     *      3 Points - "Emils Cheese Pizza" is 18 characters (a multiple of 3)
     *                 item price of 12.25 * 0.2 = 2.45, rounded up is 3 points
     *      3 Points - "Klarbrunn 12-PK 12 FL OZ" is 24 characters (a multiple of 3)
     *                 item price of 12.00 * 0.2 = 2.4, rounded up is 3 points
     *      6 points - purchase day is odd
     *   + ---------
     *   = 28 points
     */
    @Test
    public void scoreReceipt_givenFullReceiptExampleOne_expectPoints() {
        Receipt receipt = new Receipt.Builder()
                .withRetailer("Target")
                .withPurchaseDate(LocalDate.of(2022, 1, 1))
                .withPurchaseTime(LocalTime.of(13, 1))
                .withItem(buildItem("Mountain Dew 12PK", "6.49"))
                .withItem(buildItem("Emils Cheese Pizza", "12.25"))
                .withItem(buildItem("Knorr Creamy Chicken", "1.26"))
                .withItem(buildItem("Doritos Nacho Cheese", "3.35"))
                .withItem(buildItem("   Klarbrunn 12-PK 12 FL OZ   ", "12.00"))
                .withTotal("35.35")
                .build();

        assertEquals(28, Receipts.scoreReceipt(receipt));
    }

    /**
     * Total Points: 109
     * Breakdown:
     *     50 points - total is a round dollar amount
     *     25 points - total is a multiple of 0.25
     *     14 points - retailer name (M&M Corner Market) has 14 alphanumeric characters
     *                 note: '&' is not alphanumeric
     *     10 points - 2:33pm is between 2:00pm and 4:00pm
     *     10 points - 4 items (2 pairs @ 5 points each)
     *   + ---------
     *   = 109 points
     */
    @Test
    public void scoreReceipt_givenFullReceiptExampleTwo_expectPoints() {
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

        assertEquals(109, Receipts.scoreReceipt(receipt));
    }

    private Item buildItem(String description, String price) {
        return new Item.Builder()
                .withShortDescription(description)
                .withPrice(price)
                .build();
    }

}
