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
    private long quantity;
    private long rate;
    private long amount;

    public PurchaseItem(String name, long quantity, long rate, long amount) {
        this.name = name;
        this.quantity = quantity;
        this.rate = rate;
        this.amount = amount;
    }

    public static PurchaseItem fromCursor(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(AccountingDbHelper.PI_COL_NAME));
        long quantity = cursor.getLong(cursor.getColumnIndex(AccountingDbHelper.PI_COL_QUANTITY));
        long rate = cursor.getLong(cursor.getColumnIndex(AccountingDbHelper.PI_COL_RATE));
        long amount = cursor.getLong(cursor.getColumnIndex(AccountingDbHelper.PI_COL_AMOUNT));
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

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected boolean insert(Context context, long purchaseId) {
        try {
            Uri newPi = context.getContentResolver().insert(
                    getInsertUri(purchaseId), getInsertContentValues(purchaseId));
            setId(Long.parseLong(newPi.getLastPathSegment()));
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }

    protected ContentValues getInsertContentValues(long purchaseId) {
        ContentValues values = new ContentValues();
        values.put(AccountingDbHelper.PI_COL_PURCHASE_ID, purchaseId);
        values.put(AccountingDbHelper.PI_COL_NAME, getName());
        values.put(AccountingDbHelper.PI_COL_QUANTITY, getQuantity());
        values.put(AccountingDbHelper.PI_COL_RATE, getRate());
        values.put(AccountingDbHelper.PI_COL_AMOUNT, getAmount());
        return values;
    }

    public Uri getInsertUri(long purchaseId) {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                + "/" + purchaseId
                + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH);
    }

    public Uri getUpdateUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH
                + "/" + getId());
    }

    public static Uri getMultiDeleteUri(long purchaseId) {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                + "/" + purchaseId
                + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH);
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
