package io.medness.simple2pc.offer.adapter.in.web;

import io.medness.simple2pc.offer.domain.Offer;

import java.math.BigDecimal;
import java.util.UUID;

public record OfferResponse(UUID id, String name, BigDecimal price, UUID accountId, boolean reservation) {

    public static OfferResponse from(Offer offer) {
        return new OfferResponse(offer.getId(), offer.getName(), offer.getPrice(), offer.getAccountId(), offer.isReservation());
    }
}
