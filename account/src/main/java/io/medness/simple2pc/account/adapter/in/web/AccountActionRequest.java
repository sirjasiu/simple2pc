package io.medness.simple2pc.account.adapter.in.web;

import java.math.BigDecimal;

public record AccountActionRequest(Action action, BigDecimal funds) {
    enum Action {
        deposit,
        withdraw
    }
}
