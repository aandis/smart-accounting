package help.smartbusiness.smartaccounting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.CustomerNameSuggester;
import help.smartbusiness.smartaccounting.Utils.Utils;
import help.smartbusiness.smartaccounting.models.Customer;

public class EditCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView customerId;
    private MaterialEditText customerName, customerAddress;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);
        if (getIntent().hasExtra(CustomerNameSuggester.CUSTOMER_ID)) {
            fillCustomerFields();
            editButton = (Button) findViewById(R.id.edit_customer_edit);
            editButton.setOnClickListener(this);
        } else {
            finish();
        }
    }

    private void fillCustomerFields() {
        customerId = (TextView) findViewById(R.id.edit_customer_customer_id);
        customerName = (MaterialEditText) findViewById(R.id.edit_customer_customer_name);
        customerAddress = (MaterialEditText) findViewById(R.id.edit_customer_customer_address);

        Intent data = getIntent();
        customerId.setText(String.valueOf(data.getLongExtra(CustomerNameSuggester.CUSTOMER_ID, -1l)));
        customerName.setText(data.getStringExtra(CustomerNameSuggester.CUSTOMER_NAME));
        customerAddress.setText(data.getStringExtra(CustomerNameSuggester.CUSTOMER_ADDRESS));
    }

    /**
     * On customer edit button click.
     *
     * @param view The clicked button.
     */
    @Override
    public void onClick(View view) {
        Customer customer = getCustomerObject();
        if (customer.isValid() && customer.isValidId()) {
            if (!customer.update(this)) {
                Utils.notifyError(this, "An error occurred.");
            } else {
                finish();
            }
        } else {
            Utils.notifyError(this, "Invalid data");
        }
    }

    public Customer getCustomerObject() {
        return new Customer(Utils.parseLong(customerId.getText().toString()),
                customerName.getText().toString(),
                customerAddress.getText().toString());
    }
}
