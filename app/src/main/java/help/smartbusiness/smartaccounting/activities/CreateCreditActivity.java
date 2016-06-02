package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils;
import help.smartbusiness.smartaccounting.fragments.DatePickerFragment;
import help.smartbusiness.smartaccounting.models.Credit;
import help.smartbusiness.smartaccounting.models.Customer;

public class CreateCreditActivity extends TransactionCreatorActivity implements View.OnClickListener {

    private static final String TAG = CreateCreditActivity.class.getSimpleName();

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
            fillCustomerFields(intent);
        } else {
            initSuggestions("Switch to creating credit for existing customer?", CreateCreditActivity.class);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                super.onDateSet(view, year, month, day);
                dateTextView.setText(String.format("%d-%d-%d", day, month + 1, year));
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

    @Override
    public TextView getCustomerIdTextView() {
        return customerId;
    }

    @Override
    public MaterialAutoCompleteTextView getCustomerNameTextView() {
        return customerName;
    }

    @Override
    public MaterialEditText getCustomerAddressTextView() {
        return customerAddress;
    }

}
