package help.smartbusiness.smartaccounting.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by gamerboy on 22/5/16.
 */
public class PurchaseProvider extends ContentProvider {

    private AccountingDbHelper mDbHelper;

    private static final String AUTHORITY = PurchaseProvider.class.getPackage().getName();
    public static final int PURCHASES = 100;
    public static final int PURCHASES_ID = 110;
    public static final int PURCHASES_CUSTOMERS = 120;
    public static final int PURCHASES_CUSTOMER_ID = 130;

    public static final String PURCHASE_BASE_PATH = "purchases";
    public static final Uri PURCHASE_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + PURCHASE_BASE_PATH);
//    public static final Uri PURCHASE_CUSTOMER_CONTENT_URI = Uri.parse("content://"
//            + AUTHORITY + "/" + PURCHASE_BASE_PATH + "/" + CustomerProvider.CUSTOMERS_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd." + PurchaseProvider.class.getPackage().getName()
            + PURCHASE_BASE_PATH;
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd." + PurchaseProvider.class.getPackage().getName()
            + PURCHASE_BASE_PATH;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(AUTHORITY, PURCHASE_BASE_PATH, PURCHASES);
        mUriMatcher.addURI(AUTHORITY, PURCHASE_BASE_PATH + "/#", PURCHASES_ID);
//        mUriMatcher.addURI(AUTHORITY, PURCHASE_BASE_PATH + "/" + CustomerProvider.CUSTOMERS_BASE_PATH, PURCHASES_CUSTOMERS);
//        mUriMatcher.addURI(AUTHORITY, PURCHASE_BASE_PATH + "/" + CustomerProvider.CUSTOMERS_BASE_PATH + "/#", PURCHASES_CUSTOMER_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new AccountingDbHelper(getContext(),
                AccountingDbHelper.DATABASE_NAME, null, AccountingDbHelper.DATABASE_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (mUriMatcher.match(uri)) {
            case PURCHASES:
                builder.setTables(AccountingDbHelper.CALCULATED_PURCHASE_VIEW);
                break;
            case PURCHASES_ID:
                builder.setTables(AccountingDbHelper.CALCULATED_PURCHASE_VIEW);
                builder.appendWhere(AccountingDbHelper.ID + "=" + uri.getLastPathSegment());
                break;
            case PURCHASES_CUSTOMERS:
                builder.setTables(AccountingDbHelper.CALCULATED_PURCHASE_VIEW
                        + " INNER JOIN " + AccountingDbHelper.TABLE_CUSTOMER
                        + " ON " + AccountingDbHelper.TABLE_CUSTOMER + "." + AccountingDbHelper.ID
                        + " = " + AccountingDbHelper.CALCULATED_PURCHASE_VIEW + "." + AccountingDbHelper.PURCHASE_COL_CUSTOMER_ID);
                break;
            case PURCHASES_CUSTOMER_ID:
                builder.setTables(AccountingDbHelper.CALCULATED_PURCHASE_VIEW
                        + " INNER JOIN " + AccountingDbHelper.TABLE_CUSTOMER
                        + " ON " + AccountingDbHelper.TABLE_CUSTOMER + "." + AccountingDbHelper.ID
                        + " = " + AccountingDbHelper.CALCULATED_PURCHASE_VIEW + "." + AccountingDbHelper.PURCHASE_COL_CUSTOMER_ID);
                builder.appendWhere(AccountingDbHelper.TABLE_CUSTOMER
                        + "." + AccountingDbHelper.ID + "=" + uri.getLastPathSegment());
                break;
        }
        Cursor cursor = builder.query(
                mDbHelper.getReadableDatabase(),
                projection, selection, selectionArgs,
                null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return mUriMatcher.match(uri) == PURCHASES_ID ? CONTENT_ITEM_TYPE : CONTENT_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (mUriMatcher.match(uri)) {
            case PURCHASES:
                long id = mDbHelper.getWritableDatabase().insert(
                        AccountingDbHelper.TABLE_PURCHASE, "", contentValues);
                if (id < 0) {
                    throw new SQLException("Failed to add customer!");
                }
                Uri change = ContentUris.withAppendedId(PURCHASE_CONTENT_URI, id);
                getContext().getContentResolver().notifyChange(change, null);
                return change;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
