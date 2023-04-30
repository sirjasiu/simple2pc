package io.medness.simple2pc.offer.adapter.out;

import io.medness.simple2pc.offer.application.port.out.LoadOffer;
import io.medness.simple2pc.offer.application.port.out.PersistOffer;
import io.medness.simple2pc.offer.domain.Offer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OfferRepository extends CrudRepository<Offer, UUID>, PersistOffer, LoadOffer {
}
