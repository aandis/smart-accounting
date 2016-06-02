package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils;
import help.smartbusiness.smartaccounting.models.Customer;
import help.smartbusiness.smartaccounting.models.Purchase;

public class CreatePurchase extends PurchaseEditorActivity implements View.OnClickListener {

    public static final String TAG = CreatePurchase.class.getCanonicalName();

    private TextView customerId;
    private MaterialAutoCompleteTextView customerName;
    private MaterialEditText customerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purchase);
        setUpCustomerFields();
        setUpPurchaseFields();
    }

    private void setUpCustomerFields() {
        customerId = (TextView) findViewById(R.id.create_purchase_customer_id);
        customerName = (MaterialAutoCompleteTextView) findViewById(R.id.create_purchase_customer_name);
        customerAddress = (MaterialEditText) findViewById(R.id.create_purchase_customer_address);
        Intent intent = getIntent();
        if (intent.hasExtra(CustomerNameSuggester.CUSTOMER_ID)) {
            fillCustomerFields(intent);
        } else {
            CustomerNameSuggester suggester = new CustomerNameSuggester(this) {
                @Override
                public MaterialAutoCompleteTextView getCustomerNameTextView() {
                    return customerName;
                }
            };
            suggester.initSuggestions(getString(R.string.purchase_create_existing_confirmation),
                    CreatePurchase.class);
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
     * Purchase create onClick listener.
     *
     * @param view The clicked button.
     */
    @Override
    public void onClick(View view) {
        Purchase purchase = getPurchaseObject();
        if (purchase.isValid()) {
            if (!purchase.insert(this)) {
                Utils.notifyError(this, "An error occurred.");
            } else {
                finish();
            }
        } else {
            Utils.notifyError(this, "Invalid data");
        }
    }

    public Purchase getPurchaseObject() {
        Customer customer = new Customer(Utils.parseLong(customerId.getText().toString()),
                customerName.getText().toString(),
                customerAddress.getText().toString());
        return super.getPurchaseObject(customer);
    }

}
