package io.medness.simple2pc.account.application.port.out;

import io.medness.simple2pc.account.domain.Account;

import java.util.Optional;
import java.util.UUID;

public interface LoadAccount {

    Optional<Account> findById(UUID accountId);
}
