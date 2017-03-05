package help.smartbusiness.smartaccounting.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import help.smartbusiness.smartaccounting.Utils.FileUtils;

/**
 * Created by gamerboy on 19/5/16.
 */
public class AccountingDbHelper extends SQLiteOpenHelper {

    public static final String TAG = AccountingDbHelper.class.getCanonicalName();
    public static final String DATABASE_NAME = "accounting";
    public static final int DATABASE_VERSION = 2;

    public static final String ID = "_id";

    public static final String TABLE_CUSTOMER = "customers";
    public static final String CUSTOMERS_COL_NAME = "name";
    public static final String CUSTOMERS_COL_ADDRESS = "address";
    public static final String CREATE_TABLE_CUSTOMERS = "create table " + TABLE_CUSTOMER
            + " ( "
            + ID + " integer primary key autoincrement, "
            + CUSTOMERS_COL_NAME + " text not null, "
            + CUSTOMERS_COL_ADDRESS + " text not null"
            + " )";


    public static final String TABLE_PURCHASE = "purchases";
    public static final String PURCHASE_COL_CUSTOMER_ID = "c_id";
    public static final String PURCHASE_COL_DATE = "date";
    public static final String PURCHASE_COL_REMARKS = "remarks";
    public static final String PURCHASE_COL_TYPE = "type";
    public static final String PURCHASE_TYPE_SELL = "sell";
    public static final String PURCHASE_TYPE_BUY = "buy";
    public static final String CREATE_TABLE_PURCHASE = "create table " + TABLE_PURCHASE
            + " ( "
            + ID + " integer primary key autoincrement, "
            + PURCHASE_COL_CUSTOMER_ID + " integer not null, "
            + PURCHASE_COL_DATE + " text not null, "
            + PURCHASE_COL_REMARKS + " text, "
            + PURCHASE_COL_TYPE + " text NOT NULL CHECK (" + PURCHASE_COL_TYPE + " IN ('" + PURCHASE_TYPE_SELL + "','" + PURCHASE_TYPE_BUY +  "')), "
            + "foreign key (" + PURCHASE_COL_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMER + "(" + ID + ")"
                + " ON DELETE CASCADE "
            + " )";


    public static final String TABLE_PURCHASE_ITEMS = "purchase_items";
    public static final String PI_COL_PURCHASE_ID = "p_id";
    public static final String PI_COL_NAME = "name";
    public static final String PI_COL_QUANTITY = "quantity";
    public static final String PI_COL_RATE = "rate";
    public static final String PI_COL_AMOUNT = "amount";
    public static final String CREATE_TABLE_PI = "create table " + TABLE_PURCHASE_ITEMS
            + " ( "
            + ID + " integer primary key autoincrement, "
            + PI_COL_PURCHASE_ID + " integer not null, "
            + PI_COL_NAME + " text not null, "
            + PI_COL_QUANTITY + " real not null, "
            + PI_COL_RATE + " real not null, "
            + PI_COL_AMOUNT + " real not null, "
            + "foreign key (" + PI_COL_PURCHASE_ID + ") REFERENCES " + TABLE_PURCHASE + "(" + ID + ")"
                + " ON DELETE CASCADE "
            + " )";
    public static final String CREATE_PI_AMOUNT_UPDATE_TRIGGER = "create trigger update_purchase_amount "
            + " after update of "
            + PI_COL_QUANTITY + ", " + PI_COL_RATE
            + " ON " + TABLE_PURCHASE_ITEMS + " FOR EACH ROW "
            + " BEGIN "
            + " UPDATE " + TABLE_PURCHASE_ITEMS
            + " SET " + PI_COL_AMOUNT + " = " + PI_COL_RATE + "*" + PI_COL_QUANTITY
            + " WHERE " + ID + " = OLD." + ID + "; "
            + " END";


    public static final String TABLE_CREDIT = "credits";
    public static final String CREDIT_COL_CUSTOMER_ID = "c_id";
    public static final String CREDIT_COL_AMOUNT = "amount";
    public static final String CREDIT_COL_DATE = "date";
    public static final String CREDIT_COL_REMARKS = "remarks";
    public static final String CREDIT_COL_TYPE = "type";
    public static final String CREDIT_TYPE_CREDIT = "credit";
    public static final String CREDIT_TYPE_DEBIT = "debit";
    public static final String CREATE_TABLE_CREDIT = "create table " + TABLE_CREDIT
            + " ( "
            + ID + " integer primary key autoincrement, "
            + CREDIT_COL_CUSTOMER_ID + " integer not null, "
            + CREDIT_COL_AMOUNT + " real not null, "
            + CREDIT_COL_DATE + " text not null, "
            + CREDIT_COL_REMARKS + " text, "
            + CREDIT_COL_TYPE + " text NOT NULL CHECK (" + CREDIT_COL_TYPE + " IN ('" + CREDIT_TYPE_CREDIT + "','" + CREDIT_TYPE_DEBIT +  "')), "
            + "foreign key (" + CREDIT_COL_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMER + "(" + ID + ")"
                + " ON DELETE CASCADE "
            + " )";


    public static final String CALCULATED_PURCHASE_VIEW = "calculated_purchases";
    public static final String CPV_AMOUNT = "amount";
    public static final String CREATE_VIEW_PURCHASE = "create view " + CALCULATED_PURCHASE_VIEW
            + " AS "
            + " SELECT " + TABLE_PURCHASE + ".*, "
            + " total (" + TABLE_PURCHASE_ITEMS + "." + PI_COL_AMOUNT + ") AS " + CPV_AMOUNT
            + " FROM " + TABLE_PURCHASE + " LEFT OUTER JOIN " + TABLE_PURCHASE_ITEMS
            + " ON " + TABLE_PURCHASE + "." + ID + " = " + TABLE_PURCHASE_ITEMS + "." + PI_COL_PURCHASE_ID
            + " GROUP BY " + TABLE_PURCHASE + "." + ID;


    public static final String TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW = "total_customer_purchases_sold";
    public static final String CREATE_VIEW_TOTAL_CUSTOMER_PURCHASES_SOLD = "create view " + TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW
            + " AS "
            + " SELECT " + TABLE_CUSTOMER + ".*, "
            + " total (" + CALCULATED_PURCHASE_VIEW + "." + CPV_AMOUNT + ") AS " + CPV_AMOUNT
            + " FROM " + TABLE_CUSTOMER
            + " LEFT OUTER JOIN " + CALCULATED_PURCHASE_VIEW
            + " ON " + TABLE_CUSTOMER + "." + ID + " = " + CALCULATED_PURCHASE_VIEW + "." + PURCHASE_COL_CUSTOMER_ID
            + " AND " + CALCULATED_PURCHASE_VIEW + "." + PURCHASE_COL_TYPE + " = '" + PURCHASE_TYPE_SELL + "' "
            + " GROUP BY " + TABLE_CUSTOMER + "." + ID;

    public static final String TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW = "total_customer_purchases_bought";
    public static final String CREATE_VIEW_TOTAL_CUSTOMER_PURCHASES_BOUGHT = "create view " + TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW
            + " AS "
            + " SELECT " + TABLE_CUSTOMER + ".*, "
            + " total (" + CALCULATED_PURCHASE_VIEW + "." + CPV_AMOUNT + ") AS " + CPV_AMOUNT
            + " FROM " + TABLE_CUSTOMER
            + " LEFT OUTER JOIN " + CALCULATED_PURCHASE_VIEW
            + " ON " + TABLE_CUSTOMER + "." + ID + " = " + CALCULATED_PURCHASE_VIEW + "." + PURCHASE_COL_CUSTOMER_ID
            + " AND " + CALCULATED_PURCHASE_VIEW + "." + PURCHASE_COL_TYPE + " = '" + PURCHASE_TYPE_BUY + "' "
            + " GROUP BY " + TABLE_CUSTOMER + "." + ID;


    public static final String TOTAL_CUSTOMER_CREDIT_VIEW = "total_customer_credits";
    public static final String CREATE_VIEW_TOTAL_CUSTOMER_CREDITS = "create view " + TOTAL_CUSTOMER_CREDIT_VIEW
            + " AS "
            + " SELECT " + TABLE_CUSTOMER + ".*, "
            + " total (" + TABLE_CREDIT + "." + CREDIT_COL_AMOUNT + ") AS " + CREDIT_COL_AMOUNT
            + " FROM " + TABLE_CUSTOMER
            + " LEFT OUTER JOIN " + TABLE_CREDIT
            + " ON " + TABLE_CUSTOMER + "." + ID + " = " + TABLE_CREDIT + "." + CREDIT_COL_CUSTOMER_ID
            + " AND " + TABLE_CREDIT + "." + CREDIT_COL_TYPE + " = '" + CREDIT_TYPE_CREDIT + "' "
            + " GROUP BY " + TABLE_CUSTOMER + "." + ID;

    public static final String TOTAL_CUSTOMER_DEBIT_VIEW = "total_customer_debits";
    public static final String CREATE_VIEW_TOTAL_CUSTOMER_DEBITS = "create view " + TOTAL_CUSTOMER_DEBIT_VIEW
            + " AS "
            + " SELECT " + TABLE_CUSTOMER + ".*, "
            + " total (" + TABLE_CREDIT + "." + CREDIT_COL_AMOUNT + ") AS " + CREDIT_COL_AMOUNT
            + " FROM " + TABLE_CUSTOMER
            + " LEFT OUTER JOIN " + TABLE_CREDIT
            + " ON " + TABLE_CUSTOMER + "." + ID + " = " + TABLE_CREDIT + "." + CREDIT_COL_CUSTOMER_ID
            + " AND " + TABLE_CREDIT + "." + CREDIT_COL_TYPE + " = '" + CREDIT_TYPE_DEBIT + "' "
            + " GROUP BY " + TABLE_CUSTOMER + "." + ID;

    public static final String CUSTOMER_DUE_VIEW = "customer_dues";
    public static final String CDV_DUE = "due";
    public static final String CREATE_VIEW_CUSTOMER_DUE = "create view " + CUSTOMER_DUE_VIEW
            + " AS "
            + " SELECT total_dues.*, (total_dues.amount - total_credit.amount) AS " + CDV_DUE + " from ("
                + " SELECT " + TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW + "." + ID + ", "
                    + TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW + "." + CUSTOMERS_COL_NAME + ", "
                    + TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW + "." + CUSTOMERS_COL_ADDRESS + ", "
                + " total (" + TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW + "." + CPV_AMOUNT + ") "
                + " + "
                + " total (" + TOTAL_CUSTOMER_DEBIT_VIEW + "." + CREDIT_COL_AMOUNT + ") AS amount "
                + " FROM " + TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW
                + " LEFT OUTER JOIN " + TOTAL_CUSTOMER_DEBIT_VIEW
                + " ON " + TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW + "." + ID
                + " = " + TOTAL_CUSTOMER_DEBIT_VIEW + "." + ID
                + " GROUP BY " + TOTAL_CUSTOMER_PURCHASES_SOLD_VIEW + "." + ID
            + ") total_dues "
            + " INNER JOIN ("
                + " SELECT " + TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW + "." + ID + ", "
                    + TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW + "." + CUSTOMERS_COL_NAME + ", "
                    + TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW + "." + CUSTOMERS_COL_ADDRESS + ", "
                + " total (" + TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW + "." + CPV_AMOUNT + ") "
                + " + "
                + " total (" + TOTAL_CUSTOMER_CREDIT_VIEW + "." + CREDIT_COL_AMOUNT + ") AS amount "
                + " FROM " + TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW
                + " LEFT OUTER JOIN " + TOTAL_CUSTOMER_CREDIT_VIEW
                + " ON " + TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW + "." + ID
                + " = " + TOTAL_CUSTOMER_CREDIT_VIEW + "." + ID
                + " GROUP BY " + TOTAL_CUSTOMER_PURCHASES_BOUGHT_VIEW + "." + ID
            + ") total_credit "
            + " ON total_dues." + ID + " = " + " total_credit." + ID;

    private Context mContext;

    public AccountingDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_CUSTOMERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_PURCHASE);
        sqLiteDatabase.execSQL(CREATE_TABLE_PI);
        sqLiteDatabase.execSQL(CREATE_TABLE_CREDIT);

        sqLiteDatabase.execSQL(CREATE_PI_AMOUNT_UPDATE_TRIGGER);

        sqLiteDatabase.execSQL(CREATE_VIEW_PURCHASE);

        sqLiteDatabase.execSQL(CREATE_VIEW_TOTAL_CUSTOMER_PURCHASES_BOUGHT);
        sqLiteDatabase.execSQL(CREATE_VIEW_TOTAL_CUSTOMER_PURCHASES_SOLD);
        sqLiteDatabase.execSQL(CREATE_VIEW_TOTAL_CUSTOMER_CREDITS);
        sqLiteDatabase.execSQL(CREATE_VIEW_TOTAL_CUSTOMER_DEBITS);

        sqLiteDatabase.execSQL(CREATE_VIEW_CUSTOMER_DUE);

        onUpgrade(sqLiteDatabase, 1, DATABASE_VERSION);
    }

    private void printSql() {
        Log.d(TAG, CREATE_TABLE_CUSTOMERS);
        Log.d(TAG, CREATE_TABLE_PURCHASE);
        Log.d(TAG, CREATE_TABLE_PI);
        Log.d(TAG, CREATE_TABLE_CREDIT);

        Log.d(TAG, CREATE_PI_AMOUNT_UPDATE_TRIGGER);

        Log.d(TAG, CREATE_VIEW_PURCHASE);

        Log.d(TAG, CREATE_VIEW_TOTAL_CUSTOMER_PURCHASES_BOUGHT);
        Log.d(TAG, CREATE_VIEW_TOTAL_CUSTOMER_PURCHASES_SOLD);
        Log.d(TAG, CREATE_VIEW_TOTAL_CUSTOMER_CREDITS);
        Log.d(TAG, CREATE_VIEW_TOTAL_CUSTOMER_DEBITS);

        Log.d(TAG, CREATE_VIEW_CUSTOMER_DUE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "Updating table from " + oldVersion + " to " + newVersion);
        // You will not need to modify this unless you need to do some android specific things.
        // When upgrading the database, all you need to do is add a file to the assets folder and name it:
        // from_1_to_2.sql with the version that you are upgrading to as the last version.
        try {
            for (int i = oldVersion; i < newVersion; ++i) {
                String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
                Log.d(TAG, "Looking for migration file: " + migrationName);
                readAndExecuteSQLScript(sqLiteDatabase, mContext, migrationName);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception running upgrade script:", exception);
        }
    }

    /**
     * Copies the database file at the specified location
     * over the current internal application database.
     */
    public void importDatabase(Context context, String newDbName) throws IOException {

        // Close the SQLiteOpenHelper so it will
        // commit the created empty database to internal storage.
        close();
        File newDb = new File(FileUtils.getFullPath(context, newDbName));
        Log.d(TAG, "Importing from " + newDb);
        if (newDb.exists()) {
            File oldDb = context.getDatabasePath(DATABASE_NAME);
            FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper
            // will cache it and mark it as created.
            getWritableDatabase().close();
        } else {
            throw new FileNotFoundException();
        }
    }

    public void exportDatabase(Context context, String backupName) throws IOException {

        // Open your local db as the input stream
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        FileInputStream fis = new FileInputStream(dbFile);

        File backupFile = new File(FileUtils.getFullPath(context, backupName));
        Log.d(TAG, "Exporting to " + backupFile);
        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(backupFile);
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        // Close the streams
        output.flush();
        output.close();
        fis.close();
    }

    public static boolean dbEmpty(Context context) {
        // TODO Ugly. Works on ui thread and manages sqlitedb on it's own.
        AccountingDbHelper helper = new AccountingDbHelper(
                context, DATABASE_NAME, null, DATABASE_VERSION);
        long count = DatabaseUtils.queryNumEntries(
                helper.getReadableDatabase(),
                AccountingDbHelper.TABLE_CUSTOMER);
        helper.close();
        return count == 0;
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "SQL script file name is empty");
            return;
        }

        Log.d(TAG, "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException:", e);
                }
            }
        }

    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.trim().equals(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }
}
