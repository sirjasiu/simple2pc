package io.medness.simple2pc.offer.application.port.in;

import io.medness.simple2pc.offer.domain.Offer;

import java.math.BigDecimal;

public interface CreateOffer {

    Offer create(String name, BigDecimal price);
}
