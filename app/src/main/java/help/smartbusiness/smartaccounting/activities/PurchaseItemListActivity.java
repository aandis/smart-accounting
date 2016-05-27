package help.smartbusiness.smartaccounting.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

public class PurchaseItemListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = PurchaseItemListActivity.class.getCanonicalName();
    public static final String CUSTOMER_ID = "name";

    private ListView mListView;
    private SimpleCursorAdapter mAdapter;
    private long mCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_item_list);
        mCustomerId = getIntent().getLongExtra(CUSTOMER_ID, -1);
        if (mCustomerId == -1) {
            finish();
        }
        mListView = (ListView) findViewById(R.id.purchase_item_list);
        mAdapter = getListViewAdapter();
        mListView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(R.id.purchase_item_loader, null, this);
    }

    private SimpleCursorAdapter getListViewAdapter() {
        return new SimpleCursorAdapter(this,
                R.layout.purchase_item_list_item_layout,
                null,
                new String[]{
                        AccountingDbHelper.PURCHASE_COL_DATE,
                        AccountingDbHelper.PURCHASE_COL_REMARKS},
                new int[]{R.id.purchase_date,
                        R.id.purchase_remarks},
                0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(
                AccountingProvider.CUSTOMER_CONTENT_URI
                        + "/" + mCustomerId
                        + "/" + AccountingProvider.PURCHASES_BASE_PATH
                        + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH),
                null, null, null, AccountingDbHelper.PURCHASE_COL_DATE + " desc ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
