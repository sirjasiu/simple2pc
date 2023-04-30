package io.medness.simple2pc.offer.application.port.out;

import io.medness.simple2pc.offer.domain.Offer;

import java.util.Optional;
import java.util.UUID;

public interface LoadOffer {

    Optional<Offer> findById(UUID offerId);
}
