package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    public static final String TAG = MainActivity.class.getCanonicalName();
    private ListView mListView;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreatePurchase.class));
            }
        });
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = getListViewAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        getSupportLoaderManager().initLoader(R.id.customer_loader, null, this);
    }

    private SimpleCursorAdapter getListViewAdapter() {
        return new SimpleCursorAdapter(this,
                R.layout.list_layout,
                null,
                new String[]{
                        AccountingDbHelper.CUSTOMERS_COL_NAME,
                        AccountingDbHelper.CDV_DUE,
                        AccountingDbHelper.CUSTOMERS_COL_ADDRESS},
                new int[]{R.id.customer_name,
                        R.id.customer_due_amount,
                        R.id.customer_address},
                0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI),
                null, null, null, AccountingDbHelper.CDV_DUE + " desc ");
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
        Log.d(TAG, "called");
        Cursor rowCursor = (Cursor) adapterView.getItemAtPosition(i);
        long customerId = rowCursor.getLong(rowCursor.getColumnIndex(AccountingDbHelper.ID));
        startActivity(new Intent(this, PurchaseItemListActivity.class)
                .putExtra(PurchaseItemListActivity.CUSTOMER_ID, customerId));
    }
}
