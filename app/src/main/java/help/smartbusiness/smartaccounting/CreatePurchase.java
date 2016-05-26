package help.smartbusiness.smartaccounting;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class CreatePurchase extends AppCompatActivity {

    public static final String TAG = CreatePurchase.class.getName();

    private TextView purchaseDate;
    private Map<Integer, MaterialEditText> totalsEditTexts;
    private MaterialEditText purchaseTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purchase);
        totalsEditTexts = new HashMap<>();
        purchaseTotal = (MaterialEditText) findViewById(R.id.purchase_total);
        setUpDatePicker();
        setUpDefaultPis();
        setUpAddMorePis();
    }

    private void setUpDatePicker() {
        purchaseDate = (TextView) findViewById(R.id.purchase_date);
        purchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
    }

    private void setUpAddMorePis() {
        final LinearLayout purchaseItemWrapper = (LinearLayout)
                findViewById(R.id.purchase_item_wrapper);

        Button addMorePi = (Button) findViewById(R.id.add_more_pi);
        // Add listener to add more purchase item views.
        addMorePi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final LinearLayout layout = (LinearLayout) inflater
                        .inflate(R.layout.purchase_item_layout, purchaseItemWrapper, false);

                // Set layout id to identify this view. This is used when this view
                // is removed to update the total amount.
                layout.setId(View.generateViewId());
                setUpPurchaseItemEditTexts(layout);
                purchaseItemWrapper.addView(layout);

                ImageButton removePi = (ImageButton) layout.findViewById(R.id.purchase_item_remove);
                removePi.setVisibility(View.VISIBLE);
                removePi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Remove this view from the list of edit texts
                        // and recalculate total amount.
                        totalsEditTexts.remove(layout.getId());
                        purchaseItemWrapper.removeView(layout);
                        updateTotal();
                    }
                });
            }
        });
    }

    private void setUpDefaultPis() {
        LinearLayout defaultPurchaseItem = (LinearLayout) findViewById(R.id.purchase_item);
        setUpPurchaseItemEditTexts(defaultPurchaseItem);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                super.onDateSet(view, year, month, day);
                purchaseDate.setText(String.format("%d-%d-%d", day, month, year));
            }
        };
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setUpPurchaseItemEditTexts(View parent) {
        MaterialEditText purchaseItemQuantity = (MaterialEditText)
                parent.findViewById(R.id.purchase_item_quantity);
        MaterialEditText purchaseItemRate = (MaterialEditText)
                parent.findViewById(R.id.purchase_item_rate);
        MaterialEditText purchaseItemAmount = (MaterialEditText)
                parent.findViewById(R.id.purchase_item_amount);
        totalsEditTexts.put(parent.getId(), purchaseItemAmount);
        purchaseItemQuantity.addTextChangedListener(new CustomTextWatcher(
                purchaseItemQuantity, purchaseItemRate, purchaseItemAmount));
        purchaseItemRate.addTextChangedListener(new CustomTextWatcher(
                purchaseItemRate, purchaseItemQuantity, purchaseItemAmount));
    }

    private class CustomTextWatcher implements TextWatcher {
        private MaterialEditText mView;
        private MaterialEditText mOther;
        private MaterialEditText mAmount;

        public CustomTextWatcher(MaterialEditText view, MaterialEditText other, MaterialEditText amount) {
            mView = view;
            mOther = other;
            mAmount = amount;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String viewStr = mView.getText().toString();
            String otherStr = mOther.getText().toString();
            if (viewStr.length() > 0 && otherStr.length() > 0) {
                float viewVal = parseNumber(viewStr);
                float otherVal = parseNumber(otherStr);
                if (viewVal < 0 || otherVal < 0) {
                    mAmount.getText().clear();
                    return;
                }
                float amount = viewVal * otherVal;
                mAmount.setText(String.valueOf(amount));
                updateTotal();
            } else {
                mAmount.getText().clear();
            }
        }
    }


    private float parseNumber(String number) {
        try {
            return Float.parseFloat(number);
        } catch (NumberFormatException ex) {
            Toast.makeText(getBaseContext(), "Number too long!", Toast.LENGTH_LONG).show();
        }
        return -1;
    }

    private void updateTotal() {
        float sum = 0;
        for (MaterialEditText text : totalsEditTexts.values()) {
            float total = parseNumber(text.getText().toString());
            if (total < 0) {
                // One of the edit text has an invalid number which shouldn't happen.
                Log.d(TAG, "Something isn't right!");
                return;
            }
            sum += total;
        }
        purchaseTotal.setText(String.valueOf(sum));
    }
}
