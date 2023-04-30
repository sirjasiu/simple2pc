package io.medness.simple2pc.account.application.port.out;

import io.medness.simple2pc.account.domain.Account;

public interface PersistAccount {

    Account save(Account job);
}
