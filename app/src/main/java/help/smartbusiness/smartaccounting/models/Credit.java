package help.smartbusiness.smartaccounting.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

/**
 * Created by gamerboy on 28/5/16.
 */
public class Credit extends Transaction {

    public enum CreditType {

        CREDIT(AccountingDbHelper.CREDIT_TYPE_CREDIT), DEBIT(AccountingDbHelper.CREDIT_TYPE_DEBIT);

        private String type;

        private CreditType(String type) {
            this.type = type;
        }

        public String getDbType() {
            return type;
        }
    }

    private long id;
    private Customer customer;
    private String date;
    private String remarks;
    private CreditType type;
    private float amount;

    public Credit(String date, String remarks, CreditType type, float amount) {
        this.date = date;
        this.remarks = remarks;
        this.type = type;
        this.amount = amount;
    }

    public static Credit fromCursor(Cursor cursor) {
        String date = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_DATE));
        String remarks = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_REMARKS));
        CreditType type = CreditType.valueOf(cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.CREDIT_COL_TYPE)).toUpperCase());
        float amount = cursor.getFloat(cursor.getColumnIndex(
                AccountingDbHelper.CREDIT_COL_AMOUNT));
        Credit c = new Credit(date, remarks, type, amount);
        c.setId(cursor.getLong(cursor.getColumnIndex(AccountingDbHelper.ID)));
        return c;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Class getTransactionType() {
        return Credit.class;
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

    public CreditType getType() {
        return type;
    }

    public void setType(CreditType type) {
        this.type = type;
    }

    public boolean isValid(boolean validateCustomer) {
        if (validateCustomer) {
            if (getCustomer() == null || !getCustomer().isValid()) {
                return false;
            }
        }

        // Type validation.
        if (getType() == null) {
            return false;
        }

        // Not empty validations.
        if (getDate().isEmpty()) {
            return false;
        }
        return amount > 0;
    }

    public boolean insert(Context context) {
        boolean customerInserted = getCustomer().isValidId() || getCustomer().insert(context);
        if (customerInserted) {
            ContentValues values = new ContentValues();
            values.put(AccountingDbHelper.CREDIT_COL_CUSTOMER_ID, getCustomer().getId());
            values.put(AccountingDbHelper.CREDIT_COL_DATE, date);
            values.put(AccountingDbHelper.CREDIT_COL_AMOUNT, amount);
            values.put(AccountingDbHelper.CREDIT_COL_REMARKS, remarks);
            values.put(AccountingDbHelper.CREDIT_COL_TYPE, getType().getDbType());
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

    public boolean update(Context context) {
        ContentValues values = new ContentValues();
        values.put(AccountingDbHelper.CREDIT_COL_DATE, date);
        values.put(AccountingDbHelper.CREDIT_COL_AMOUNT, amount);
        values.put(AccountingDbHelper.CREDIT_COL_REMARKS, remarks);
        values.put(AccountingDbHelper.CREDIT_COL_TYPE, getType().getDbType());
        try {
            context.getContentResolver().update(getUpdateUri(), values,
                    AccountingDbHelper.ID + " = " + getId(), null);
        } catch (NullPointerException ignored) {
            return false;
        }
        return true;
    }

    public boolean delete(Context context) {
        return false;
    }

    public Uri getInsertUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + getCustomer().getId()
                + "/" + AccountingProvider.CREDITS_BASE_PATH);
    }

    private Uri getUpdateUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + AccountingProvider.CREDITS_BASE_PATH
                + "/" + getId());
    }
}
