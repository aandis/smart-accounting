package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;
import help.smartbusiness.smartaccounting.fragments.YesNoDialog;

/**
 * Created by gamerboy on 31/5/16.
 * Adds autocompletion feature to customer name field and appropriate handling of
 * creating transaction for existing customer.
 */
public class CustomerNameSuggester {

    // TODO refactor into a Suggester class.

    private Context mContext;
    private MaterialAutoCompleteTextView mTextView;

    public CustomerNameSuggester(Context context, MaterialAutoCompleteTextView textView) {
        this.mContext = context;
        this.mTextView = textView;
    }

    public static final String CUSTOMER_ID = "_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_ADDRESS = "customer_address";

    public void initSuggestions(final String alertMessage, final Class<?> launchClass) {
        final AppCompatActivity activity = (AppCompatActivity) mContext;
        final SimpleCursorAdapter adapter = getSuggestionAdapter();
        mTextView.setAdapter(adapter);
        mTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {
                YesNoDialog dialogFragment = YesNoDialog.newInstance("", alertMessage);
                dialogFragment.show(activity.getSupportFragmentManager(),
                        YesNoDialog.TAG);
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
                        activity.startActivity(intent);
                        activity.finish();
                    }

                    @Override
                    public void onNoClick() {

                    }
                });
            }
        });
    }

    private SimpleCursorAdapter getSuggestionAdapter() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(mContext,
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
                return mContext.getContentResolver().query(Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI),
                        null,
                        AccountingDbHelper.CUSTOMERS_COL_NAME + " LIKE '%" + charSequence + "%'",
                        null, null, null);
            }
        });
        return adapter;
    }
}
