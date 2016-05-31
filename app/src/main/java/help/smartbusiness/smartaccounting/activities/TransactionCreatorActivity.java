package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;
import help.smartbusiness.smartaccounting.fragments.YesNoDialog;

/**
 * Created by gamerboy on 31/5/16.
 * Adds autocompletion feature to customer name field and appropriate handling of
 * creating transaction for existing customer.
 */
public abstract class TransactionCreatorActivity extends AppCompatActivity {

    public static final String CUSTOMER_ID = "_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_ADDRESS = "customer_address";

    public abstract TextView getCustomerIdTextView();

    public abstract MaterialAutoCompleteTextView getCustomerNameTextView();

    public abstract MaterialEditText getCustomerAddressTextView();

    public void fillCustomerFields(Intent intent) {
        getCustomerIdTextView().setText(String.valueOf(intent.getLongExtra(CUSTOMER_ID, -1l)));
        getCustomerNameTextView().setText(intent.getStringExtra(CUSTOMER_NAME));
        getCustomerNameTextView().setEnabled(false);
        getCustomerAddressTextView().setText(intent.getStringExtra(CUSTOMER_ADDRESS));
        getCustomerAddressTextView().setEnabled(false);
    }

    public void initSuggestions(final String alertMessage, final Class<?> launchClass) {
        final SimpleCursorAdapter adapter = getSuggestionAdapter();
        getCustomerNameTextView().setAdapter(adapter);
        getCustomerNameTextView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {
                YesNoDialog dialogFragment = YesNoDialog.newInstance("", alertMessage);
                dialogFragment.show(getSupportFragmentManager(), YesNoDialog.TAG);
                dialogFragment.setCallback(new YesNoDialog.DialogClickListener() {
                    @Override
                    public void onYesClick() {
                        Cursor cursor = (Cursor) adapter.getItem(i);
                        long existingCustomerId = cursor.getLong(
                                cursor.getColumnIndex(AccountingDbHelper.ID));
                        String existingCustomerName = cursor.getString(
                                cursor.getColumnIndex(AccountingDbHelper.CUSTOMERS_COL_NAME));
                        String existingCustomerAddress = cursor.getString(
                                cursor.getColumnIndex(AccountingDbHelper.CUSTOMERS_COL_ADDRESS));
                        Intent intent = new Intent(view.getContext(), launchClass);
                        intent.putExtra(CUSTOMER_ID, existingCustomerId);
                        intent.putExtra(CUSTOMER_NAME, existingCustomerName);
                        intent.putExtra(CUSTOMER_ADDRESS, existingCustomerAddress);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNoClick() {

                    }
                });
            }
        });
    }

    private SimpleCursorAdapter getSuggestionAdapter() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,
                null,
                new String[]{AccountingDbHelper.CUSTOMERS_COL_NAME},
                new int[]{android.R.id.text1}, 0);
        adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex(AccountingDbHelper.CUSTOMERS_COL_NAME));
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            // TODO: Change this if this doesn't run on a background thread. See CursorAdapter.runQueryOnBackgroundThread()
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                return getContentResolver().query(Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI),
                        null,
                        AccountingDbHelper.CUSTOMERS_COL_NAME + " LIKE '%" + charSequence + "%'",
                        null, null, null);
            }
        });
        return adapter;
    }
}
