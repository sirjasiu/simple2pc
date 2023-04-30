package io.medness.simple2pc.account.application.port.in;

import io.medness.simple2pc.account.domain.WithdrawJobData;
import io.medness.simple2pc.job.domain.Job;

import java.math.BigDecimal;
import java.util.UUID;

public interface Withdraw {

    void withdraw(UUID accountId, BigDecimal value);

    Job<WithdrawJobData> prepareWithdraw(UUID accountId, BigDecimal value);
}
