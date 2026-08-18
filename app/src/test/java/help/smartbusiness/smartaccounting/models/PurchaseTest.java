package help.smartbusiness.smartaccounting.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.database.Cursor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;

public class PurchaseTest {

    @Test
    public void purchaseTypeEnumMapsToDbStrings() {
        assertEquals(AccountingDbHelper.PURCHASE_TYPE_SELL,
                Purchase.PurchaseType.SELL.getDbType());
        assertEquals(AccountingDbHelper.PURCHASE_TYPE_BUY,
                Purchase.PurchaseType.BUY.getDbType());
    }

    @Test
    public void fullConstructorSetsAllFields() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 5000, "2024-04-01 10:00:00");

        assertEquals("2024-04-01", purchase.getDate());
        assertEquals("note", purchase.getRemarks());
        assertEquals(Purchase.PurchaseType.SELL, purchase.getType());
        assertEquals(5000, purchase.getAmount());
        assertEquals("2024-04-01 10:00:00", purchase.getCreatedAt());
        assertNotNull(purchase.getPurchaseItems());
        assertTrue(purchase.getPurchaseItems().isEmpty());
    }

    @Test
    public void shortConstructorLeavesCreatedAtNull() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.BUY, 1000);
        assertNull(purchase.getCreatedAt());
    }

    @Test
    public void settersUpdateFields() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 1000);
        purchase.setId(3);
        purchase.setDate("2024-05-01");
        purchase.setRemarks("updated");
        purchase.setType(Purchase.PurchaseType.BUY);
        purchase.setAmount(2000);
        purchase.setCreatedAt("2024-05-01 12:00:00");
        Customer customer = new Customer("Alice", "Addr");
        purchase.setCustomer(customer);
        List<PurchaseItem> items = Arrays.asList(
                new PurchaseItem("A", 1, 100, 100));
        purchase.setPurchaseItems(items);

        assertEquals(3, purchase.getId());
        assertEquals("2024-05-01", purchase.getDate());
        assertEquals("updated", purchase.getRemarks());
        assertEquals(Purchase.PurchaseType.BUY, purchase.getType());
        assertEquals(2000, purchase.getAmount());
        assertEquals("2024-05-01 12:00:00", purchase.getCreatedAt());
        assertEquals(customer, purchase.getCustomer());
        assertEquals(items, purchase.getPurchaseItems());
    }

    @Test
    public void getTransactionTypeReturnsPurchaseClass() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 1000);
        assertEquals(Purchase.class, purchase.getTransactionType());
    }

    @Test
    public void isValidTrueWhenAllValid() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 1000);
        assertTrue(purchase.isValid(false, false));
    }

    @Test
    public void isValidFalseWhenTypeNull() {
        Purchase purchase = new Purchase("2024-04-01", "note", null, 1000);
        assertFalse(purchase.isValid(false, false));
    }

    @Test
    public void isValidFalseWhenDateEmpty() {
        Purchase purchase = new Purchase("", "note",
                Purchase.PurchaseType.SELL, 1000);
        assertFalse(purchase.isValid(false, false));
    }

    @Test
    public void isValidFalseWhenCustomerRequiredButNull() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 1000);
        assertFalse(purchase.isValid(true, false));
    }

    @Test
    public void isValidFalseWhenCustomerRequiredButInvalid() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 1000);
        purchase.setCustomer(new Customer("", ""));
        assertFalse(purchase.isValid(true, false));
    }

    @Test
    public void isValidFalseWhenPurchaseItemInvalid() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 1000);
        List<PurchaseItem> items = new ArrayList<>();
        items.add(new PurchaseItem("", 0, 0, 0));
        purchase.setPurchaseItems(items);
        assertFalse(purchase.isValid(false, true));
    }

    @Test
    public void isValidTrueWithValidCustomerAndItems() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 1000);
        purchase.setCustomer(new Customer("Name", "Address"));
        List<PurchaseItem> items = new ArrayList<>();
        items.add(new PurchaseItem("A", 1, 100, 100));
        purchase.setPurchaseItems(items);
        assertTrue(purchase.isValid(true, true));
    }

    @Test
    public void isValidTrueWithEmptyItemListWhenValidationRequested() {
        Purchase purchase = new Purchase("2024-04-01", "note",
                Purchase.PurchaseType.SELL, 1000);
        purchase.setPurchaseItems(new ArrayList<>());
        assertTrue(purchase.isValid(false, true));
    }

    @Test
    public void fromCursorReadsAllFields() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(AccountingDbHelper.ID)).thenReturn(0);
        when(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_DATE)).thenReturn(1);
        when(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_REMARKS)).thenReturn(2);
        when(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE)).thenReturn(3);
        when(cursor.getColumnIndex(AccountingDbHelper.CPV_AMOUNT)).thenReturn(4);
        when(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_CREATED_AT)).thenReturn(5);
        when(cursor.getLong(0)).thenReturn(7L);
        when(cursor.getString(1)).thenReturn("2024-06-01");
        when(cursor.getString(2)).thenReturn("goods");
        when(cursor.getString(3)).thenReturn(AccountingDbHelper.PURCHASE_TYPE_SELL);
        when(cursor.getLong(4)).thenReturn(12345L);
        when(cursor.getString(5)).thenReturn("2024-06-01 09:00:00");

        Purchase purchase = Purchase.fromCursor(cursor);

        assertEquals(7, purchase.getId());
        assertEquals("2024-06-01", purchase.getDate());
        assertEquals("goods", purchase.getRemarks());
        assertEquals(Purchase.PurchaseType.SELL, purchase.getType());
        assertEquals(12345, purchase.getAmount());
        assertEquals("2024-06-01 09:00:00", purchase.getCreatedAt());
    }
}
