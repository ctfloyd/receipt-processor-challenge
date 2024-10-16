package com.ctfloyd.receipt.processor.model.receipt;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Describes the identifier of a receipt that has been processed.
 */
public class ProcessReceiptResponse {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ProcessReceiptResponse.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessReceiptResponse that = (ProcessReceiptResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public static final class Builder {
        private ProcessReceiptResponse processReceiptResponse;

        public Builder() {
            processReceiptResponse = new ProcessReceiptResponse();
        }

        public Builder withId(String id) {
            processReceiptResponse.setId(id);
            return this;
        }

        public ProcessReceiptResponse build() {
            return processReceiptResponse;
        }
    }
}
