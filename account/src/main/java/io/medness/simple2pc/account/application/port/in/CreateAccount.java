package io.medness.simple2pc.account.application.port.in;

import io.medness.simple2pc.account.domain.Account;

public interface CreateAccount {

    Account create(String name);
}
