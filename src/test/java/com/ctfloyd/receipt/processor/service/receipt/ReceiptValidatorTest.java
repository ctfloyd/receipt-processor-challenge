package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.Item;
import com.ctfloyd.receipt.processor.model.receipt.Receipt;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReceiptValidatorTest {

    @Test
    public void validateReceipt_givenValidReceipt_expectTrue() {
        assertTrue(ReceiptValidator.isValid(getValidReceipt()));
    }

    @Test
    public void validateReceipt_givenInvalidRetailer_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.setRetailer("Invalid!@");
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceipt_givenInvalidTotal_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.setTotal("Invalid!@");
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceipt_givenInvalidShortDescription_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.getItems().get(0).setShortDescription("Invalid!@");
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceipt_givenInvalidItemPrice_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.getItems().get(0).setPrice("Invalid!@");
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceipt_givenNoItems_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.setItems(List.of());
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceipt_givenNullRetailer_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.setRetailer(null);
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceipt_givenNullPurchaseDate_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.setPurchaseDate(null);
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceipt_givenNullPurchaseTime_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.setPurchaseTime(null);
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceipt_givenNullTotal_expectFalse() {
        Receipt receipt = getValidReceipt();
        receipt.setTotal(null);
        assertFalse(ReceiptValidator.isValid(receipt));
    }

    @Test
    public void validateReceiptId_givenValidReceiptId_expectTrue() {
        assertTrue(ReceiptValidator.isValidId(UUID.randomUUID().toString()));
    }

    @Test
    public void validateReceiptId_givenNullId_expectFalse() {
        assertFalse(ReceiptValidator.isValidId(null));
    }

    @Test
    public void validateReceiptId_givenInvalidId_expectFalse() {
        assertFalse(ReceiptValidator.isValidId("Inv alid"));
    }

    private Receipt getValidReceipt() {
        return new Receipt.Builder()
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
    }

    private Item buildItem(String description, String price) {
        return new Item.Builder()
                .withShortDescription(description)
                .withPrice(price)
                .build();
    }

}
