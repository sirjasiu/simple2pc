package io.medness.simple2pc.account.application;

import io.medness.simple2pc.account.application.job.WithdrawJobHandler;
import io.medness.simple2pc.account.application.port.in.CreateAccount;
import io.medness.simple2pc.account.application.port.in.Deposit;
import io.medness.simple2pc.account.application.port.in.FindAccount;
import io.medness.simple2pc.account.application.port.in.Withdraw;
import io.medness.simple2pc.account.application.port.out.LoadAccount;
import io.medness.simple2pc.account.application.port.out.PersistAccount;
import io.medness.simple2pc.account.domain.Account;
import io.medness.simple2pc.account.domain.AccountNotFoundException;
import io.medness.simple2pc.account.domain.WithdrawJobData;
import io.medness.simple2pc.job.application.port.in.PrepareJob;
import io.medness.simple2pc.job.domain.Job;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Transactional
public class AccountService implements CreateAccount, FindAccount, Deposit, Withdraw {

    private final LoadAccount loadAccount;
    private final PersistAccount persistAccount;
    private final PrepareJob prepareJob;

    public AccountService(LoadAccount loadAccount, PersistAccount persistAccount, PrepareJob prepareJob) {
        this.loadAccount = loadAccount;
        this.persistAccount = persistAccount;
        this.prepareJob = prepareJob;
    }

    @Override
    public Account create(String name) {
        return persistAccount.save(new Account(name));
    }

    @Override
    public Optional<Account> find(UUID accountId) {
        return loadAccount.findById(accountId);
    }

    @Override
    public Job<WithdrawJobData> prepareWithdraw(UUID accountId, BigDecimal value) {
        return prepareJob.prepare(WithdrawJobHandler.OPERATION_NAME, new WithdrawJobData(accountId, value));
    }

    @Override
    public void deposit(UUID accountId, BigDecimal value) {
        get(accountId).deposit(value);
    }

    @Override
    public void withdraw(UUID accountId, BigDecimal value) {
        get(accountId).withdraw(value);
    }
}
