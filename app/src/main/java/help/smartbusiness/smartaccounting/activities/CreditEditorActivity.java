package help.smartbusiness.smartaccounting.activities;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.Utils;
import help.smartbusiness.smartaccounting.fragments.DatePickerFragment;
import help.smartbusiness.smartaccounting.models.Credit;

/**
 * Created by gamerboy on 4/6/16.
 */
public abstract class CreditEditorActivity extends AppCompatActivity {

    public TextView dateTextView;
    public MaterialEditText creditAmount, creditRemarks;
    public Button createCreditButton;
    public RadioGroup creditTypeGroup;

    public void setUpCreditFields() {
        creditAmount = (MaterialEditText) findViewById(R.id.create_credit_amount);
        creditRemarks = (MaterialEditText) findViewById(R.id.create_credit_remarks);
        creditTypeGroup = (RadioGroup) findViewById(R.id.create_credit_type_group);
        createCreditButton = (Button) findViewById(R.id.credit_create);
        setUpDatePicker();
        setSubmitButtonAction(createCreditButton);
    }

    public abstract void setSubmitButtonAction(Button button);

    private void setUpDatePicker() {
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
                dateTextView.setText(String.format("%d-%d-%d", day, month + 1, year));
            }
        };
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public Credit getCreditObject() {
        return new Credit(dateTextView.getText().toString(),
                creditRemarks.getText().toString(),
                getCreditType(),
                Utils.parseFloat(creditAmount.getText().toString()));
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


}
