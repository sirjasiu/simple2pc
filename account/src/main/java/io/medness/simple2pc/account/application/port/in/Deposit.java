package io.medness.simple2pc.account.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface Deposit {

    void deposit(UUID accountId, BigDecimal value);
}
