package help.smartbusiness.smartaccounting.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;
import help.smartbusiness.smartaccounting.fragments.YesNoDialog;
import help.smartbusiness.smartaccounting.models.Credit;
import help.smartbusiness.smartaccounting.models.Customer;
import help.smartbusiness.smartaccounting.models.Purchase;
import help.smartbusiness.smartaccounting.models.Transaction;

public class TransactionListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener {

    public static final String TAG = TransactionListActivity.class.getCanonicalName();
    public static final String CUSTOMER_ID = "id";

    private TextView mTotalAmount;
    private ExpandableListView mListView;
    private SimpleCursorTreeAdapter mAdapter;
    private long mCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        mCustomerId = getIntent().getLongExtra(CUSTOMER_ID, -1);
        if (mCustomerId == -1) {
            finish();
        }
        mTotalAmount = (TextView) findViewById(R.id.total_amount);
        mListView = (ExpandableListView) findViewById(R.id.transactions_list);
        mAdapter = getListViewAdapter();
        mListView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(R.id.transaction_loader, null, this);
        getSupportLoaderManager().initLoader(R.id.total_amount_loader, null, mAmountLoaderCallback);
        mListView.setOnItemLongClickListener(this);
    }

    private SimpleCursorTreeAdapter getListViewAdapter() {
        return new SimpleCursorTreeAdapter(this,
                null,
                R.layout.transaction_item_layout,
                new String[]{
                        AccountingDbHelper.ID,
                        AccountingDbHelper.PURCHASE_COL_DATE,
                        AccountingDbHelper.PURCHASE_COL_REMARKS,
                        AccountingDbHelper.CPV_AMOUNT,
                        AccountingDbHelper.PURCHASE_COL_TYPE},
                new int[]{
                        R.id.transaction_id,
                        R.id.transaction_date,
                        R.id.transaction_remarks,
                        R.id.transaction_amount,
                        R.id.transaction_type},
                R.layout.transaction_item_purchase_item_layout,
                new String[]{
                        AccountingDbHelper.PI_COL_NAME,
                        AccountingDbHelper.PI_COL_RATE,
                        AccountingDbHelper.PI_COL_QUANTITY,
                        AccountingDbHelper.PI_COL_AMOUNT},
                new int[]{
                        R.id.transaction_item_pi_name,
                        R.id.transaction_item_pi_rate,
                        R.id.transaction_item_pi_quantity,
                        R.id.transaction_item_pi_amount}) {
            @Override
            protected Cursor getChildrenCursor(Cursor cursor) {
                // TODO Do this asynchronously with LoaderManager.
                String type = cursor.getString(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_TYPE));
                if (type.equals(AccountingDbHelper.PURCHASE_TYPE_BUY) ||
                        type.equals(AccountingDbHelper.PURCHASE_TYPE_SELL)) {
                    Purchase purchase = Purchase.fromCursor(cursor);
                    return getContentResolver().query(Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                                    + "/" + AccountingProvider.PURCHASES_BASE_PATH
                                    + "/" + purchase.getId()
                                    + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH),
                            null, null, null, null);
                }
                return null;
            }
        };
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(
                AccountingProvider.CUSTOMER_CONTENT_URI
                        + "/" + mCustomerId
                        + "/" + AccountingProvider.TRANSACTION_BASE_PATH),
                null, null, null, AccountingDbHelper.PURCHASE_COL_DATE + " DESC ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setGroupCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        TransactionOptions options = new TransactionOptions(position);
        options.show();
        return true;
    }

    private LoaderManager.LoaderCallbacks<Cursor> mAmountLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(TransactionListActivity.this, Uri.parse(
                    AccountingProvider.CUSTOMER_CONTENT_URI
                            + "/" + mCustomerId),
                    null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.moveToNext()) {
                Customer customer = Customer.fromCursor(data);
                setTitle(customer.getFirstName()  + "'s account");
                mTotalAmount.setText(String.valueOf(customer.getDue()));
                if (customer.getDue() == 0.0) {
                    ClearTransaction clear = new ClearTransaction(customer);
                    clear.init();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private class ClearTransaction implements YesNoDialog.DialogClickListener, Handler.Callback {

        private Customer mCustomer;
        private Handler handler = new Handler(this);

        public ClearTransaction(Customer customer) {
            this.mCustomer = customer;
        }

        public void init() {
            handler.sendEmptyMessage(0);
        }

        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0) {
                showDialog();
            }
            return false;
        }

        public void showDialog() {
            YesNoDialog dialog = YesNoDialog
                    .newInstance("", getString(R.string.transaction_delete_message));
            dialog.setCallback(this);
            dialog.show(getSupportFragmentManager(), YesNoDialog.TAG);
        }


        @Override
        public void onYesClick() {
            YesNoDialog dialog = YesNoDialog.newInstance(
                    getString(R.string.transaction_delete_confirm),
                    getString(R.string.transaction_delete_confirm_message));
            dialog.setCallback(new YesNoDialog.DialogClickListener() {
                @Override
                public void onYesClick() {
                    mCustomer.delete(TransactionListActivity.this);
                    mTotalAmount.setText(null);
                }

                @Override
                public void onNoClick() {

                }
            });
            dialog.show(getSupportFragmentManager(), YesNoDialog.TAG);
        }

        @Override
        public void onNoClick() {

        }

    }

    private class TransactionOptions implements DialogInterface.OnClickListener, YesNoDialog.DialogClickListener {

        private Transaction mTransaction;

        public TransactionOptions(int flatAdapterPosition) {
            int groupPosition = getGroupPosition(flatAdapterPosition);
            Cursor transactionCursor = mAdapter.getGroup(groupPosition);
            this.mTransaction = Transaction.fromCursor(transactionCursor);
        }

        private int getGroupPosition(int flatPos) {
            long packedPos = mListView.getExpandableListPosition(flatPos);
            return ExpandableListView.getPackedPositionGroup(packedPos);
        }

        public void show() {
            CharSequence[] options = new CharSequence[]{"Edit", "Delete"};
            AlertDialog.Builder builder = new AlertDialog.Builder(TransactionListActivity.this);
            builder.setItems(options, this).show();
        }

        /**
         * Options dialog on click.
         *
         * @param dialogInterface Dialog.
         * @param i               Clicked position.
         */
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case 0:
                    startEditActivity();
                    break;
                case 1:
                    YesNoDialog dialog = YesNoDialog.newInstance("", "Confirm delete?");
                    dialog.setCallback(this);
                    dialog.show(getSupportFragmentManager(), YesNoDialog.TAG);
                    break;
            }
        }

        /**
         * On delete confirmed.
         */
        @Override
        public void onYesClick() {
            deleteTransaction();
        }

        /**
         * On delete cancelled.
         */
        @Override
        public void onNoClick() {
        }

        private void startEditActivity() {
            Intent editIntent = null;
            if (mTransaction.getTransactionType().equals(Purchase.class)) {
                editIntent = new Intent(TransactionListActivity.this, EditPurchaseActivity.class);
                editIntent.putExtra(EditPurchaseActivity.PURCHASE_ID, mTransaction.getId());
            } else if (mTransaction.getTransactionType().equals(Credit.class)) {
                editIntent = new Intent(TransactionListActivity.this, EditCreditActivity.class);
                editIntent.putExtra(EditCreditActivity.CREDIT_ID, mTransaction.getId());
            }
            startActivity(editIntent);
        }

        private void deleteTransaction() {
            mTransaction.delete(TransactionListActivity.this);
        }

    }
}