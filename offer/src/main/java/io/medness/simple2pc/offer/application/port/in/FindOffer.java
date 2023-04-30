package io.medness.simple2pc.offer.application.port.in;

import io.medness.simple2pc.offer.domain.Offer;
import io.medness.simple2pc.offer.domain.OfferNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface FindOffer {

    Optional<Offer> find(UUID offerId);

    default Offer get(UUID offerId) {
        return find(offerId).orElseThrow(OfferNotFoundException::new);
    }
}
