package help.smartbusiness.smartaccounting.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

/**
 * Created by gamerboy on 26/5/16.
 */
public class Purchase {

    public enum PurchaseType {
        SELL(AccountingDbHelper.PURCHASE_TYPE_SELL), BUY(AccountingDbHelper.PURCHASE_TYPE_BUY);

        private String type;

        private PurchaseType(String type) {
            this.type = type;
        }

        public String getDbType() {
            return type;
        }
    }

    public static final String TAG = Purchase.class.getName();
    private long id;
    private Customer customer;
    private String date;
    private String remarks;
    private PurchaseType type;
    private float amount;
    private List<PurchaseItem> purchaseItems;

    public Purchase(String date, String remarks, PurchaseType type, float amount) {
        this.date = date;
        this.remarks = remarks;
        this.type = type;
        this.amount = amount;
        purchaseItems = new ArrayList<>();
    }

    public static Purchase fromCursor(Cursor cursor) {
        String date = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_DATE));
        String remarks = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_REMARKS));
        PurchaseType type = PurchaseType.valueOf(cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE)).toUpperCase());
        float amount = cursor.getFloat(cursor.getColumnIndex(
                AccountingDbHelper.CPV_AMOUNT));
        Purchase p = new Purchase(date, remarks, type, amount);
        p.setId(cursor.getLong(cursor.getColumnIndex(AccountingDbHelper.ID)));
        return p;
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

    public List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseItem> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }

    public boolean isValid(boolean validateCustomer, boolean validatePi) {
        if (validateCustomer) {
            if (getCustomer() == null || !getCustomer().isValid()) {
                return false;
            }
        }
        if (validatePi) {
            for (PurchaseItem item : purchaseItems) {
                if (!item.isValid()) {
                    return false;
                }
            }
        }

        // Type validation.
        if (getType() == null) {
            return false;
        }
        if (getDate().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Insert purchase and it's associated entities into the database.
     * Inserts customer, purchase and purchase items into the database.
     *
     * @param context Context
     * @return True if everything was properly inserted else False
     */
    public boolean insert(Context context) {
        // TODO: This method uses contentResolver.insert() which works on the ui thread. Move to background thread using AsyncQueryHandler.
        // TODO: Process this whole insert in a transaction!
        boolean customerInserted = getCustomer().isValidId() || getCustomer().insert(context);
        if (customerInserted) {
            ContentValues values = new ContentValues();
            values.put(AccountingDbHelper.PURCHASE_COL_CUSTOMER_ID, getCustomer().getId());
            values.put(AccountingDbHelper.PURCHASE_COL_DATE, getDate());
            values.put(AccountingDbHelper.PURCHASE_COL_REMARKS, getRemarks());
            values.put(AccountingDbHelper.PURCHASE_COL_TYPE, getType().getDbType());

            try {
                Uri newPurchase = context.getContentResolver().insert(getInsertUri(), values);
                setId(Long.parseLong(newPurchase.getLastPathSegment()));
                for (PurchaseItem item : getPurchaseItems()) {
                    // TODO Use bulk insert or write own transaction.
                    // TODO If one of the pi fails to be inserted, the whole transaction should rollback.
                    if (!item.insert(context, getId())) {
                        return false;
                    }
                }
            } catch (SQLException ex) {
                return false;
            }
            // Processed successfully.
            return true;
        }
        // Couldn't insert customer.
        return false;
    }

    public boolean update(Context context) {
        // TODO Use transaction!
        ContentValues values = new ContentValues();
        values.put(AccountingDbHelper.PURCHASE_COL_DATE, getDate());
        values.put(AccountingDbHelper.PURCHASE_COL_REMARKS, getRemarks());
        values.put(AccountingDbHelper.PURCHASE_COL_TYPE, getType().getDbType());
        try {
            int rowsUpdated = context.getContentResolver().update(getUpdateUri(),
                    values, AccountingDbHelper.ID + " = " + getId(), null);
            if (rowsUpdated > 0) {
                for (PurchaseItem item : getPurchaseItems()) {
                    if (!item.update(context)) {
                        return false;
                    }
                }
            }
        } catch (NullPointerException ex) {
            return false;
        }
        return true;
    }

    private Uri getUpdateUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                + "/" + getId());
    }

    /**
     * Return the uri to be used to insert this purchase.
     */
    private Uri getInsertUri() {
        return Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + getCustomer().getId()
                + "/" + AccountingProvider.PURCHASES_BASE_PATH);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public PurchaseType getType() {
        return type;
    }

    public void setType(PurchaseType type) {
        this.type = type;
    }

}
