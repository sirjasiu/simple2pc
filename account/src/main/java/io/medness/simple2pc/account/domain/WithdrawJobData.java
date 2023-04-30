package io.medness.simple2pc.account.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record WithdrawJobData(UUID accountId, BigDecimal value) implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WithdrawJobData that = (WithdrawJobData) o;
        return Objects.equals(accountId, that.accountId) && value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, value);
    }
}
