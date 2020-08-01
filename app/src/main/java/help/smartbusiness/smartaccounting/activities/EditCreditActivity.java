package help.smartbusiness.smartaccounting.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.LoaderManager;
import androidx.core.content.CursorLoader;
import androidx.core.content.Loader;
import android.view.View;
import android.widget.Button;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.Utils.Utils;
import help.smartbusiness.smartaccounting.db.AccountingProvider;
import help.smartbusiness.smartaccounting.models.Credit;

public class EditCreditActivity extends CreditEditorActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String TAG = EditCreditActivity.class.getSimpleName();
    public static final String CREDIT_ID = "c_id";

    private long mCreditId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreditId = getIntent().getLongExtra(CREDIT_ID, -1);
        if (mCreditId == -1) {
            finish();
            return;
        }
        setContentView(R.layout.activity_edit_credit);
        setUpCreditFields();
        getSupportLoaderManager().initLoader(R.id.credit_loader, null, this);
    }

    @Override
    public void setSubmitButtonAction(Button button) {
        button.setText(getString(R.string.save));
        button.setOnClickListener(this);
    }

    /**
     * Edit save onClick listener
     *
     * @param view The clicked button.
     */
    @Override
    public void onClick(View view) {
        Credit credit = getCreditObject();
        credit.setId(mCreditId);
        if (credit.isValid(false)) { // Only validate credit and not the associated customer.
            if (!credit.update(this)) {
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
                + "/" + AccountingProvider.CREDITS_BASE_PATH
                + "/" + mCreditId),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fillCreditFields(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void fillCreditFields(Cursor creditCursor) {
        if (!creditCursor.moveToNext()) {
            return;
        }
        Credit credit = Credit.fromCursor(creditCursor);
        dateTextView.setText(credit.getDate());
        if (credit.getType().equals(Credit.CreditType.CREDIT)) {
            creditTypeGroup.check(R.id.create_credit_type_credit);
        } else if (credit.getType().equals(Credit.CreditType.DEBIT)) {
            creditTypeGroup.check(R.id.create_credit_type_debit);
        }
        creditRemarks.setText(credit.getRemarks());
        creditAmount.setText(String.valueOf(credit.getAmount()));
    }

}
