package help.smartbusiness.smartaccounting.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;
import help.smartbusiness.smartaccounting.services.ExportDbService;
import help.smartbusiness.smartaccounting.services.ImportDbService;

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
        setUpFabs();
        mListView = (ListView) findViewById(R.id.customer_due_list);
        mAdapter = getListViewAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        getSupportLoaderManager().initLoader(R.id.customer_loader, null, this);
    }

    private void setUpFabs() {
        final FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.create_fab_menu);
        FloatingActionButton purchaseButton = (FloatingActionButton) findViewById(R.id.purchase_create_fab);
        FloatingActionButton creditButton = (FloatingActionButton) findViewById(R.id.credit_create_fab);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreatePurchase.class));
                menu.toggle();
            }
        });
        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateCreditActivity.class));
                menu.toggle();
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_export) {
            if (AccountingDbHelper.dbEmpty(this)) {
                Snackbar.make(mListView,
                        R.string.export_cancelled_message,
                        Snackbar.LENGTH_LONG).show();
            } else {
                ExportDbService.startExport(this);
                Snackbar.make(mListView,
                        R.string.export_start_message,
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        } else if (id == R.id.action_import) {
            if (AccountingDbHelper.dbEmpty(this)) {
                ImportDbService.startImport(this);
                Snackbar.make(mListView,
                        R.string.import_start_message,
                        Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(mListView,
                        R.string.import_cancelled_message,
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        } else if (id == R.id.action_logout) {
            Intent logoutIntent = new Intent(this, BackupActivity.class);
            logoutIntent.putExtra(BackupActivity.LOGOUT_REQUEST, true);
            startActivity(logoutIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI),
                null, AccountingDbHelper.CDV_DUE + " <> 0",
                null, AccountingDbHelper.CDV_DUE + " desc ");
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
