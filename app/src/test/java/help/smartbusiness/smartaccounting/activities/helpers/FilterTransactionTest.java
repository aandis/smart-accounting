package help.smartbusiness.smartaccounting.activities.helpers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class FilterTransactionTest {

    @Test
    public void getFilterQueryReturnsSqlWhenBothDatesPresent() {
        assertEquals("date >= ? AND date <= ?",
                FilterTransaction.getFilterQuery("2024-01-01", "2024-12-31"));
    }

    @Test
    public void getFilterQueryReturnsNullWhenFromDateNull() {
        assertNull(FilterTransaction.getFilterQuery(null, "2024-12-31"));
    }

    @Test
    public void getFilterQueryReturnsNullWhenToDateNull() {
        assertNull(FilterTransaction.getFilterQuery("2024-01-01", null));
    }

    @Test
    public void getFilterQueryReturnsNullWhenBothNull() {
        assertNull(FilterTransaction.getFilterQuery(null, null));
    }

    @Test
    public void getFilterArgsReturnsBothDatesWhenPresent() {
        assertArrayEquals(new String[]{"2024-01-01", "2024-12-31"},
                FilterTransaction.getFilterArgs("2024-01-01", "2024-12-31"));
    }

    @Test
    public void getFilterArgsReturnsNullWhenFromDateNull() {
        assertNull(FilterTransaction.getFilterArgs(null, "2024-12-31"));
    }

    @Test
    public void getFilterArgsReturnsNullWhenToDateNull() {
        assertNull(FilterTransaction.getFilterArgs("2024-01-01", null));
    }

    @Test
    public void getFilterArgsReturnsNullWhenBothNull() {
        assertNull(FilterTransaction.getFilterArgs(null, null));
    }
}
