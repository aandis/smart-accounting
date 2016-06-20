package help.smartbusiness.smartaccounting.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

/**
 * Created by gamerboy on 26/5/16.
 */
public class Customer {
    private long id;
    private String name;
    private String address;
    private float due;

    public Customer(long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public static Customer fromCursor(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndex(AccountingDbHelper.ID));
        String name = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.CUSTOMERS_COL_NAME));
        String address = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.CUSTOMERS_COL_ADDRESS));
        float due = cursor.getFloat(
                cursor.getColumnIndex(AccountingDbHelper.CDV_DUE));
        Customer customer = new Customer(id, name, address);
        customer.setDue(due);
        return customer;
    }

    public Customer(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return name.split("\\s+")[0];
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    protected boolean insert(Context context) {
        ContentValues values = new ContentValues();
        values.put(AccountingDbHelper.CUSTOMERS_COL_NAME, getName());
        values.put(AccountingDbHelper.CUSTOMERS_COL_ADDRESS, getAddress());
        try {
            Uri newCustomer = context.getContentResolver().insert(
                    Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI), values);
            setId(Long.parseLong(newCustomer.getLastPathSegment()));
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isValid() {
        if (getName() == null || getName().isEmpty()) {
            return false;
        }
        if (getAddress() == null || getAddress().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isValidId() {
        return id > 0;
    }

    public float getDue() {
        return due;
    }

    public void setDue(float due) {
        this.due = due;
    }

    public boolean update(Context context) {
        // TODO on a background thread.
        ContentValues values = new ContentValues();
        values.put(AccountingDbHelper.CUSTOMERS_COL_NAME, getName());
        values.put(AccountingDbHelper.CUSTOMERS_COL_ADDRESS, getAddress());
        try {
            context.getContentResolver().update(getUpdateUri(), values,
                    AccountingDbHelper.ID + " = " + getId(), null);
        } catch (NullPointerException ignored) {
            return false;
        }
        return true;
    }


    public boolean delete(Context context) {
        // TODO Do in Background thread.
        int deleted = context.getContentResolver().delete(getDeleteUri(),
                AccountingDbHelper.ID + "=" + getId(), null);
        if (deleted > 0) {
            return true;
        }
        return false;
    }

    private Uri getDeleteUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + getId());
    }

    private Uri getUpdateUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + getId());
    }


}
