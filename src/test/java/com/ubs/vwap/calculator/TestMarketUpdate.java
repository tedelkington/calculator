package com.ubs.vwap.calculator;

import com.ubs.vwap.calculator.contract.Market;
import com.ubs.vwap.calculator.contract.MarketUpdate;
import com.ubs.vwap.calculator.contract.TwoWayPrice;

public final class TestMarketUpdate implements MarketUpdate {
    private final Market market;
    private final TwoWayPrice twoWayPrice;

    public TestMarketUpdate(Market market, TwoWayPrice twoWayPrice) {
        this.market = market;
        this.twoWayPrice = twoWayPrice;
    }

    @Override
    public Market getMarket() {
        return market;
    }

    @Override
    public TwoWayPrice getTwoWayPrice() {
        return twoWayPrice;
    }
}
