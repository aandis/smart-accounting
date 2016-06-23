package help.smartbusiness.smartaccounting;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import help.smartbusiness.smartaccounting.db.AccountingDbHelper;

/**
 * Created by gamerboy on 23/6/16.
 */
public class ChangeDateHelper {

    public static final String TAG = ChangeDateHelper.class.getName();

    public static void init(Context context) {
        HashMap<Integer, String> map = getDates(context);
        correctDates(map);
        setDates(context, map);
    }

    private static void correctDates(HashMap<Integer, String> map) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            String date = entry.getValue();
            String corrected = correctDate(date);
            assert corrected != null;
            entry.setValue(corrected);
        }
    }

    private static String correctDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String parsed;
        try {
            parsed = format.format(format.parse(date));
            return parsed;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<Integer, String> getDates(Context context) {
        AccountingDbHelper helper = new AccountingDbHelper(
                context,
                AccountingDbHelper.DATABASE_NAME, null, AccountingDbHelper.DATABASE_VERSION);
        String dateQuery = "SELECT " +
                AccountingDbHelper.ID + "," +
                AccountingDbHelper.PURCHASE_COL_DATE +
                " FROM " +
                AccountingDbHelper.TABLE_CREDIT;
        Cursor cursor = helper.getReadableDatabase().rawQuery(dateQuery, null);
        HashMap<Integer, String> dateMap = new HashMap<>();
        while (cursor.moveToNext()) {
            dateMap.put(cursor.getInt(cursor.getColumnIndex(AccountingDbHelper.ID)),
                    cursor.getString(cursor.getColumnIndex(AccountingDbHelper.PURCHASE_COL_DATE)));
        }
        if (BuildConfig.DEBUG && cursor.getCount() != dateMap.size()) {
            throw new AssertionError();
        }
        cursor.close();
        helper.close();
        return dateMap;
    }

    private static void setDates(Context context, HashMap<Integer, String> correctedDates) {
        String setQuery = "UPDATE " +
                AccountingDbHelper.TABLE_CREDIT +
                " SET " + AccountingDbHelper.PURCHASE_COL_DATE + " = " +
                " CASE ";
        for (Map.Entry<Integer, String> entry : correctedDates.entrySet()) {
            Integer id = entry.getKey();
            String date = entry.getValue();
            setQuery += " WHEN " + AccountingDbHelper.ID + " = " + id
                    + " THEN '" + date + "'";
        }
        setQuery += " END ";
        AccountingDbHelper helper = new AccountingDbHelper(context, AccountingDbHelper.DATABASE_NAME, null, AccountingDbHelper.DATABASE_VERSION);
        helper.getReadableDatabase().execSQL(setQuery);
    }

    private static void printMap(HashMap<Integer, String> map) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            Log.d(TAG, entry.getKey() + " : " + entry.getValue());
        }
    }

}
