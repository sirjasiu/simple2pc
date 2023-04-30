package io.medness.simple2pc.account.adapter.in.web;

import io.medness.simple2pc.account.application.port.in.CreateAccount;
import io.medness.simple2pc.account.application.port.in.Deposit;
import io.medness.simple2pc.account.application.port.in.FindAccount;
import io.medness.simple2pc.account.application.port.in.Withdraw;
import io.medness.simple2pc.account.domain.WithdrawJobData;
import io.medness.simple2pc.job.application.port.in.GenerateJobPath;
import io.medness.simple2pc.job.domain.Job;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final CreateAccount createAccount;
    private final FindAccount findAccount;
    private final Withdraw withdraw;
    private final Deposit deposit;
    private final GenerateJobPath generateJobPath;

    public AccountController(CreateAccount createAccount,
                             FindAccount findAccount,
                             Withdraw withdraw, Deposit deposit,
                             GenerateJobPath generateJobPath) {
        this.createAccount = createAccount;
        this.findAccount = findAccount;
        this.withdraw = withdraw;
        this.deposit = deposit;
        this.generateJobPath = generateJobPath;
    }

    @PostMapping
    public AccountResponse createAccount(@RequestBody AccountRequest request) {
        return AccountResponse.from(
                createAccount.create(request.name())
        );
    }

    @GetMapping("{accountId}")
    public AccountResponse getAccount(@PathVariable UUID accountId) {
        return AccountResponse.from(findAccount.get(accountId));
    }

    @PostMapping("{accountId}/actions")
    public AccountResponse accountActions(@PathVariable UUID accountId, @RequestBody AccountActionRequest request) {
        switch (request.action()) {
            case deposit -> deposit.deposit(accountId, request.funds());
            case withdraw -> withdraw.withdraw(accountId, request.funds());
        }
        return AccountResponse.from(findAccount.get(accountId));
    }

    @PostMapping("{accountId}/prepare-actions")
    public ResponseEntity<Void> prepareAccountActions(@PathVariable UUID accountId,
                                                      @RequestBody AccountActionRequest request,
                                                      UriComponentsBuilder uriComponentsBuilder) {
        return switch (request.action()) {
            case withdraw -> {
                Job<WithdrawJobData> withdrawJobDataJob = withdraw.prepareWithdraw(accountId, request.funds());
                yield ResponseEntity.accepted()
                        .header("location",
                                generateJobPath.generate(withdrawJobDataJob, uriComponentsBuilder).toString())
                        .build();
            }
            case deposit -> throw new UnsupportedOperationException();
        };
    }

}
