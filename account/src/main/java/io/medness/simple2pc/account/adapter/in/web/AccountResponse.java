package io.medness.simple2pc.account.adapter.in.web;

import io.medness.simple2pc.account.domain.Account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(UUID id, String name, BigDecimal funds) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getName(), account.getFunds());
    }
}
