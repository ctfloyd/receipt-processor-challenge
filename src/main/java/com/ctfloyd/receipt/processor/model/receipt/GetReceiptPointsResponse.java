package com.ctfloyd.receipt.processor.model.receipt;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Describes how many points were awarded for processing a receipt.
 */
public class GetReceiptPointsResponse {

    private long points;

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GetReceiptPointsResponse.class.getSimpleName() + "[", "]")
                .add("points=" + points)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetReceiptPointsResponse that = (GetReceiptPointsResponse) o;
        return points == that.points;
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }


    public static final class Builder {
        private GetReceiptPointsResponse getReceiptPointsResponse;

        public Builder() {
            getReceiptPointsResponse = new GetReceiptPointsResponse();
        }

        public Builder withPoints(long points) {
            getReceiptPointsResponse.setPoints(points);
            return this;
        }

        public GetReceiptPointsResponse build() {
            return getReceiptPointsResponse;
        }
    }
}
