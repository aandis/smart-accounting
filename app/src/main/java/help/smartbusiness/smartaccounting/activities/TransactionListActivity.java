package help.smartbusiness.smartaccounting.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

public class TransactionListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = TransactionListActivity.class.getCanonicalName();
    public static final String CUSTOMER_ID = "id";

    private ExpandableListView mListView;
    private SimpleCursorTreeAdapter mAdapter;
    private long mCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        mCustomerId = getIntent().getLongExtra(CUSTOMER_ID, -1);
        if (mCustomerId == -1) {
            finish();
        }
        mListView = (ExpandableListView) findViewById(R.id.transactions_list);
        mAdapter = getListViewAdapter();
        mListView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(R.id.transaction_loader, null, this);
    }

    private SimpleCursorTreeAdapter getListViewAdapter() {
        return new SimpleCursorTreeAdapter(this,
                null,
                R.layout.transaction_item_layout,
                new String[]{
                        AccountingDbHelper.ID,
                        AccountingDbHelper.PURCHASE_COL_DATE,
                        AccountingDbHelper.PURCHASE_COL_REMARKS,
                        AccountingDbHelper.CPV_AMOUNT,
                        AccountingDbHelper.PURCHASE_COL_TYPE},
                new int[]{
                        R.id.transaction_id,
                        R.id.transaction_date,
                        R.id.transaction_remarks,
                        R.id.transaction_amount,
                        R.id.transaction_type},
                R.layout.transaction_item_purchase_item_layout,
                new String[]{
                        AccountingDbHelper.PI_COL_NAME,
                        AccountingDbHelper.PI_COL_RATE,
                        AccountingDbHelper.PI_COL_QUANTITY,
                        AccountingDbHelper.PI_COL_AMOUNT},
                new int[]{
                        R.id.transaction_item_pi_name,
                        R.id.transaction_item_pi_rate,
                        R.id.transaction_item_pi_quantity,
                        R.id.transaction_item_pi_amount}) {
            @Override
            protected Cursor getChildrenCursor(Cursor cursor) {
                // TODO Do this asynchronously with LoaderManager.
                String type = cursor.getString(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE));
                if (type.equals(AccountingDbHelper.PURCHASE_TYPE_BUY) ||
                        type.equals(AccountingDbHelper.PURCHASE_TYPE_SELL)) {
                    long transactionId = cursor.getLong(cursor.getColumnIndex(AccountingDbHelper.ID));
                    return getContentResolver().query(Uri.parse(
                                    AccountingProvider.PURCHASE_CONTENT_URI
                                            + "/" + transactionId
                                            + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH),
                            null, null, null, null);
                }
                return null;
            }
        };
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(
                AccountingProvider.CUSTOMER_CONTENT_URI
                        + "/" + mCustomerId
                        + "/" + AccountingProvider.TRANSACTION_BASE_PATH),
                null, null, null, AccountingDbHelper.PURCHASE_COL_DATE + " ASC ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setGroupCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
