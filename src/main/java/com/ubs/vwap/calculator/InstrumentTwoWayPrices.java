package com.ubs.vwap.calculator;

import com.ubs.vwap.calculator.contract.Instrument;
import com.ubs.vwap.calculator.contract.Market;
import com.ubs.vwap.calculator.contract.TwoWayPrice;
import org.agrona.collections.Int2ObjectHashMap;

import java.util.Map;


/** A specific class to cache each instrument's {@link TwoWayPrice}. Use agrona map to there's no GC created via
 * repeated calls to iterator() (agrona will guarantee same iterator for each key). Also no boxing on the int
 *
 * It also separates concerns a little, as compared to a map of maps, making things clearer. And we can implement
 * this in different ways if we think the following is not optimal for the expected data (perhaps we know that
 * we're guaranteed to get updates for all instruments and markets at SOD - perhaps then we just use flat arrays
 * for both maps/caches) */
public final class InstrumentTwoWayPrices {
    private final Map<Integer, TwoWayPrice> twoWayPrices = new Int2ObjectHashMap<>(Market.values().length, 0.99f); // we'll never resize - they'll bump our capacity up to 64
    private final VwapTwoWayPrice result;

    public InstrumentTwoWayPrices(final Instrument instrument) {
        result = new VwapTwoWayPrice(instrument);
    }

    /** We want zero GC - it's the caller's responsibility to extract the primitive results from {@link VwapTwoWayPrice}
     * before calling us again (we reuse that object for each instrument) */
    public VwapTwoWayPrice calculateVwap(final Market market, final TwoWayPrice twoWayPrice) {
        twoWayPrices.put(market.ordinal(), twoWayPrice);
        result.reset();

        for (final TwoWayPrice price : twoWayPrices.values()) { // won't create GC - the iterator is reused
            result.applyMarketUpdate(price);
        }

        return result;
    }
}
