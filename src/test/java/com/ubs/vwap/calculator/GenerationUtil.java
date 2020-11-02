package com.ubs.vwap.calculator;

import com.ubs.vwap.calculator.contract.*;

// TODO I haven't added final qualifier to method params inside the test classes deliberately - I'm fine with either convention, in or out
public final class GenerationUtil {
    public static MarketUpdate marketUpdate(Market market, Instrument instrument, State state, double bidPrice,
                                            double bidAmount, double offerPrice, double offerAmount) {
        final TwoWayPrice twoWayPrice = new TestTwoWayPrice(instrument, state, bidPrice, bidAmount, offerPrice, offerAmount);
        return new TestMarketUpdate(market, twoWayPrice);
    }

    public static MarketUpdate marketUpdateZeroes(Market market, Instrument instrument, State state) {
        final TwoWayPrice twoWayPrice = new TestTwoWayPrice(instrument, state, 0.0, 0.0, 0.0, 0.0);
        return new TestMarketUpdate(market, twoWayPrice);
    }

    private GenerationUtil() {}
}
