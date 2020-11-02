package com.ubs.vwap.calculator;

import com.ubs.vwap.calculator.contract.Calculator;
import com.ubs.vwap.calculator.contract.Instrument;
import com.ubs.vwap.calculator.contract.MarketUpdate;
import com.ubs.vwap.calculator.contract.TwoWayPrice;

import java.util.EnumMap;
import java.util.Map;

/**
 * VWAP calculators - uses EnumMap which is backed by a simple, contiguous array in memory which will be nice and quick
 * for traversing (O)n and retrieval (O)1 (and there will be no amortized cost since there won't be any resizing
 * and no any possibility of collisions (we use a EnumMap which is pre-populated with its Enum's size))
 *
 * We want zero GC - it's the caller's responsibility to extract the primitive results from {@link VwapTwoWayPrice}
 * before calling us again (we reuse that object for each instrument)
 *
 * We could consider changing the interface signature and have the caller send its own instance of {@link TwoWayPrice}
 * then the client can specify the reset-call-extract order itself
 */
// TODO we could consider renaming "price" to "value" across the board - we might want to use this logic for
//  yield or rate-based instruments - "value" would be a more agnostic name
public final class VwapCalculator implements Calculator {
    private final Map<Instrument, InstrumentTwoWayPrices> prices;

    /** Pre-populate our cache - we have enums for keys so we know our max capacity and then we're ready to as the updates
     * come in, rather than spending time creating caches for keys not yet seen when we should be just calculating */
    public VwapCalculator() {
        prices = new EnumMap<>(Instrument.class);

        // explicit iteration to guarantee no GC. A standard implicit loop would also be ok I think, since values()
        // returns an array (ie there's no implicit new of an iterator) eg
        // for (final Instrument instrument : Instrument.values())
        final Instrument[] values = Instrument.values();
        for (int i = 0; i < values.length; i++) {
            final Instrument instrument = values[i];
            prices.put(instrument, new InstrumentTwoWayPrices(instrument));
        }
    }

    /** Given this method receives an already complex object {@link MarketUpdate}, it does not validate the input and
     * leaves that work to the calling/creator of {@link MarketUpdate}. It's inefficient and in fact error prone to be
     * constantly (re)validating state. Do the technical and business validation, mainly, at the entry and exit points
     * to each and every JVM - and then have sound tests validating how the process/flow reacts should there be an
     * unexpected exception */
    @Override
    public TwoWayPrice applyMarketUpdate(final MarketUpdate marketUpdate) {
        final Instrument instrument = marketUpdate.getTwoWayPrice().getInstrument();
        final InstrumentTwoWayPrices instrumentPrices = prices.get(instrument);
        return instrumentPrices.calculateVwap(marketUpdate.getMarket(), marketUpdate.getTwoWayPrice());
    }
}
