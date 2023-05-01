package io.medness.simple2pc.offer.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record PurchaseJobData(UUID offerId, UUID buyerId, BigDecimal value) implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PurchaseJobData that = (PurchaseJobData) o;
        return Objects.equals(offerId, that.offerId) && Objects.equals(buyerId, that.buyerId) && value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offerId, buyerId, value);
    }
}
