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
 * Created by gamerboy on 19/5/16.
 */
public class CustomerProvider extends ContentProvider {

    private AccountingDbHelper mDbHelper;

    private static final String AUTHORITY = "help.smartbusiness.smartaccounting.db";
    public static final int CUSTOMERS = 100;
    public static final int CUSTOMERS_ID = 110;

    private static final String CUSTOMERS_BASE_PATH = "customers";
    public static final Uri CUSTOMER_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CUSTOMERS_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd." + CustomerProvider.class.getPackage().getName()
            + CUSTOMERS_BASE_PATH;
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd." + CustomerProvider.class.getPackage().getName()
            + CUSTOMERS_BASE_PATH;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(AUTHORITY, CUSTOMERS_BASE_PATH, CUSTOMERS);
        mUriMatcher.addURI(AUTHORITY, CUSTOMERS_BASE_PATH + "/#", CUSTOMERS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new AccountingDbHelper(getContext(), AccountingDbHelper.DATABASE_NAME, null, 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(AccountingDbHelper.TABLE_CUSTOMER);

        int uriType = mUriMatcher.match(uri);
        switch (uriType) {
            case CUSTOMERS_ID:
                builder.appendWhere(AccountingDbHelper.ID + "=" + uri.getLastPathSegment());
                break;
            case CUSTOMERS:
                // Nothing to do.
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
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
        return mUriMatcher.match(uri) == CUSTOMERS_ID ? CONTENT_ITEM_TYPE : CONTENT_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (mUriMatcher.match(uri)) {
            case CUSTOMERS:
                long id = mDbHelper.getWritableDatabase().insert(
                        AccountingDbHelper.TABLE_CUSTOMER, "", contentValues);
                if (id < 0) {
                    throw new SQLException("Failed to add customer!");
                }
                Uri change = ContentUris.withAppendedId(CUSTOMER_CONTENT_URI, id);
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
