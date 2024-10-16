package com.ctfloyd.receipt.processor.model.receipt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The POJO representation of a receipt. Receipts can be processed for points.
 */
public class Receipt {

    private String retailer;
    private String purchaseDate;
    private String purchaseTime;
    private final List<Item> items = new ArrayList<>();

    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public void setItems(Collection<Item> items) {
        this.items.clear();
        if (items != null) {
            addItems(items);
        }
    }

    public void addItems(Collection<Item> items) {
        if (items != null) {
            items.forEach(this::addItem);
        }
    }

    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Receipt.class.getSimpleName() + "[", "]")
                .add("retailer='" + retailer + "'")
                .add("purchaseDate='" + purchaseDate + "'")
                .add("purchaseTime='" + purchaseTime + "'")
                .add("items=" + items)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(retailer, receipt.retailer) && Objects.equals(purchaseDate, receipt.purchaseDate) && Objects.equals(purchaseTime, receipt.purchaseTime) && Objects.equals(items, receipt.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retailer, purchaseDate, purchaseTime, items);
    }


    public static final class Builder {
        private Receipt receipt;

        public Builder() {
            receipt = new Receipt();
        }

        public Builder withRetailer(String retailer) {
            receipt.setRetailer(retailer);
            return this;
        }

        public Builder withPurchaseDate(String purchaseDate) {
            receipt.setPurchaseDate(purchaseDate);
            return this;
        }

        public Builder withPurchaseTime(String purchaseTime) {
            receipt.setPurchaseTime(purchaseTime);
            return this;
        }

        public Builder withItem(Item item) {
            receipt.addItem(item);
            return this;
        }

        public Builder withItems(Collection<Item> items) {
            receipt.setItems(items);
            return this;
        }

        public Receipt build() {
            return receipt;
        }
    }
}
