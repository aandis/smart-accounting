package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import help.smartbusiness.smartaccounting.R;
import help.smartbusiness.smartaccounting.db.AccountingDbHelper;
import help.smartbusiness.smartaccounting.db.AccountingProvider;

/**
 * Created by gamerboy on 5/6/16.
 */
public class PurchaseItemNameSuggester {

    private Context mContext;
    private MaterialAutoCompleteTextView mTextView;

    public PurchaseItemNameSuggester(Context context, MaterialAutoCompleteTextView textView) {
        this.mContext = context;
        this.mTextView = textView;
    }

    public void initSuggestions() {
        final SimpleCursorAdapter adapter = getSuggestionAdapter();
        mTextView.setAdapter(adapter);
    }

    private SimpleCursorAdapter getSuggestionAdapter() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(mContext,
                R.layout.support_simple_spinner_dropdown_item,
                null,
                new String[]{AccountingDbHelper.PI_COL_NAME},
                new int[]{android.R.id.text1}, 0);

        adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex(AccountingDbHelper.PI_COL_NAME));
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            // TODO: Change this if this doesn't run on a background thread. See CursorAdapter.runQueryOnBackgroundThread()
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                return mContext.getContentResolver().query(
                        Uri.parse(AccountingProvider.CUSTOMER_CONTENT_URI
                                + "/" + AccountingProvider.PURCHASES_BASE_PATH
                                + "/" + AccountingProvider.PURCHASE_ITEMS_BASE_PATH
                                + "/" + AccountingProvider.RAW),
                        new String[]{AccountingDbHelper.ID, AccountingDbHelper.PI_COL_NAME},
                        AccountingDbHelper.PI_COL_NAME + " LIKE ?",
                        new String[]{"%" + charSequence + "%"}, null, null);
            }
        });
        return adapter;

    }
}
