package io.medness.simple2pc.account.adapter.out;

import io.medness.simple2pc.account.application.port.out.LoadAccount;
import io.medness.simple2pc.account.application.port.out.PersistAccount;
import io.medness.simple2pc.account.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID>, PersistAccount, LoadAccount {
}
