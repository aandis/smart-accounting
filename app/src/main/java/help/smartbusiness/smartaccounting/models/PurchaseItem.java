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
public class PurchaseItem {
    private long id;
    private String name;
    private float quantity;
    private float rate;
    private float amount;

    public PurchaseItem(String name, float quantity, float rate, float amount) {
        this.name = name;
        this.quantity = quantity;
        this.rate = rate;
        this.amount = amount;
    }

    public static PurchaseItem fromCursor(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(AccountingDbHelper.PI_COL_NAME));
        float quantity = cursor.getFloat(cursor.getColumnIndex(AccountingDbHelper.PI_COL_QUANTITY));
        float rate = cursor.getFloat(cursor.getColumnIndex(AccountingDbHelper.PI_COL_RATE));
        float amount = cursor.getFloat(cursor.getColumnIndex(AccountingDbHelper.PI_COL_AMOUNT));
        PurchaseItem pi = new PurchaseItem(name, quantity, rate, amount);
        pi.setId(cursor.getLong(cursor.getColumnIndex(AccountingDbHelper.ID)));
        return pi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected boolean insert(Context context, long purchaseId) {
        ContentValues values = new ContentValues();
        values.put(AccountingDbHelper.PI_COL_PURCHASE_ID, purchaseId);
        values.put(AccountingDbHelper.PI_COL_NAME, getName());
        values.put(AccountingDbHelper.PI_COL_QUANTITY, getQuantity());
        values.put(AccountingDbHelper.PI_COL_RATE, getRate());
        values.put(AccountingDbHelper.PI_COL_AMOUNT, getAmount());
        try {
            Uri newPi = context.getContentResolver().insert(
                    getInsertUri(purchaseId), values);
            setId(Long.parseLong(newPi.getLastPathSegment()));
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }

    private Uri getInsertUri(long purchaseId) {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                + "/" + purchaseId
                + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH);
    }

    private Uri getUpdateUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH
                + "/" + getId());
    }

    public boolean isValid() {
        if (getName() == null || getName().isEmpty()) {
            return false;
        }
        if (getQuantity() <= 0 || getRate() <= 0 || getAmount() <= 0) {
            return false;
        }
        return true;
    }

    public boolean update(Context context) {
        ContentValues values = new ContentValues();
        values.put(AccountingDbHelper.PI_COL_NAME, getName());
        values.put(AccountingDbHelper.PI_COL_QUANTITY, getQuantity());
        values.put(AccountingDbHelper.PI_COL_RATE, getRate());
        values.put(AccountingDbHelper.PI_COL_AMOUNT, getAmount());
        try {
            int rowsUpdated = context.getContentResolver().update(
                    getUpdateUri(), values,
                    AccountingDbHelper.ID + " = " + getId(), null);
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (NullPointerException ex) {
            return false;
        }
        return false;
    }
}
