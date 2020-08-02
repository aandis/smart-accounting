package help.smartbusiness.smartaccounting.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

public class SearchActivity extends SmartAccountingActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String SEARCH_QUERY = "query";

    private ListView mResultsList;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpResultList();
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }

    private void setUpResultList() {
        mResultsList = (ListView) findViewById(R.id.customer_search_result);
        mResultsList.setEmptyView(findViewById(R.id.empty));
        mResultsList.setOnItemClickListener(this);
        mAdapter = getListViewAdapter();
        mResultsList.setAdapter(mAdapter);
    }

    private SimpleCursorAdapter getListViewAdapter() {
        return new SimpleCursorAdapter(this,
                R.layout.customer_due_list_layout,
                null,
                new String[]{
                        AccountingDbHelper.CUSTOMERS_COL_NAME,
                        AccountingDbHelper.CDV_DUE,
                        AccountingDbHelper.CUSTOMERS_COL_ADDRESS},
                new int[]{R.id.due_customer_name,
                        R.id.customer_due_amount,
                        R.id.due_customer_address},
                0);
    }

    private void search(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_QUERY, query);
        getSupportLoaderManager().initLoader(R.id.search_loader, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = args.getString(SEARCH_QUERY);
        return new CursorLoader(this,
                Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI),
                new String[]{
                        AccountingDbHelper.ID,
                        AccountingDbHelper.CUSTOMERS_COL_NAME,
                        AccountingDbHelper.CUSTOMERS_COL_ADDRESS,
                        AccountingDbHelper.CDV_DUE},
                AccountingDbHelper.CUSTOMERS_COL_NAME + " LIKE ?",
                new String[]{"%" + query + "%"},
                AccountingDbHelper.CDV_DUE + " desc ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor rowCursor = (Cursor) adapterView.getItemAtPosition(i);
        long customerId = rowCursor.getLong(rowCursor.getColumnIndex(AccountingDbHelper.ID));
        startActivity(new Intent(this, TransactionListActivity.class)
                .putExtra(TransactionListActivity.CUSTOMER_ID, customerId));
    }
}
