package io.medness.simple2pc.offer.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

public record OfferActionRequest(Action action, UUID buyerId, BigDecimal price) {
    enum Action {
        purchase
    }
}
