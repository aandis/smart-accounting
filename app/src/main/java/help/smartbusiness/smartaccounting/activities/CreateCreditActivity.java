package help.smartbusiness.smartaccounting.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils;
import help.smartbusiness.smartaccounting.fragments.DatePickerFragment;
import help.smartbusiness.smartaccounting.models.Credit;
import help.smartbusiness.smartaccounting.models.Customer;

public class CreateCreditActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView dateTextView;
    private Button createCreditButton;
    private MaterialEditText customerName, customerAddress, creditAmount, creditRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_credit);
        customerName = (MaterialEditText) findViewById(R.id.create_credit_customer_name);
        customerAddress = (MaterialEditText) findViewById(R.id.create_credit_customer_address);
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
                Utils.notifyError(this, "An error occured.");
            } else {
                finish();
            }
        } else {
            Utils.notifyError(this, "Invalid data");
        }
    }

    private Credit getCreditObject() {
        Customer customer = new Customer(customerName.getText().toString(),
                customerAddress.getText().toString());
        String date = dateTextView.getText().toString();
        float amount = Utils.parseNumber(creditAmount.getText().toString());
        String remarks = creditRemarks.getText().toString();
        return new Credit(customer, date, amount, remarks);
    }
}
