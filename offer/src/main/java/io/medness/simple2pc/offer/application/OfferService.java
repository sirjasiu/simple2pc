package io.medness.simple2pc.offer.application;

import io.medness.simple2pc.job.application.port.in.PrepareJob;
import io.medness.simple2pc.job.domain.Job;
import io.medness.simple2pc.offer.application.port.in.CreateOffer;
import io.medness.simple2pc.offer.application.port.in.FindOffer;
import io.medness.simple2pc.offer.application.port.in.Purchase;
import io.medness.simple2pc.offer.application.port.in.Reservation;
import io.medness.simple2pc.offer.application.port.out.LoadOffer;
import io.medness.simple2pc.offer.application.port.out.PersistOffer;
import io.medness.simple2pc.offer.domain.Offer;
import io.medness.simple2pc.offer.domain.PurchaseJobData;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OfferService implements CreateOffer, FindOffer, Purchase, Reservation {

    private final LoadOffer loadOffer;
    private final PersistOffer persistOffer;
    private final PrepareJob prepareJob;

    public OfferService(LoadOffer loadOffer, PersistOffer persistOffer, PrepareJob prepareJob) {
        this.loadOffer = loadOffer;
        this.persistOffer = persistOffer;
        this.prepareJob = prepareJob;
    }

    @Override
    public Offer create(String name, BigDecimal price) {
        return persistOffer.save(new Offer(name, price));
    }

    @Override
    public void purchase(UUID offerId, UUID buyerId, BigDecimal value) {
        get(offerId).purchase(buyerId, value);
    }

    @Override
    public Job<PurchaseJobData> preparePurchase(UUID offerId, UUID buyerId, BigDecimal value) {
        return prepareJob.prepare(new PurchaseJobData(offerId, buyerId, value));
    }

    @Override
    public void makeReservation(UUID offerId, UUID buyerId, BigDecimal value) {
        get(offerId).makeReservation(buyerId, value);
    }

    @Override
    public void cancelReservation(UUID offerId, UUID buyerId) {
        get(offerId).cancelReservation(buyerId);
    }

    @Override
    public Optional<Offer> find(UUID offerId) {
        return loadOffer.findById(offerId);
    }
}
