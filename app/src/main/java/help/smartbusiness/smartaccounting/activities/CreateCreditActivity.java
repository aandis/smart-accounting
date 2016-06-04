package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.CustomerNameSuggester;
import help.smartbusiness.smartaccounting.Utils.Utils;
import help.smartbusiness.smartaccounting.models.Credit;
import help.smartbusiness.smartaccounting.models.Customer;

public class CreateCreditActivity extends CreditEditorActivity implements View.OnClickListener {

    private static final String TAG = CreateCreditActivity.class.getSimpleName();

    private TextView customerId;
    private MaterialAutoCompleteTextView customerName;
    private MaterialEditText customerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_credit);
        setUpCustomerFields();
        setUpCreditFields();
    }

    private void setUpCustomerFields() {
        customerId = (TextView) findViewById(R.id.create_credit_customer_id);
        customerName = (MaterialAutoCompleteTextView) findViewById(R.id.create_credit_customer_name);
        customerAddress = (MaterialEditText) findViewById(R.id.create_credit_customer_address);
        Intent intent = getIntent();
        if (intent.hasExtra(CustomerNameSuggester.CUSTOMER_ID)) {
            fillCustomerFields(intent);
        } else {
            CustomerNameSuggester suggester = new CustomerNameSuggester(this, customerName);
            suggester.initSuggestions(getString(R.string.credit_create_existing_confirmation),
                    CreateCreditActivity.class);
        }
    }

    public void fillCustomerFields(Intent intent) {
        customerId.setText(String.valueOf(intent.getLongExtra(CustomerNameSuggester.CUSTOMER_ID, -1l)));
        customerName.setText(intent.getStringExtra(CustomerNameSuggester.CUSTOMER_NAME));
        customerName.setEnabled(false);
        customerAddress.setText(intent.getStringExtra(CustomerNameSuggester.CUSTOMER_ADDRESS));
        customerAddress.setEnabled(false);
    }

    @Override
    public void setSubmitButtonAction(Button button) {
        button.setOnClickListener(this);
    }

    /**
     * Credit create onClick listener.
     *
     * @param view The clicked button.
     */
    @Override
    public void onClick(View view) {
        Credit credit = getCreditObject();
        if (credit.isValid(true)) {
            if (!credit.insert(this)) {
                Utils.notifyError(this, "An error occurred.");
            } else {
                finish();
            }
        } else {
            Utils.notifyError(this, "Invalid data");
        }
    }

    public Credit getCreditObject() {
        Customer customer = new Customer(Utils.parseLong(customerId.getText().toString()),
                customerName.getText().toString(),
                customerAddress.getText().toString());
        Credit credit = super.getCreditObject();
        credit.setCustomer(customer);
        return credit;
    }

}
