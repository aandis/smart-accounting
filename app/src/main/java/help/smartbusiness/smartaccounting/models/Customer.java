package help.smartbusiness.smartaccounting.models;

import android.content.ContentValues;
import android.content.Context;
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
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
