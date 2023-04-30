package io.medness.simple2pc.offer.application.port.in;

import io.medness.simple2pc.job.domain.Job;
import io.medness.simple2pc.offer.domain.PurchaseJobData;

import java.math.BigDecimal;
import java.util.UUID;

public interface Purchase {

    void purchase(UUID offerId, UUID accountId, BigDecimal value);
    Job<PurchaseJobData> preparePurchase(UUID offerId, UUID accountId, BigDecimal value);
}
