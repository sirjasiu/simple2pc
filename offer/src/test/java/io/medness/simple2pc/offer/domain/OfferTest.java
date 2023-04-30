package io.medness.simple2pc.offer.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class OfferTest {

    private static UUID ACCOUNT_ID1 = UUID.randomUUID();
    private static UUID ACCOUNT_ID2 = UUID.randomUUID();

    @Test
    public void shouldPurchaseOffer() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);

        // when
        offer.purchase(ACCOUNT_ID1, BigDecimal.ONE);

        // then
        assertThat(offer.getAccountId()).isEqualTo(ACCOUNT_ID1);
        assertThat(offer.isReservation()).isFalse();

    }

    @Test
    public void shouldNotPurchaseOfferIfNotEnoughFunds() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);

        // expect
        assertThatThrownBy(() -> offer.purchase(ACCOUNT_ID1, BigDecimal.ZERO))
                .isInstanceOf(InsufficientFundsException.class);

    }

    @Test
    public void shouldNotPurchaseAlreadyTakenOffer() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);
        offer.purchase(ACCOUNT_ID2, BigDecimal.ONE);

        // expect
        assertThatThrownBy(() -> offer.purchase(ACCOUNT_ID1, BigDecimal.ONE))
                .isInstanceOf(AlreadyPurchasedException.class);

    }

    @Test
    public void shouldNotPurchaseReservedOffer() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);
        offer.makeReservation(ACCOUNT_ID2, BigDecimal.ONE);

        // expect
        assertThatThrownBy(() -> offer.purchase(ACCOUNT_ID1, BigDecimal.ONE))
                .isInstanceOf(AlreadyPurchasedException.class);

    }

    @Test
    public void shouldPurchaseReservedOfferByTheSameAccount() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);
        offer.makeReservation(ACCOUNT_ID1, BigDecimal.ONE);

        // when
        offer.purchase(ACCOUNT_ID1, BigDecimal.ONE);

        // then
        assertThat(offer.getAccountId()).isEqualTo(ACCOUNT_ID1);
        assertThat(offer.isReservation()).isFalse();
    }
    @Test
    public void shouldMakeReservation() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);

        // when
        offer.makeReservation(ACCOUNT_ID1, BigDecimal.ONE);

        // then
        assertThat(offer.getAccountId()).isEqualTo(ACCOUNT_ID1);
        assertThat(offer.isReservation()).isTrue();

    }

    @Test
    public void shouldNotMakeReservationIfNotEnoughFunds() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);

        // expect
        assertThatThrownBy(() -> offer.makeReservation(ACCOUNT_ID1, BigDecimal.ZERO))
                .isInstanceOf(InsufficientFundsException.class);

    }

    @Test
    public void shouldNotMakeReservationAlreadyPurchasedOffer() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);
        offer.purchase(ACCOUNT_ID2, BigDecimal.ONE);

        // expect
        assertThatThrownBy(() -> offer.makeReservation(ACCOUNT_ID1, BigDecimal.ONE))
                .isInstanceOf(AlreadyPurchasedException.class);

    }

    @Test
    public void shouldNotMakeReservationAlreadyReservedOffer() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);
        offer.makeReservation(ACCOUNT_ID2, BigDecimal.ONE);

        // expect
        assertThatThrownBy(() -> offer.makeReservation(ACCOUNT_ID1, BigDecimal.ONE))
                .isInstanceOf(AlreadyPurchasedException.class);

    }

    @Test
    public void shouldCancelReservationWhenOfferNotReserver() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);

        // when
        offer.cancelReservation(ACCOUNT_ID1);

        // then
        assertThat(offer.getAccountId()).isNull();
        assertThat(offer.isReservation()).isFalse();

    }

    @Test
    public void shouldCancelReservationReservedByTheSameAccoutn() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);
        offer.makeReservation(ACCOUNT_ID1, BigDecimal.ONE);

        // when
        offer.cancelReservation(ACCOUNT_ID1);

        // then
        assertThat(offer.getAccountId()).isNull();
        assertThat(offer.isReservation()).isFalse();

    }

    @Test
    public void shouldNotCancelReservationReservedByOtherAccount() {
        // given
        Offer offer = new Offer("offer", BigDecimal.ONE);
        offer.makeReservation(ACCOUNT_ID2, BigDecimal.ONE);

        // expect
        assertThatThrownBy(() -> offer.cancelReservation(ACCOUNT_ID1))
                .isInstanceOf(AlreadyPurchasedException.class);

    }

}