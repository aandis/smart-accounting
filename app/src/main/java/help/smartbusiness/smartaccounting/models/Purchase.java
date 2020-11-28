package help.smartbusiness.smartaccounting.models;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

/**
 * Created by gamerboy on 26/5/16.
 */
public class Purchase extends Transaction {

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
    private long amount;
    private String createdAt;
    private List<PurchaseItem> purchaseItems;

    public Purchase(String date, String remarks, PurchaseType type, long amount, String createdAt) {
        this.date = date;
        this.remarks = remarks;
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
        purchaseItems = new ArrayList<>();
    }

    public Purchase(String date, String remarks, PurchaseType type, long amount) {
        this(date, remarks, type, amount, null);
    }

    public static Purchase fromCursor(Cursor cursor) {
        String date = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_DATE));
        String remarks = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_REMARKS));
        PurchaseType type = PurchaseType.valueOf(cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE)).toUpperCase());
        long amount = cursor.getLong(cursor.getColumnIndex(
                AccountingDbHelper.CPV_AMOUNT));
        String createdAt = cursor.getString(
                cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_CREATED_AT));
        Purchase p = new Purchase(date, remarks, type, amount, createdAt);
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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
        // TODO make sure applyBatch() actually executes a in transaction.

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        // Update purchase.
        ContentValues purchaseValues = new ContentValues();
        purchaseValues.put(AccountingDbHelper.PURCHASE_COL_DATE, getDate());
        purchaseValues.put(AccountingDbHelper.PURCHASE_COL_REMARKS, getRemarks());
        purchaseValues.put(AccountingDbHelper.PURCHASE_COL_TYPE, getType().getDbType());
        operations.add(ContentProviderOperation.newUpdate(getUpdateUri())
                .withValues(purchaseValues)
                .withSelection(AccountingDbHelper.ID + " = " + getId(), null)
                .withExpectedCount(1)
                .build());

        // Delete existing purchase items for this purchase.
        operations.add(ContentProviderOperation
                .newDelete(PurchaseItem.getMultiDeleteUri(getId()))
                .withSelection(AccountingDbHelper.PI_COL_PURCHASE_ID
                        + " = " + getId(), null)
                .build());

        // Add new purchase items.
        for (PurchaseItem item : getPurchaseItems()) {
            operations.add(ContentProviderOperation
                    .newInsert(item.getInsertUri(getId()))
                    .withValues(item.getInsertContentValues(getId()))
                    .build());
        }

        try {
            ContentProviderResult result[] = context.getContentResolver()
                    .applyBatch(AccountingProvider.AUTHORITY, operations);
        } catch (OperationApplicationException | RemoteException ignored) {
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
                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                + "/" + getId());
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

    public Class getTransactionType() {
        return Purchase.class;
    }

}
