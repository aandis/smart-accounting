package help.smartbusiness.smartaccounting.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.database.Cursor;

import org.junit.Test;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;

public class CreditTest {

    @Test
    public void creditTypeEnumMapsToDbStrings() {
        assertEquals(AccountingDbHelper.CREDIT_TYPE_CREDIT,
                Credit.CreditType.CREDIT.getDbType());
        assertEquals(AccountingDbHelper.CREDIT_TYPE_DEBIT,
                Credit.CreditType.DEBIT.getDbType());
    }

    @Test
    public void fullConstructorSetsAllFields() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, 250, "2024-01-15 10:00:00");

        assertEquals("2024-01-15", credit.getDate());
        assertEquals("note", credit.getRemarks());
        assertEquals(Credit.CreditType.CREDIT, credit.getType());
        assertEquals(250, credit.getAmount());
        assertEquals("2024-01-15 10:00:00", credit.getCreatedAt());
    }

    @Test
    public void shortConstructorLeavesCreatedAtNull() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.DEBIT, 100);

        assertNull(credit.getCreatedAt());
    }

    @Test
    public void settersUpdateFields() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, 100);
        credit.setId(9);
        credit.setDate("2024-02-01");
        credit.setRemarks("updated");
        credit.setType(Credit.CreditType.DEBIT);
        credit.setAmount(500);
        credit.setCreatedAt("2024-02-01 12:00:00");
        Customer customer = new Customer("Alice", "Addr");
        credit.setCustomer(customer);

        assertEquals(9, credit.getId());
        assertEquals("2024-02-01", credit.getDate());
        assertEquals("updated", credit.getRemarks());
        assertEquals(Credit.CreditType.DEBIT, credit.getType());
        assertEquals(500, credit.getAmount());
        assertEquals("2024-02-01 12:00:00", credit.getCreatedAt());
        assertEquals(customer, credit.getCustomer());
    }

    @Test
    public void getTransactionTypeReturnsCreditClass() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, 100);
        assertEquals(Credit.class, credit.getTransactionType());
    }

    @Test
    public void isValidTrueWhenAllFieldsPresent() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, 100);
        assertTrue(credit.isValid(false));
    }

    @Test
    public void isValidFalseWhenTypeNull() {
        Credit credit = new Credit("2024-01-15", "note", null, 100);
        assertFalse(credit.isValid(false));
    }

    @Test
    public void isValidFalseWhenDateEmpty() {
        Credit credit = new Credit("", "note",
                Credit.CreditType.CREDIT, 100);
        assertFalse(credit.isValid(false));
    }

    @Test
    public void isValidFalseWhenAmountZero() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, 0);
        assertFalse(credit.isValid(false));
    }

    @Test
    public void isValidFalseWhenAmountNegative() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, -5);
        assertFalse(credit.isValid(false));
    }

    @Test
    public void isValidWithCustomerFalseWhenCustomerNull() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, 100);
        assertFalse(credit.isValid(true));
    }

    @Test
    public void isValidWithCustomerFalseWhenCustomerInvalid() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, 100);
        credit.setCustomer(new Customer("", ""));
        assertFalse(credit.isValid(true));
    }

    @Test
    public void isValidWithCustomerTrueWhenValid() {
        Credit credit = new Credit("2024-01-15", "note",
                Credit.CreditType.CREDIT, 100);
        credit.setCustomer(new Customer("Name", "Address"));
        assertTrue(credit.isValid(true));
    }

    @Test
    public void fromCursorReadsAllFields() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.ID)).thenReturn(0);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_DATE)).thenReturn(1);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_REMARKS)).thenReturn(2);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_TYPE)).thenReturn(3);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_AMOUNT)).thenReturn(4);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_CREATED_AT)).thenReturn(5);
        when(cursor.getLong(0)).thenReturn(11L);
        when(cursor.getString(1)).thenReturn("2024-03-01");
        when(cursor.getString(2)).thenReturn("cash");
        when(cursor.getString(3)).thenReturn(AccountingDbHelper.CREDIT_TYPE_DEBIT);
        when(cursor.getLong(4)).thenReturn(999L);
        when(cursor.getString(5)).thenReturn("2024-03-01 10:00:00");

        Credit credit = Credit.fromCursor(cursor);

        assertEquals(11, credit.getId());
        assertEquals("2024-03-01", credit.getDate());
        assertEquals("cash", credit.getRemarks());
        assertEquals(Credit.CreditType.DEBIT, credit.getType());
        assertEquals(999, credit.getAmount());
        assertEquals("2024-03-01 10:00:00", credit.getCreatedAt());
    }
}
