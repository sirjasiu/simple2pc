package io.medness.simple2pc.offer.application.job;

import io.medness.simple2pc.job.adapter.out.JobHandler;
import io.medness.simple2pc.offer.application.port.in.Purchase;
import io.medness.simple2pc.offer.application.port.in.Reservation;
import io.medness.simple2pc.offer.domain.PurchaseJobData;
import org.springframework.stereotype.Component;

@Component
public class PurchaseJobHandler implements JobHandler<PurchaseJobData> {

    public static final String OPERATION_NAME = "purchase";

    private final Purchase purchase;

    private final Reservation reservation;

    public PurchaseJobHandler(Purchase purchase, Reservation reservation) {
        this.purchase = purchase;
        this.reservation = reservation;
    }

    @Override
    public boolean canHandle(String operationName) {
        return OPERATION_NAME.equals(operationName);
    }

    @Override
    public void commit(PurchaseJobData data) {
        purchase.purchase(data.offerId(), data.buyerId(), data.value());
    }

    @Override
    public void prepare(PurchaseJobData data) {
        reservation.makeReservation(data.offerId(), data.buyerId(), data.value());
    }

    @Override
    public void abort(PurchaseJobData data) {
        reservation.cancelReservation(data.offerId(), data.buyerId());
    }
}
