package io.medness.simple2pc.account.application.job;

import io.medness.simple2pc.account.application.port.in.FindAccount;
import io.medness.simple2pc.account.application.port.in.Withdraw;
import io.medness.simple2pc.account.domain.WithdrawJobData;
import io.medness.simple2pc.job.adapter.out.JobHandler;
import org.springframework.stereotype.Component;

@Component
public class WithdrawJobHandler implements JobHandler<WithdrawJobData> {

    private final Withdraw withdraw;

    private final FindAccount findAccount;

    public WithdrawJobHandler(Withdraw withdraw, FindAccount findAccount) {
        this.withdraw = withdraw;
        this.findAccount = findAccount;
    }

    @Override
    public boolean canHandle(Object data) {
        return data instanceof WithdrawJobData;
    }

    @Override
    public void commit(WithdrawJobData data) {
        withdraw.withdraw(data.accountId(), data.value());
    }

    @Override
    public void prepare(WithdrawJobData data) {
        findAccount.get(data.accountId()).validateAvailableFunds(data.value());
    }

    @Override
    public void abort(WithdrawJobData data) {
        //nothing to do
    }
}
