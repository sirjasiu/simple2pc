package io.medness.simple2pc.offer.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(schema = "offer", name = "offer")
public class Offer {

    @Id
    private UUID id;

    private String name;

    private BigDecimal price;

    @Column(name = "account_id")
    private UUID accountId;

    private boolean reservation;

    protected Offer() {
    }

    public Offer(String name, BigDecimal price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public boolean isReservation() {
        return reservation;
    }

    public void purchase(UUID accountId, BigDecimal funds) {
        if (price.compareTo(funds) != 0) {
            throw new InsufficientFundsException();
        }
        if (this.accountId != null && !this.accountId.equals(accountId)) {
            throw new AlreadyPurchasedException();
        }
        this.accountId = accountId;
        this.reservation = false;
    }

    public void makeReservation(UUID accountId, BigDecimal funds) {
        if (price.compareTo(funds) != 0) {
            throw new InsufficientFundsException();
        }
        if (this.accountId != null && !this.accountId.equals(accountId)) {
            throw new AlreadyPurchasedException();
        }
        this.accountId = accountId;
        this.reservation = true;
    }

    public void cancelReservation(UUID accountId) {
        if (this.accountId != null && !this.accountId.equals(accountId)) {
            throw new AlreadyPurchasedException();
        } else {
            this.accountId = null;
            this.reservation = false;
        }
    }
}
