package help.smartbusiness.smartaccounting.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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

    private static final String PURCHASE_BASE_PATH = "purchases";
    public static final Uri PURCHASE_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + PURCHASE_BASE_PATH);

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
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new AccountingDbHelper(getContext(),
                AccountingDbHelper.DATABASE_NAME, null, AccountingDbHelper.DATABASE_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
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
