package com.ubs.vwap.calculator.contract;

public interface MarketUpdate {
    Market getMarket();
    TwoWayPrice getTwoWayPrice();
}
