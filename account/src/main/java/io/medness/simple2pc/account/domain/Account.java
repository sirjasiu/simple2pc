package io.medness.simple2pc.account.domain;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(schema = "account", name = "account")
public class Account {

    @Id
    private UUID id;

    private String name;

    private BigDecimal funds;
    
    protected Account() {
    }

    public Account(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.funds = BigDecimal.ZERO;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getFunds() {
        return funds;
    }

    public void deposit(BigDecimal value) {
        funds = funds.add(value);
    }

    public void withdraw(BigDecimal value) {
        validateAvailableFunds(value);
        funds = funds.subtract(value);
    }

    public void validateAvailableFunds(BigDecimal value) {
        if (funds.compareTo(value) < 0) {
            throw new InsufficientFundsException();
        }
    }
}
