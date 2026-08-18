package help.smartbusiness.smartaccounting.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.database.Cursor;

import org.junit.Test;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;

public class CustomerTest {

    @Test
    public void constructorWithIdSetsAllFields() {
        Customer customer = new Customer(7, "Alice", "12 Elm St");

        assertEquals(7, customer.getId());
        assertEquals("Alice", customer.getName());
        assertEquals("12 Elm St", customer.getAddress());
    }

    @Test
    public void constructorWithoutIdLeavesIdZero() {
        Customer customer = new Customer("Bob", "Main Street");

        assertEquals(0, customer.getId());
        assertEquals("Bob", customer.getName());
        assertEquals("Main Street", customer.getAddress());
    }

    @Test
    public void settersUpdateFields() {
        Customer customer = new Customer("Bob", "Main Street");
        customer.setId(42);
        customer.setName("Robert");
        customer.setAddress("New Address");
        customer.setDue(500);

        assertEquals(42, customer.getId());
        assertEquals("Robert", customer.getName());
        assertEquals("New Address", customer.getAddress());
        assertEquals(500, customer.getDue());
    }

    @Test
    public void getFirstNameReturnsFirstToken() {
        assertEquals("Abhishek", new Customer("Abhishek Raj", "addr").getFirstName());
        assertEquals("Mary", new Customer("Mary Ann Smith", "addr").getFirstName());
        assertEquals("Solo", new Customer("Solo", "addr").getFirstName());
    }

    @Test
    public void getFirstNameHandlesMultipleWhitespace() {
        assertEquals("First", new Customer("First   Last", "addr").getFirstName());
        assertEquals("First", new Customer("First\tLast", "addr").getFirstName());
    }

    @Test
    public void isValidTrueForNonEmptyNameAndAddress() {
        assertTrue(new Customer("Name", "Address").isValid());
    }

    @Test
    public void isValidFalseForNullName() {
        assertFalse(new Customer(null, "Address").isValid());
    }

    @Test
    public void isValidFalseForEmptyName() {
        assertFalse(new Customer("", "Address").isValid());
    }

    @Test
    public void isValidFalseForNullAddress() {
        assertFalse(new Customer("Name", null).isValid());
    }

    @Test
    public void isValidFalseForEmptyAddress() {
        assertFalse(new Customer("Name", "").isValid());
    }

    @Test
    public void isValidIdTrueForPositive() {
        Customer customer = new Customer("Name", "Address");
        customer.setId(1);
        assertTrue(customer.isValidId());
    }

    @Test
    public void isValidIdFalseForZero() {
        Customer customer = new Customer("Name", "Address");
        customer.setId(0);
        assertFalse(customer.isValidId());
    }

    @Test
    public void isValidIdFalseForNegative() {
        Customer customer = new Customer("Name", "Address");
        customer.setId(-1);
        assertFalse(customer.isValidId());
    }

    @Test
    public void fromCursorReadsAllFields() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(anyString())).thenReturn(0);
        when(cursor.getColumnIndex(AccountingDbHelper.ID)).thenReturn(0);
        when(cursor.getColumnIndex(AccountingDbHelper.CUSTOMERS_COL_NAME)).thenReturn(1);
        when(cursor.getColumnIndex(AccountingDbHelper.CUSTOMERS_COL_ADDRESS)).thenReturn(2);
        when(cursor.getColumnIndex(AccountingDbHelper.CDV_DUE)).thenReturn(3);
        when(cursor.getLong(0)).thenReturn(99L);
        when(cursor.getString(1)).thenReturn("Charlie");
        when(cursor.getString(2)).thenReturn("789 Oak Ave");
        when(cursor.getLong(3)).thenReturn(1500L);

        Customer customer = Customer.fromCursor(cursor);

        assertEquals(99, customer.getId());
        assertEquals("Charlie", customer.getName());
        assertEquals("789 Oak Ave", customer.getAddress());
        assertEquals(1500, customer.getDue());
    }
}
