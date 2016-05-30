package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FilterQueryProvider;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;
import help.smartbusiness.smartaccounting.fragments.DatePickerFragment;
import help.smartbusiness.smartaccounting.fragments.YesNoDialog;
import help.smartbusiness.smartaccounting.models.Credit;
import help.smartbusiness.smartaccounting.models.Customer;

public class CreateCreditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CreateCreditActivity.class.getCanonicalName();

    public static final String CUSTOMER_ID = "_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_ADDRESS = "customer_address";

    private TextView customerId, dateTextView;
    private Button createCreditButton;
    private MaterialAutoCompleteTextView customerName;
    private MaterialEditText customerAddress, creditAmount, creditRemarks;
    private RadioGroup creditTypeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_credit);
        setUpCustomerFields();
        creditAmount = (MaterialEditText) findViewById(R.id.create_credit_amount);
        creditRemarks = (MaterialEditText) findViewById(R.id.create_credit_remarks);
        createCreditButton = (Button) findViewById(R.id.credit_create);
        createCreditButton.setOnClickListener(this);
        dateTextView = (TextView) findViewById(R.id.create_credit_date);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        creditTypeGroup = (RadioGroup) findViewById(R.id.create_credit_type_group);
    }

    private void setUpCustomerFields() {
        customerId = (TextView) findViewById(R.id.create_credit_customer_id);
        customerName = (MaterialAutoCompleteTextView) findViewById(R.id.create_credit_customer_name);
        customerAddress = (MaterialEditText) findViewById(R.id.create_credit_customer_address);
        Intent intent = getIntent();
        if (intent.hasExtra(CUSTOMER_ID)) {
            customerId.setText(String.valueOf(intent.getLongExtra(CUSTOMER_ID, -1l)));
            customerName.setText(intent.getStringExtra(CUSTOMER_NAME));
            customerName.setEnabled(false);
            customerAddress.setText(intent.getStringExtra(CUSTOMER_ADDRESS));
            customerAddress.setEnabled(false);
        } else {
            initSuggestions();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                super.onDateSet(view, year, month, day);
                dateTextView.setText(String.format("%d-%d-%d", day, month, year));
            }
        };
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Credit create onClick listener.
     *
     * @param view The clicked button.
     */
    @Override
    public void onClick(View view) {
        Credit credit = getCreditObject();
        if (credit.isValid()) {
            if (!credit.insert(this)) {
                Utils.notifyError(this, "An error occurred.");
            } else {
                finish();
            }
        } else {
            Utils.notifyError(this, "Invalid data");
        }
    }

    private Credit getCreditObject() {
        Customer customer = new Customer(Utils.parseLong(customerId.getText().toString()),
                customerName.getText().toString(),
                customerAddress.getText().toString());
        String date = dateTextView.getText().toString();
        float amount = Utils.parseFloat(creditAmount.getText().toString());
        String remarks = creditRemarks.getText().toString();
        return new Credit(customer, date, amount, getCreditType(), remarks);
    }

    private Credit.CreditType getCreditType() {
        int checkedId = creditTypeGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.create_credit_type_credit:
                return Credit.CreditType.CREDIT;
            case R.id.create_credit_type_debit:
                return Credit.CreditType.DEBIT;
        }
        return null;
    }

    private void initSuggestions() {
        final SimpleCursorAdapter adapter = getSuggestionAdapter();
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
        customerName.setAdapter(adapter);
        customerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {
                YesNoDialog dialogFragment = YesNoDialog
                        .newInstance("", "Switch to creating credit for existing customer?");
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
                        Intent intent = new Intent(view.getContext(), CreateCreditActivity.class);
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
        return new SimpleCursorAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,
                null,
                new String[]{AccountingDbHelper.CUSTOMERS_COL_NAME},
                new int[]{android.R.id.text1}, 0);
    }
}
