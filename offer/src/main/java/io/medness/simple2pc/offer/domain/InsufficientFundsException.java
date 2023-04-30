package io.medness.simple2pc.offer.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Insufficient funds")
public class InsufficientFundsException extends RuntimeException {
}
