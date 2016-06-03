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

import com.rengwuxian.materialedittext.MaterialEditText;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingProvider;
import help.smartbusiness.smartaccounting.models.Purchase;
import help.smartbusiness.smartaccounting.models.PurchaseItem;

public class EditPurchaseActivity extends PurchaseEditorActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String TAG = EditPurchaseActivity.class.getSimpleName();

    // TODO - Shouldn't have to pass customer id. Improve uri structure.
    public static final String CUSTOMER_ID = "c_id";
    public static final String PURCHASE_ID = "p_id";

    private long mPurchaseId, mCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_purchase);
        setUpPurchaseFields();
        mCustomerId = getIntent().getLongExtra(CUSTOMER_ID, -1);
        mPurchaseId = getIntent().getLongExtra(PURCHASE_ID, -1);
        if (mCustomerId == -1 || mPurchaseId == -1) {
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                + "/" + mCustomerId
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
        MaterialEditText purchaseItemName = ((MaterialEditText)
                purchaseItemLayout.findViewById(R.id.input_purchase_item_name));
        MaterialEditText purchaseItemQuantity = (MaterialEditText)
                purchaseItemLayout.findViewById(R.id.input_purchase_item_quantity);
        MaterialEditText purchaseItemRate = (MaterialEditText)
                purchaseItemLayout.findViewById(R.id.input_purchase_item_rate);
        purchaseItemName.setText(item.getName());
        purchaseItemQuantity.setText(String.valueOf(item.getQuantity()));
        purchaseItemRate.setText(String.valueOf(item.getRate()));
    }
}
