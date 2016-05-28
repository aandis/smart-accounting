package help.smartbusiness.smartaccounting.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

/**
 * Created by gamerboy on 28/5/16.
 */
public class Credit {
    private long id;
    private Customer customer;
    private String date;
    private String remarks;
    private float amount;

    public Credit(Customer customer, String date, float amount, String remarks) {
        this.customer = customer;
        this.date = date;
        this.remarks = remarks;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isValid() {
        // Not empty validations.
        List<String> notEmpty = new ArrayList<>(Arrays.asList(
                customer.getName(), customer.getAddress(), date));
        for (String text : notEmpty) {
            if (text.isEmpty()) {
                return false;
            }
        }
        return amount >= 0;
    }

    public boolean insert(Context context) {
        if (getCustomer().insert(context)) {
            ContentValues values = new ContentValues();
            values.put(AccountingDbHelper.CREDIT_COL_CUSTOMER_ID, getCustomer().getId());
            values.put(AccountingDbHelper.CREDIT_COL_DATE, date);
            values.put(AccountingDbHelper.CREDIT_COL_AMOUNT, amount);
            values.put(AccountingDbHelper.CREDIT_COL_REMARKS, remarks);
            try {
                Uri newPurchase = context.getContentResolver().insert(getInsertUri(), values);
                setId(Long.parseLong(newPurchase.getLastPathSegment()));
            } catch (SQLiteException ex) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public Uri getInsertUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + getCustomer().getId()
                + "/" + AccountingProvider.CREDITS_BASE_PATH);
    }
}
