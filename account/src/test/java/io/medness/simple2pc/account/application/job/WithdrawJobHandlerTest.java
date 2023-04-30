package io.medness.simple2pc.account.application.job;

import io.medness.simple2pc.account.application.port.in.FindAccount;
import io.medness.simple2pc.account.application.port.in.Withdraw;
import io.medness.simple2pc.account.domain.Account;
import io.medness.simple2pc.account.domain.AccountNotFoundException;
import io.medness.simple2pc.account.domain.InsufficientFundsException;
import io.medness.simple2pc.account.domain.WithdrawJobData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawJobHandlerTest {

    @Mock
    Withdraw withdraw;
    @Mock
    FindAccount findAccount;

    @InjectMocks
    WithdrawJobHandler handler;

    @Test
    public void shouldProperlyPerformJobPreparation() {
        //given
        Account john = new Account("John");
        john.deposit(new BigDecimal(10));
        when(findAccount.get(any())).thenReturn(john);


        // when
        handler.prepare(new WithdrawJobData(john.getId(), BigDecimal.ONE));

        //then
        verify(findAccount).get(john.getId());
    }

    @Test
    public void shouldNotPerformJobPreparationOnNotExistingUser() {
        //given
        doThrow(new AccountNotFoundException()).when(findAccount).get(any());

        // when
        Assertions.assertThatThrownBy(
                () -> handler.prepare(new WithdrawJobData(UUID.randomUUID(), BigDecimal.ONE)))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    public void shouldNotPerformJobPreparationOnNonSufficientFunds() {
        //given
        Account john = new Account("John");
        when(findAccount.get(any())).thenReturn(john);

        // when
        Assertions.assertThatThrownBy(
                        () -> handler.prepare(new WithdrawJobData(UUID.randomUUID(), BigDecimal.ONE)))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    public void shouldProperlyPerformCommit() {
        //given
        UUID accountId = UUID.randomUUID();
        // when
        handler.commit(new WithdrawJobData(accountId, BigDecimal.ONE));

        //then
        verify(withdraw).withdraw(accountId, BigDecimal.ONE);
    }

}