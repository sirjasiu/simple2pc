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

    @Column(name = "buyer_id")
    private UUID buyerId;

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

    public UUID getBuyerId() {
        return buyerId;
    }

    public boolean isReservation() {
        return reservation;
    }

    public void purchase(UUID buyerId, BigDecimal funds) {
        if (price.compareTo(funds) != 0) {
            throw new InsufficientFundsException();
        }
        if (this.buyerId != null && !this.buyerId.equals(buyerId)) {
            throw new AlreadyPurchasedException();
        }
        this.buyerId = buyerId;
        this.reservation = false;
    }

    public void makeReservation(UUID buyerId, BigDecimal funds) {
        if (price.compareTo(funds) != 0) {
            throw new InsufficientFundsException();
        }
        if (this.buyerId != null && !this.buyerId.equals(buyerId)) {
            throw new AlreadyPurchasedException();
        }
        this.buyerId = buyerId;
        this.reservation = true;
    }

    public void cancelReservation(UUID buyerId) {
        if (this.buyerId != null && !this.buyerId.equals(buyerId)) {
            throw new AlreadyPurchasedException();
        } else {
            this.buyerId = null;
            this.reservation = false;
        }
    }
}
