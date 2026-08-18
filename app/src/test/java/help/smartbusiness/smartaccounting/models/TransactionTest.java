package help.smartbusiness.smartaccounting.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.database.Cursor;

import org.junit.Test;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;

public class TransactionTest {

    @Test
    public void typeIsPurchaseTrueForSell() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE)).thenReturn(0);
        when(cursor.getString(0)).thenReturn(AccountingDbHelper.PURCHASE_TYPE_SELL);
        assertTrue(Transaction.typeIsPurchase(cursor));
    }

    @Test
    public void typeIsPurchaseTrueForBuy() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE)).thenReturn(0);
        when(cursor.getString(0)).thenReturn(AccountingDbHelper.PURCHASE_TYPE_BUY);
        assertTrue(Transaction.typeIsPurchase(cursor));
    }

    @Test
    public void typeIsPurchaseHandlesUpperCase() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE)).thenReturn(0);
        when(cursor.getString(0)).thenReturn("SELL");
        assertTrue(Transaction.typeIsPurchase(cursor));
    }

    @Test
    public void typeIsPurchaseFalseForCreditType() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE)).thenReturn(0);
        when(cursor.getString(0)).thenReturn(AccountingDbHelper.CREDIT_TYPE_CREDIT);
        assertFalse(Transaction.typeIsPurchase(cursor));
    }

    @Test
    public void typeIsCreditTrueForCredit() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_TYPE)).thenReturn(0);
        when(cursor.getString(0)).thenReturn(AccountingDbHelper.CREDIT_TYPE_CREDIT);
        assertTrue(Transaction.typeIsCredit(cursor));
    }

    @Test
    public void typeIsCreditTrueForDebit() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_TYPE)).thenReturn(0);
        when(cursor.getString(0)).thenReturn(AccountingDbHelper.CREDIT_TYPE_DEBIT);
        assertTrue(Transaction.typeIsCredit(cursor));
    }

    @Test
    public void typeIsCreditHandlesUpperCase() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_TYPE)).thenReturn(0);
        when(cursor.getString(0)).thenReturn("DEBIT");
        assertTrue(Transaction.typeIsCredit(cursor));
    }

    @Test
    public void typeIsCreditFalseForPurchaseType() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_TYPE)).thenReturn(0);
        when(cursor.getString(0)).thenReturn(AccountingDbHelper.PURCHASE_TYPE_SELL);
        assertFalse(Transaction.typeIsCredit(cursor));
    }
}
