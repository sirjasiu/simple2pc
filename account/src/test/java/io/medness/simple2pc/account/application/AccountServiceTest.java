package io.medness.simple2pc.account.application;

import io.medness.simple2pc.account.application.job.WithdrawJobHandler;
import io.medness.simple2pc.account.application.port.out.LoadAccount;
import io.medness.simple2pc.account.application.port.out.PersistAccount;
import io.medness.simple2pc.account.domain.Account;
import io.medness.simple2pc.account.domain.AccountNotFoundException;
import io.medness.simple2pc.account.domain.InsufficientFundsException;
import io.medness.simple2pc.account.domain.WithdrawJobData;
import io.medness.simple2pc.job.application.port.in.PrepareJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    LoadAccount loadAccount;
    @Mock
    PersistAccount persistAccount;
    @Mock
    PrepareJob prepareJob;

    @InjectMocks
    AccountService service;

    @Test
    public void shouldCreateAnAccount() {
        //given
        when(persistAccount.save(any())).thenAnswer((inv) -> inv.getArgument(0));

        // when
        Account john = service.create("John");

        //then
        assertThat(john.getName()).isEqualTo("John");
        assertThat(john.getFunds()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(persistAccount).save(john);
    }

    @Test
    public void shouldFetchAccount() {
        //given
        Account john = new Account("John");
        when(loadAccount.findById(any())).thenReturn(Optional.of(john));

        // when
        Account account = service.get(john.getId());

        //then
        assertThat(account.getName()).isEqualTo("John");
        assertThat(account.getFunds()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    public void shouldThrowExceptionIfCannotFetchAccount() {
        // expect
        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(AccountNotFoundException.class);

    }

    @Test
    public void shouldDepositFunds() {
        //given
        Account john = new Account("John");
        when(loadAccount.findById(any())).thenReturn(Optional.of(john));

        // when
        service.deposit(john.getId(), BigDecimal.ONE);

        //then
        assertThat(john.getName()).isEqualTo("John");
        assertThat(john.getFunds()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    public void shouldWithdrawFunds() {
        //given
        Account john = new Account("John");
        john.deposit(new BigDecimal(10));
        when(loadAccount.findById(any())).thenReturn(Optional.of(john));

        // when
        service.withdraw(john.getId(), BigDecimal.ONE);

        //then
        assertThat(john.getName()).isEqualTo("John");
        assertThat(john.getFunds()).isEqualByComparingTo(new BigDecimal(9));
    }

    @Test
    public void shouldNotBeAbleToWithdrawInsufficientFunds() {
        //given
        Account john = new Account("John");
        when(loadAccount.findById(any())).thenReturn(Optional.of(john));

        // expect
        assertThatThrownBy(() -> service.withdraw(john.getId(), BigDecimal.ONE))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    public void shouldPassWithdrawingFundsIntoJobHandler() {
        //given
        Account john = new Account("John");
        john.deposit(new BigDecimal(10));

        // when
        service.prepareWithdraw(john.getId(), BigDecimal.ONE);

        //then
        verify(prepareJob).prepare(WithdrawJobHandler.OPERATION_NAME,
                new WithdrawJobData(john.getId(), BigDecimal.ONE));
    }
}