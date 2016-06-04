package help.smartbusiness.smartaccounting.models;

import android.database.Cursor;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;

/**
 * Created by gamerboy on 4/6/16.
 */
public abstract class Transaction {

    public static Transaction fromCursor(Cursor cursor) {
        if (typeIsPurchase(cursor)) {
            return Purchase.fromCursor(cursor);
        } else if (typeIsCredit(cursor)) {
            return Credit.fromCursor(cursor);
        }
        return null;
    }

    public static boolean typeIsPurchase(Cursor cursor) {
        String type = cursor.getString(cursor.getColumnIndex(
                AccountingDbHelper.PURCHASE_COL_TYPE)).toUpperCase();
        return type.equals(Purchase.PurchaseType.BUY.toString())
                || type.equals(Purchase.PurchaseType.SELL.toString());

    }

    public static boolean typeIsCredit(Cursor cursor) {
        String type = cursor.getString(cursor.getColumnIndex(
                AccountingDbHelper.CREDIT_COL_TYPE)).toUpperCase();
        return type.equals(Credit.CreditType.CREDIT.toString())
                || type.equals(Credit.CreditType.DEBIT.toString());
    }

    public abstract long getId();
    public abstract void setId(long id);

    public abstract Class getTransactionType();
}
