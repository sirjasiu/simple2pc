package io.medness.simple2pc.offer.adapter.in.web;

import java.math.BigDecimal;

public record OfferRequest(String name, BigDecimal price) {
}
