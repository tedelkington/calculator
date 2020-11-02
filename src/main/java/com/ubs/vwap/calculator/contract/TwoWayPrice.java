package com.ubs.vwap.calculator.contract;

public interface TwoWayPrice {
    Instrument getInstrument();
    State getState();
    // fyi - I reordered this from the pdf - only change made to the spec
    double getBidPrice();
    double getBidAmount();
    double getOfferPrice();
    double getOfferAmount();
}
