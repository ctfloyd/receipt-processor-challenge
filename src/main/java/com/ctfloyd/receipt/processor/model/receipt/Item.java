package com.ctfloyd.receipt.processor.model.receipt;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * An item on a receipt. Items contribute to the total score of a receipt.
 */
public class Item {

    private String shortDescription;
    private String price;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Item.class.getSimpleName() + "[", "]")
                .add("shortDescription='" + shortDescription + "'")
                .add("price='" + price + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(shortDescription, item.shortDescription) && Objects.equals(price, item.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortDescription, price);
    }


    public static final class Builder {
        private Item item;

        public Builder() {
            item = new Item();
        }

        public Builder withShortDescription(String shortDescription) {
            item.setShortDescription(shortDescription);
            return this;
        }

        public Builder withPrice(String price) {
            item.setPrice(price);
            return this;
        }

        public Item build() {
            return item;
        }
    }
}
