package help.smartbusiness.smartaccounting.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gamerboy on 19/5/16.
 */
public class AccountingDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "accounting";
    public static final int DATABASE_VERSION = 1;

    public static final String ID = "_id";

    public static final String TABLE_CUSTOMERS = "customers";
    public static final String CUSTOMERS_COL_NAME = "name";
    public static final String CUSTOMERS_COL_ADDRESS = "address";
    public static final String CREATE_TABLE_CUSTOMERS = "create table " + TABLE_CUSTOMERS
            + " ( "
            + ID + " integer primary key autoincrement, "
            + CUSTOMERS_COL_NAME + " text not null, "
            + CUSTOMERS_COL_ADDRESS + " text not null"
            + " ); ";

    public static final String TABLE_TRANSACTION = "transactions";
    public static final String TRANSACTION_COL_CUSTOMER_ID = "cid";
    public static final String TRANSACTION_COL_DATE = "date";
    public static final String TRANSACTION_COL_IS_BOUGHT = "is_bought";
    public static final String TRANSACTION_COL_AMOUNT = "amount";
    public static final String TRANSACTION_COL_REMARKS = "remarks";
    public static final String CREATE_TABLE_TRANSACTION = "create table " + TABLE_TRANSACTION
            + " ( "
            + ID + " integer primary key autoincrement, "
            + TRANSACTION_COL_CUSTOMER_ID + " integer not null, "
            + TRANSACTION_COL_DATE + " text not null, "
            + TRANSACTION_COL_IS_BOUGHT + " boolean not null check (date in (0,1)), "
            + TRANSACTION_COL_AMOUNT + " real not null, "
            + TRANSACTION_COL_REMARKS + " text, "
            + "foreign key (" + TRANSACTION_COL_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMERS + "(" + ID + ")"
            + " ); ";

    public static final String TABLE_PURCHASE = "purchases";
    public static final String PURCHASE_COL_TRANSACTION_ID = "tid";
    public static final String PURCHASE_COL_NAME = "name";
    public static final String PURCHASE_COL_QUANTITY = "quantity";
    public static final String PURCHASE_COL_RATE = "rate";
    public static final String PURCHASE_COL_AMOUNT = "amount";
    public static final String CREATE_TABLE_PURCHASE = "create table " + TABLE_PURCHASE
            + " ( "
            + ID + " integer primary key autoincrement, "
            + PURCHASE_COL_TRANSACTION_ID + " integer not null, "
            + PURCHASE_COL_NAME + " text not null, "
            + PURCHASE_COL_QUANTITY + " real not null, "
            + PURCHASE_COL_RATE + " real not null, "
            + PURCHASE_COL_AMOUNT + " AS " + PURCHASE_COL_QUANTITY + "*" + PURCHASE_COL_RATE + ", "
            + "foreign key (" + PURCHASE_COL_TRANSACTION_ID + ") REFERENCES " + TABLE_TRANSACTION + "(" + ID + ")"
            + " ); ";

    public static final String DB_SCHEMA = CREATE_TABLE_CUSTOMERS
            + CREATE_TABLE_TRANSACTION
            + CREATE_TABLE_PURCHASE;

    public AccountingDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_SCHEMA);
        Log.d("tag", DB_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP table IF EXISTS " + TABLE_CUSTOMERS);
        onCreate(sqLiteDatabase);
    }
}
