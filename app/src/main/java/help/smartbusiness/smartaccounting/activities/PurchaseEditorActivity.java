package help.smartbusiness.smartaccounting.activities;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.Utils;
import help.smartbusiness.smartaccounting.fragments.DatePickerFragment;
import help.smartbusiness.smartaccounting.models.Purchase;
import help.smartbusiness.smartaccounting.models.PurchaseItem;

/**
 * Created by gamerboy on 2/6/16.
 */
public abstract class PurchaseEditorActivity extends AppCompatActivity {

    public TextView purchaseDate;
    public MaterialEditText purchaseTotal, purchaseRemarks;
    public Button createPurchaseButton;
    public LinearLayout purchaseItemWrapper, defaultPurchaseItem;
    public RadioGroup purchaseTypeGroup;
    public Map<Integer, MaterialEditText> totalsEditTexts;

    public void setUpPurchaseFields() {
        totalsEditTexts = new HashMap<>();
        purchaseTotal = (MaterialEditText) findViewById(R.id.create_purchase_total);
        purchaseRemarks = (MaterialEditText) findViewById(R.id.create_purchase_remarks);
        purchaseTypeGroup = (RadioGroup) findViewById(R.id.create_purchase_type_group);
        createPurchaseButton = (Button) findViewById(R.id.purchase_create);
        setUpDatePicker();
        setUpDefaultPis();
        setUpAddMorePis();
        setSubmitButtonAction(createPurchaseButton);
    }

    public abstract void setSubmitButtonAction(Button button);

    private void setUpDatePicker() {
        purchaseDate = (TextView) findViewById(R.id.create_purchase_date);
        purchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
    }

    private void setUpAddMorePis() {
        purchaseItemWrapper = (LinearLayout)
                findViewById(R.id.purchase_item_wrapper);

        Button addMorePi = (Button) findViewById(R.id.add_more_pi);
        // Add listener to add more purchase item views.
        addMorePi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPi();
            }
        });
    }

    public LinearLayout addPi() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater
                .inflate(R.layout.purchase_item_input_layout, purchaseItemWrapper, false);

        // Set layout id to identify this view. This is used when this view
        // is removed to update the total amount.
        layout.setId(View.generateViewId());
        setUpPurchaseItemEditTexts(layout);
        purchaseItemWrapper.addView(layout);

        ImageButton removePi = (ImageButton) layout.findViewById(R.id.input_purchase_item_remove);
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
        return layout;
    }

    private void setUpDefaultPis() {
        defaultPurchaseItem = (LinearLayout) findViewById(R.id.create_purchase_purchase_item);
        setUpPurchaseItemEditTexts(defaultPurchaseItem);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                super.onDateSet(view, year, month, day);
                purchaseDate.setText(String.format("%d-%d-%d", day, month + 1, year));
            }
        };
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setUpPurchaseItemEditTexts(View parent) {
        MaterialEditText purchaseItemQuantity = (MaterialEditText)
                parent.findViewById(R.id.input_purchase_item_quantity);
        MaterialEditText purchaseItemRate = (MaterialEditText)
                parent.findViewById(R.id.input_purchase_item_rate);
        MaterialEditText purchaseItemAmount = (MaterialEditText)
                parent.findViewById(R.id.input_purchase_item_amount);
        totalsEditTexts.put(parent.getId(), purchaseItemAmount);
        purchaseItemQuantity.addTextChangedListener(new CustomTextWatcher(
                purchaseItemQuantity, purchaseItemRate, purchaseItemAmount));
        purchaseItemRate.addTextChangedListener(new CustomTextWatcher(
                purchaseItemRate, purchaseItemQuantity, purchaseItemAmount));
    }

    private void updateTotal() {
        float sum = 0;
        for (MaterialEditText text : totalsEditTexts.values()) {
            float total = Utils.parseFloat(text.getText().toString());
            if (total < 0) {
                // One of the edit text has an invalid number which shouldn't happen.
                return;
            }
            sum += total;
        }
        purchaseTotal.setText(String.valueOf(sum));
    }

    public Purchase getPurchaseObject() {
        Purchase purchase = new Purchase(purchaseDate.getText().toString(),
                purchaseRemarks.getText().toString(),
                getPurchaseType(),
                Utils.parseFloat(purchaseTotal.getText().toString()));

        for (int i = 0; i < purchaseItemWrapper.getChildCount(); i++) {
            LinearLayout purchaseItem = (LinearLayout) purchaseItemWrapper.getChildAt(i);
            long id = Utils.parseLong(((TextView) purchaseItem
                    .findViewById(R.id.input_purchase_item_id)).getText().toString());
            String name = ((MaterialEditText) purchaseItem
                    .findViewById(R.id.input_purchase_item_name)).getText().toString();
            float quantity = Utils.parseFloat(((MaterialEditText) purchaseItem
                    .findViewById(R.id.input_purchase_item_quantity)).getText().toString());
            float rate = Utils.parseFloat(((MaterialEditText) purchaseItem
                    .findViewById(R.id.input_purchase_item_rate)).getText().toString());
            float amount = Utils.parseFloat(((MaterialEditText) purchaseItem
                    .findViewById(R.id.input_purchase_item_amount)).getText().toString());
            PurchaseItem item = new PurchaseItem(name, quantity, rate, amount);
            item.setId(id);
            purchase.getPurchaseItems().add(item);
        }
        return purchase;
    }

    private Purchase.PurchaseType getPurchaseType() {
        int checkedId = purchaseTypeGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.create_purchase_type_buy:
                return Purchase.PurchaseType.BUY;
            case R.id.create_purchase_type_sell:
                return Purchase.PurchaseType.SELL;
        }
        return null;
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
                float viewVal = Utils.parseFloat(viewStr);
                float otherVal = Utils.parseFloat(otherStr);
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

}
