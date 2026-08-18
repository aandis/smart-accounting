package help.smartbusiness.smartaccounting.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DateParserTest {

    @Test
    public void fromSqliteDateConvertsToDdMmYyyy() {
        assertEquals("15-03-2024", DateParser.fromSqliteDate("2024-03-15"));
    }

    @Test
    public void fromSqliteDateHandlesEndOfMonth() {
        assertEquals("31-12-2023", DateParser.fromSqliteDate("2023-12-31"));
    }

    @Test
    public void fromSqliteDateReturnsEmptyOnInvalid() {
        assertEquals("", DateParser.fromSqliteDate("not-a-date"));
    }

    @Test
    public void fromSqliteDateReturnsEmptyOnEmpty() {
        assertEquals("", DateParser.fromSqliteDate(""));
    }

    @Test
    public void toSqliteDateConvertsToYyyyMmDd() {
        assertEquals("2024-03-15", DateParser.toSqliteDate("15-03-2024"));
    }

    @Test
    public void toSqliteDateHandlesEndOfMonth() {
        assertEquals("2023-12-31", DateParser.toSqliteDate("31-12-2023"));
    }

    @Test
    public void toSqliteDateReturnsEmptyOnInvalid() {
        assertEquals("", DateParser.toSqliteDate("not-a-date"));
    }

    @Test
    public void toSqliteDateReturnsEmptyOnEmpty() {
        assertEquals("", DateParser.toSqliteDate(""));
    }

    @Test
    public void roundTripFromSqliteAndBack() {
        String sqlite = "2024-07-04";
        String normal = DateParser.fromSqliteDate(sqlite);
        assertEquals(sqlite, DateParser.toSqliteDate(normal));
    }

    @Test
    public void roundTripFromNormalAndBack() {
        String normal = "04-07-2024";
        String sqlite = DateParser.toSqliteDate(normal);
        assertEquals(normal, DateParser.fromSqliteDate(sqlite));
    }

    @Test
    public void padSqliteDatePreservesValidDate() {
        assertEquals("2024-03-15", DateParser.padSqliteDate("2024-03-15"));
    }

    @Test
    public void padSqliteDateReturnsNullOnInvalid() {
        assertNull(DateParser.padSqliteDate("not-a-date"));
    }
}
