package io.medness.simple2pc.account.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Account Not Found")
public class AccountNotFoundException extends RuntimeException{
}
