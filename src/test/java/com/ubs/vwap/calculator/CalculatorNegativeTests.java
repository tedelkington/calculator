package com.ubs.vwap.calculator;

import com.ubs.vwap.calculator.contract.Calculator;
import com.ubs.vwap.calculator.contract.TwoWayPrice;
import org.junit.Before;
import org.junit.Test;

import static com.ubs.vwap.calculator.GenerationUtil.marketUpdate;
import static com.ubs.vwap.calculator.GenerationUtil.marketUpdateZeroes;
import static com.ubs.vwap.calculator.contract.Instrument.INSTRUMENT0;
import static com.ubs.vwap.calculator.contract.Market.MARKET0;
import static com.ubs.vwap.calculator.contract.State.FIRM;
import static java.lang.Double.MIN_VALUE;
import static java.lang.Double.NaN;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Negative test cases
 */
public class CalculatorNegativeTests {
    private Calculator calculator;
    private TwoWayPrice result;

    @Before
    public void setup() {
        calculator = new VwapCalculator();
    }

    @Test
    public void nullInput_throwsNull() {
        assertThrows(NullPointerException.class, () -> calculator.applyMarketUpdate(null));
    }

    @Test
    public void zeroesUpdate_zerosAndNaNsOutput() {
        result = calculator.applyMarketUpdate(marketUpdateZeroes(MARKET0, INSTRUMENT0, FIRM));

        assertEquals(INSTRUMENT0, result.getInstrument());
        assertEquals(FIRM, result.getState());
        assertEquals(NaN, result.getBidPrice(), MIN_VALUE);
        assertEquals(0.0, result.getBidAmount(), MIN_VALUE);
        assertEquals(NaN, result.getOfferPrice(), MIN_VALUE);
        assertEquals(0.0, result.getOfferAmount(), MIN_VALUE);
    }

    @Test
    public void nanInput_validOutput() {
        result = calculator.applyMarketUpdate(marketUpdate(MARKET0, INSTRUMENT0, FIRM, NaN, NaN, NaN, NaN));

        assertEquals(INSTRUMENT0, result.getInstrument());
        assertEquals(FIRM, result.getState());
        assertEquals(NaN, result.getBidPrice(), MIN_VALUE);
        assertEquals(NaN, result.getBidAmount(), MIN_VALUE);
        assertEquals(NaN, result.getOfferPrice(), MIN_VALUE);
        assertEquals(NaN, result.getOfferAmount(), MIN_VALUE);
    }
}
