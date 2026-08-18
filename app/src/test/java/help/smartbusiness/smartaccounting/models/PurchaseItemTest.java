package help.smartbusiness.smartaccounting.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.database.Cursor;

import org.junit.Test;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;

public class PurchaseItemTest {

    @Test
    public void constructorSetsAllFields() {
        PurchaseItem item = new PurchaseItem("Widget", 5, 100, 500);

        assertEquals("Widget", item.getName());
        assertEquals(5, item.getQuantity());
        assertEquals(100, item.getRate());
        assertEquals(500, item.getAmount());
        assertEquals(0, item.getId());
    }

    @Test
    public void settersUpdateFields() {
        PurchaseItem item = new PurchaseItem("Widget", 5, 100, 500);
        item.setId(21);
        item.setName("Gadget");
        item.setQuantity(10);
        item.setRate(200);
        item.setAmount(2000);

        assertEquals(21, item.getId());
        assertEquals("Gadget", item.getName());
        assertEquals(10, item.getQuantity());
        assertEquals(200, item.getRate());
        assertEquals(2000, item.getAmount());
    }

    @Test
    public void isValidTrueWhenAllPositive() {
        assertTrue(new PurchaseItem("Widget", 1, 1, 1).isValid());
    }

    @Test
    public void isValidFalseWhenNameNull() {
        assertFalse(new PurchaseItem(null, 1, 1, 1).isValid());
    }

    @Test
    public void isValidFalseWhenNameEmpty() {
        assertFalse(new PurchaseItem("", 1, 1, 1).isValid());
    }

    @Test
    public void isValidFalseWhenQuantityZero() {
        assertFalse(new PurchaseItem("Widget", 0, 1, 1).isValid());
    }

    @Test
    public void isValidFalseWhenRateZero() {
        assertFalse(new PurchaseItem("Widget", 1, 0, 1).isValid());
    }

    @Test
    public void isValidFalseWhenAmountZero() {
        assertFalse(new PurchaseItem("Widget", 1, 1, 0).isValid());
    }

    @Test
    public void isValidFalseWhenQuantityNegative() {
        assertFalse(new PurchaseItem("Widget", -1, 1, 1).isValid());
    }

    @Test
    public void fromCursorReadsAllFields() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.ID)).thenReturn(0);
        when(cursor.getColumnIndex(AccountingDbHelper.PI_COL_NAME)).thenReturn(1);
        when(cursor.getColumnIndex(AccountingDbHelper.PI_COL_QUANTITY)).thenReturn(2);
        when(cursor.getColumnIndex(AccountingDbHelper.PI_COL_RATE)).thenReturn(3);
        when(cursor.getColumnIndex(AccountingDbHelper.PI_COL_AMOUNT)).thenReturn(4);
        when(cursor.getLong(0)).thenReturn(6L);
        when(cursor.getString(1)).thenReturn("Widget");
        when(cursor.getLong(2)).thenReturn(5L);
        when(cursor.getLong(3)).thenReturn(100L);
        when(cursor.getLong(4)).thenReturn(500L);

        PurchaseItem item = PurchaseItem.fromCursor(cursor);

        assertEquals(6, item.getId());
        assertEquals("Widget", item.getName());
        assertEquals(5, item.getQuantity());
        assertEquals(100, item.getRate());
        assertEquals(500, item.getAmount());
    }
}
