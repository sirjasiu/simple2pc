package io.medness.simple2pc.offer.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface Reservation {

    void makeReservation(UUID offerId, UUID buyerId, BigDecimal value);

    void cancelReservation(UUID offerId, UUID buyerId);
}
