package io.medness.simple2pc.offer.application.port.out;

import io.medness.simple2pc.offer.domain.Offer;

public interface PersistOffer {

    Offer save(Offer offer);
}
