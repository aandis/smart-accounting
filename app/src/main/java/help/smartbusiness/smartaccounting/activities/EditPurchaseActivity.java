package help.smartbusiness.smartaccounting.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.Utils;
import help.smartbusiness.smartaccounting.db.AccountingProvider;
import help.smartbusiness.smartaccounting.models.Purchase;
import help.smartbusiness.smartaccounting.models.PurchaseItem;

public class EditPurchaseActivity extends PurchaseEditorActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String TAG = EditPurchaseActivity.class.getSimpleName();

    public static final String PURCHASE_ID = "p_id";

    private long mPurchaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_purchase);
        setUpPurchaseFields();
        mPurchaseId = getIntent().getLongExtra(PURCHASE_ID, -1);
        if (mPurchaseId == -1) {
            finish();
        }
        getSupportLoaderManager().initLoader(R.id.purchases_purchase_item_loader, null, this);
    }

    @Override
    public void setSubmitButtonAction(Button button) {
        button.setText(getString(R.string.transaction_edit_save));
        button.setOnClickListener(this);
    }

    /**
     * Edit save onClick listener
     *
     * @param view The clicked button.
     */
    @Override
    public void onClick(View view) {
        Purchase purchase = getPurchaseObject();
        purchase.setId(mPurchaseId);
        if (purchase.isValid(false, true)) { // Only validate purchase and associate pis and not the associated customer.
            if (!purchase.update(this)) {
                Utils.notifyError(this, "An error occurred.");
            } else {
                finish();
            }
        } else {
            Utils.notifyError(this, "Invalid data");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                + "/" + mPurchaseId
                + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fillDataFields(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void fillDataFields(Cursor purchaseCursor) {
        // Fill purchase fields.
        if (!purchaseCursor.moveToNext()) {
            return;
        }
        Purchase purchase = Purchase.fromCursor(purchaseCursor);
        purchaseDate.setText(purchase.getDate());
        if (purchase.getType().equals(Purchase.PurchaseType.SELL)) {
            purchaseTypeGroup.check(R.id.create_purchase_type_sell);
        } else if (purchase.getType().equals(Purchase.PurchaseType.BUY)) {
            purchaseTypeGroup.check(R.id.create_purchase_type_buy);
        }
        purchaseRemarks.setText(purchase.getRemarks());

        // Fill purchase item fields.
        fillPurchaseItemFields(purchaseCursor, defaultPurchaseItem);
        while (purchaseCursor.moveToNext()) {
            LinearLayout purchaseItemLayout = addPi();
            fillPurchaseItemFields(purchaseCursor, purchaseItemLayout);
        }
    }

    private void fillPurchaseItemFields(Cursor purchaseItemCursor, LinearLayout purchaseItemLayout) {
        PurchaseItem item = PurchaseItem.fromCursor(purchaseItemCursor);
        TextView purchaseItemId = ((TextView) purchaseItemLayout
                .findViewById(R.id.input_purchase_item_id));
        MaterialAutoCompleteTextView purchaseItemName = ((MaterialAutoCompleteTextView)
                purchaseItemLayout.findViewById(R.id.input_purchase_item_name));
        MaterialEditText purchaseItemQuantity = (MaterialEditText)
                purchaseItemLayout.findViewById(R.id.input_purchase_item_quantity);
        MaterialEditText purchaseItemRate = (MaterialEditText)
                purchaseItemLayout.findViewById(R.id.input_purchase_item_rate);

        purchaseItemId.setText(String.valueOf(item.getId()));
        purchaseItemName.setText(item.getName());
        purchaseItemQuantity.setText(String.valueOf(item.getQuantity()));
        purchaseItemRate.setText(String.valueOf(item.getRate()));
    }
}
