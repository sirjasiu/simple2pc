package io.medness.simple2pc.offer.adapter.in.web;

import io.medness.simple2pc.job.application.port.in.GenerateJobPath;
import io.medness.simple2pc.job.domain.Job;
import io.medness.simple2pc.offer.application.port.in.CreateOffer;
import io.medness.simple2pc.offer.application.port.in.FindOffer;
import io.medness.simple2pc.offer.application.port.in.Purchase;
import io.medness.simple2pc.offer.domain.PurchaseJobData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {

    private final CreateOffer createOffer;
    private final FindOffer findOffer;
    private final Purchase purchase;
    private final GenerateJobPath generateJobPath;

    public OfferController(CreateOffer createOffer, FindOffer findOffer, Purchase purchase,
                           GenerateJobPath generateJobPath) {
        this.createOffer = createOffer;
        this.findOffer = findOffer;
        this.purchase = purchase;
        this.generateJobPath = generateJobPath;
    }

    @PostMapping
    public OfferResponse createOffer(@RequestBody OfferRequest request) {
        return OfferResponse.from(
                createOffer.create(request.name(), request.price())
        );
    }

    @GetMapping("{offerId}")
    public OfferResponse getOffer(@PathVariable UUID offerId) {
        return OfferResponse.from(findOffer.get(offerId));
    }

    @PostMapping("{offerId}/actions")
    public OfferResponse offerActions(@PathVariable UUID offerId, @RequestBody OfferActionRequest request) {
        switch (request.action()) {
            case purchase -> purchase.purchase(offerId, request.buyerId(), request.price());
        }
        return OfferResponse.from(findOffer.get(offerId));
    }

    @PostMapping("{offerId}/prepare-actions")
    public ResponseEntity<Void> prepareOfferActions(@PathVariable UUID offerId,
                                                      @RequestBody OfferActionRequest request,
                                                      UriComponentsBuilder uriComponentsBuilder) {
        return switch (request.action()) {
            case purchase -> {
                Job<PurchaseJobData> prepareWithdraw = purchase.preparePurchase(offerId, request.buyerId(),
                        request.price());
                yield ResponseEntity.accepted()
                        .header("location",
                                generateJobPath.generate(prepareWithdraw, uriComponentsBuilder).toString())
                        .build();
            }
        };
    }

}
