package io.medness.simple2pc.offer.application.job;

import io.medness.simple2pc.offer.application.port.in.Purchase;
import io.medness.simple2pc.offer.application.port.in.Reservation;
import io.medness.simple2pc.offer.domain.PurchaseJobData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static java.math.BigDecimal.ONE;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PurchaseJobHandlerTest {

    private static final UUID OFFER_ID = UUID.randomUUID();
    private static final UUID BUYER_ID = UUID.randomUUID();

    @Mock
    private Purchase purchase;

    @Mock
    private Reservation reservation;

    @InjectMocks
    private PurchaseJobHandler handler;

    @Test
    public void shouldPrepareOperation() {
        // when
        handler.prepare(new PurchaseJobData(OFFER_ID, BUYER_ID, ONE));

        // then
        verify(reservation)
                .makeReservation(eq(OFFER_ID), eq(BUYER_ID), argThat((a) -> a.compareTo(ONE) == 0));
    }

    @Test
    public void shouldCommitOperation() {
        // when
        handler.commit(new PurchaseJobData(OFFER_ID, BUYER_ID, ONE));

        // then
        verify(purchase)
                .purchase(eq(OFFER_ID), eq(BUYER_ID), argThat((a) -> a.compareTo(ONE) == 0));
    }


    @Test
    public void shouldAbortOperation() {
        // when
        handler.abort(new PurchaseJobData(OFFER_ID, BUYER_ID, ONE));

        // then
        verify(reservation).cancelReservation(OFFER_ID, BUYER_ID);
    }
}