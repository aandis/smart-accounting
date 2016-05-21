package help.smartbusiness.smartaccounting;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.CustomerProvider;

public class MainActivity extends AppCompatActivity {

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ListView listView = (ListView) findViewById(R.id.list);
        CursorLoader loader = new CursorLoader(this,
                CustomerProvider.CUSTOMER_CONTENT_URI, null, null, null, null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.list_layout,
                loader.loadInBackground(),
                new String[]{AccountingDbHelper.CUSTOMERS_COL_NAME},
                new int[]{R.id.customer_name});
        listView.setAdapter(adapter);
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
            ContentValues values = new ContentValues();
            values.put(AccountingDbHelper.CUSTOMERS_COL_NAME, "Sk nigam");
            values.put(AccountingDbHelper.CUSTOMERS_COL_ADDRESS, "Zamania");
            getContentResolver().insert(CustomerProvider.CUSTOMER_CONTENT_URI, values);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
