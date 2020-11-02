package com.ubs.vwap.calculator;

import com.ubs.vwap.calculator.contract.Calculator;
import com.ubs.vwap.calculator.contract.TwoWayPrice;
import org.junit.Before;
import org.junit.Test;

import static com.ubs.vwap.calculator.GenerationUtil.marketUpdate;
import static com.ubs.vwap.calculator.GenerationUtil.marketUpdateZeroes;
import static com.ubs.vwap.calculator.contract.Instrument.*;
import static com.ubs.vwap.calculator.contract.Market.*;
import static com.ubs.vwap.calculator.contract.State.FIRM;
import static com.ubs.vwap.calculator.contract.State.INDICATIVE;
import static java.lang.Double.MIN_VALUE;
import static org.junit.Assert.assertEquals;

/**
 * Positive test cases
 *
 * The contract of the method is declared that the caller must extract the results between calls,
 * hence we reuse the same results instance across all the tests
 */
public class CalculatorPositiveTests {
    private Calculator calculator;
    private TwoWayPrice result;

    @Before
    public void setup() {
        calculator = new VwapCalculator();
    }

    @Test
    public void oneUpdate_validOutput() {
        result = calculator.applyMarketUpdate(marketUpdate(MARKET0, INSTRUMENT0, FIRM, 2, 10, 4, 20));

        assertEquals(INSTRUMENT0, result.getInstrument());
        assertEquals(FIRM, result.getState());
        assertEquals(2.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(10.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(4.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(20.0, result.getOfferAmount(), MIN_VALUE);
    }

    @Test
    public void threeInputsSameInstrument_validOutput() {
        result = calculator.applyMarketUpdate(marketUpdate(MARKET0, INSTRUMENT0, FIRM, 2, 10, 4, 20));
        assertEquals(2.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(10.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(4.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(20.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET1, INSTRUMENT0, FIRM, 2, 10, 4, 20));
        assertEquals(20.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(40.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET2, INSTRUMENT0, FIRM, 2, 10, 4, 20));
        assertEquals(30.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(60.0, result.getOfferAmount(), MIN_VALUE);
    }

    @Test
    public void threeInputsSameInstrumentVariedDepth_validOutput() {
        result = calculator.applyMarketUpdate(marketUpdate(MARKET0, INSTRUMENT0, FIRM, 5, 10, 20, 40));
        assertEquals(5.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(10.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(20.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(40.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET1, INSTRUMENT0, FIRM, 2, 20, 5, 20));
        assertEquals(3.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(30.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(15.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(60.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET2, INSTRUMENT0, FIRM, 1, 70, 4, 140));
        assertEquals(1.6, result.getBidPrice(), MIN_VALUE);
        assertEquals(100.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(7.3, result.getOfferPrice(), MIN_VALUE);
        assertEquals(200.0, result.getOfferAmount(), MIN_VALUE);
    }

    @Test
    public void twoInstrumentsOnSameMarket_eachOverridesTheirPrevious() {
        result = calculator.applyMarketUpdate(marketUpdate(MARKET0, INSTRUMENT0, FIRM, 5, 10, 20, 40));
        assertEquals(INSTRUMENT0, result.getInstrument());
        assertEquals(5.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(10.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(20.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(40.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET0, INSTRUMENT1, FIRM, 2, 20, 5, 20));
        assertEquals(INSTRUMENT1, result.getInstrument());
        assertEquals(2.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(20.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(5.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(20.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET0, INSTRUMENT0, FIRM, 1, 70, 4, 140));
        assertEquals(INSTRUMENT0, result.getInstrument());
        assertEquals(1.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(70.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(4.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(140.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET0, INSTRUMENT1, FIRM, 6, 12, 3, 4));
        assertEquals(INSTRUMENT1, result.getInstrument());
        assertEquals(6.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(12.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(3.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(4.0, result.getOfferAmount(), MIN_VALUE);
    }

    @Test
    public void atTheBounds_validOutput() {
        result = calculator.applyMarketUpdate(marketUpdate(MARKET49, INSTRUMENT19, FIRM, 5, 10, 20, 40));
        assertEquals(FIRM, result.getState());
        assertEquals(5.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(10.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(20.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(40.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET48, INSTRUMENT19, INDICATIVE, 6, 5, 10, 50));
        assertEquals(INDICATIVE, result.getState());
        assertEquals(5.333333333333333333, result.getBidPrice(), MIN_VALUE);
        assertEquals(15.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(14.44444444444444444, result.getOfferPrice(), MIN_VALUE);
        assertEquals(90.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET48, INSTRUMENT18, INDICATIVE, 5, 10, 20, 40));
        assertEquals(INDICATIVE, result.getState());
        assertEquals(5.0, result.getBidPrice(), MIN_VALUE);
        assertEquals(10.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(20.0, result.getOfferPrice(), MIN_VALUE);
        assertEquals(40.0, result.getOfferAmount(), MIN_VALUE);

        result = calculator.applyMarketUpdate(marketUpdate(MARKET49, INSTRUMENT19, FIRM, 3, 15, 2, 10));
        assertEquals(INDICATIVE, result.getState());
        assertEquals(3.75, result.getBidPrice(), MIN_VALUE);
        assertEquals(20.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(8.666666666666666666, result.getOfferPrice(), MIN_VALUE);
        assertEquals(60.0, result.getOfferAmount(), MIN_VALUE);

        // and back to FIRM just for fun
        result = calculator.applyMarketUpdate(marketUpdate(MARKET48, INSTRUMENT19, FIRM, 6, 5, 10, 50));
        assertEquals(FIRM, result.getState());
    }

    @Test
    public void singleIndicativeUpdate_makesOutputIndicative() {
        result = calculator.applyMarketUpdate(marketUpdateZeroes(MARKET0, INSTRUMENT0, FIRM));
        assertEquals(FIRM, result.getState());

        result = calculator.applyMarketUpdate(marketUpdateZeroes(MARKET1, INSTRUMENT0, FIRM));
        assertEquals(FIRM, result.getState());

        result = calculator.applyMarketUpdate(marketUpdateZeroes(MARKET2, INSTRUMENT0, FIRM));
        assertEquals(FIRM, result.getState());

        result = calculator.applyMarketUpdate(marketUpdateZeroes(MARKET3, INSTRUMENT0, INDICATIVE));
        assertEquals(INDICATIVE, result.getState());

        result = calculator.applyMarketUpdate(marketUpdateZeroes(MARKET4, INSTRUMENT0, FIRM));
        assertEquals(INDICATIVE, result.getState());
    }
}


