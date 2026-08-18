package help.smartbusiness.smartaccounting.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class UtilsTest {

    private static final double DELTA = 1e-9;

    @Test
    public void parseDoubleValidInteger() {
        assertEquals(42.0, Utils.parseDouble("42"), DELTA);
    }

    @Test
    public void parseDoubleValidDecimal() {
        assertEquals(3.14, Utils.parseDouble("3.14"), DELTA);
    }

    @Test
    public void parseDoubleNegative() {
        assertEquals(-7.5, Utils.parseDouble("-7.5"), DELTA);
    }

    @Test
    public void parseDoubleInvalidReturnsNegativeOne() {
        assertEquals(-1.0, Utils.parseDouble("not a number"), DELTA);
    }

    @Test
    public void parseDoubleEmptyReturnsNegativeOne() {
        assertEquals(-1.0, Utils.parseDouble(""), DELTA);
    }

    @Test
    public void parseLongValid() {
        assertEquals(Long.valueOf(1234L), Utils.parseLong("1234"));
    }

    @Test
    public void parseLongNegative() {
        assertEquals(Long.valueOf(-99L), Utils.parseLong("-99"));
    }

    @Test
    public void parseLongInvalidReturnsNegativeOne() {
        assertEquals(Long.valueOf(-1L), Utils.parseLong("abc"));
    }

    @Test
    public void parseLongEmptyReturnsNegativeOne() {
        assertEquals(Long.valueOf(-1L), Utils.parseLong(""));
    }

    @Test
    public void parseLongDecimalReturnsNegativeOne() {
        assertEquals(Long.valueOf(-1L), Utils.parseLong("3.14"));
    }

    @Test
    public void convertLongToDecimalWholeRupees() {
        assertEquals("2.42", Utils.convertLongToDecimal("242"));
    }

    @Test
    public void convertLongToDecimalLargerNumber() {
        assertEquals("36.93", Utils.convertLongToDecimal("3693"));
    }

    @Test
    public void convertLongToDecimalZero() {
        assertEquals("0.0", Utils.convertLongToDecimal("0"));
    }

    @Test
    public void convertLongToDecimalSingleDigit() {
        assertEquals("0.05", Utils.convertLongToDecimal("5"));
    }

    @Test
    public void convertLongToDecimalNullReturnsNull() {
        assertNull(Utils.convertLongToDecimal(null));
    }

    @Test
    public void convertLongToDecimalEmptyReturnsNull() {
        assertNull(Utils.convertLongToDecimal(""));
    }

    @Test
    public void convertLongToDecimalInvalidReturnsNull() {
        assertNull(Utils.convertLongToDecimal("abc"));
    }
}
