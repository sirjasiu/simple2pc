package io.medness.simple2pc.account.application.port.in;

import io.medness.simple2pc.account.domain.Account;
import io.medness.simple2pc.account.domain.AccountNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface FindAccount {

    Optional<Account> find(UUID accountId);

    default Account get(UUID accountId) {
        return find(accountId).orElseThrow(AccountNotFoundException::new);
    }
}
