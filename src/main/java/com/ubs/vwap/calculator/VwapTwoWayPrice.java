package com.ubs.vwap.calculator;

import com.ubs.vwap.calculator.contract.Instrument;
import com.ubs.vwap.calculator.contract.State;
import com.ubs.vwap.calculator.contract.TwoWayPrice;

import static com.ubs.vwap.calculator.contract.State.FIRM;
import static com.ubs.vwap.calculator.contract.State.INDICATIVE;

/**
 * Performs the running aggregate vwap calculations. This is just a calculator and will have a single instance per
 * calculation "path" - therefore call {@link VwapTwoWayPrice#reset()} after
 * {@link VwapTwoWayPrice#applyMarketUpdate(TwoWayPrice)} ()} calls are complete (or before you start)
 *
 * We expose this class to the original client caller - hence the 2 update methods are package private
 */
public final class VwapTwoWayPrice implements TwoWayPrice {
    private final Instrument instrument;

    private State state;
    // TODO we could consider longs instead of doubles throughout the API - avoids floating point errors etc
    private double bidPrice;
    private double bidAmount;
    private double offerPrice;
    private double offerAmount;

    public VwapTwoWayPrice(final Instrument instrument) {
        this.instrument = instrument;
    }

    void applyMarketUpdate(final TwoWayPrice twoWayPrice) {
        bidPrice += twoWayPrice.getBidPrice() * twoWayPrice.getBidAmount();
        bidAmount += twoWayPrice.getBidAmount();
        offerPrice += twoWayPrice.getOfferPrice() * twoWayPrice.getOfferAmount();
        offerAmount += twoWayPrice.getOfferAmount();

        if (INDICATIVE == twoWayPrice.getState()) {
            state = INDICATIVE;
        }
    }

    void reset() {
        state = FIRM;
        bidPrice = 0.0;
        bidAmount = 0.0;
        offerPrice = 0.0;
        offerAmount = 0.0;
    }

    @Override
    public Instrument getInstrument() {
        return instrument;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public double getBidPrice() {
        return bidPrice / bidAmount;
    }

    @Override
    public double getBidAmount() {
        return bidAmount;
    }

    @Override
    public double getOfferPrice() {
        return offerPrice / offerAmount;
    }

    @Override
    public double getOfferAmount() {
        return offerAmount;
    }
}
