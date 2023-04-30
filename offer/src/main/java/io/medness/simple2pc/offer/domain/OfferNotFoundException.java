package io.medness.simple2pc.offer.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Offer Not Found")
public class OfferNotFoundException extends RuntimeException{
}
