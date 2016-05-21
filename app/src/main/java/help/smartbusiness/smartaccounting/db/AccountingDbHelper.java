package help.smartbusiness.smartaccounting.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gamerboy on 19/5/16.
 */
public class AccountingDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "accounting";
    public static final int DATABASE_VERSION = 1;

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
    public static final String CREATE_TABLE_PURCHASE = "create table " + TABLE_PURCHASE
            + " ( "
            + ID + " integer primary key autoincrement, "
            + PURCHASE_COL_CUSTOMER_ID + " integer not null, "
            + PURCHASE_COL_DATE + " text not null, "
            + PURCHASE_COL_REMARKS + " text, "
            + "foreign key (" + PURCHASE_COL_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMER + "(" + ID + ")"
            + " )";
    public static final String CALCULATED_PURCHASE_VIEW = "calculated_purchases";


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


    public static final String CREATE_VIEW_PURCHASE = "create view " + CALCULATED_PURCHASE_VIEW
            + " AS "
            + " SELECT " + TABLE_PURCHASE + ".*, "
                + " sum (" + TABLE_PURCHASE_ITEMS + "." + PI_COL_AMOUNT + ") AS amount "
            + " FROM " + TABLE_PURCHASE + " LEFT OUTER JOIN " + TABLE_PURCHASE_ITEMS
            + " ON " + TABLE_PURCHASE + "." + ID + " = " + TABLE_PURCHASE_ITEMS + "." + PI_COL_PURCHASE_ID
            + " GROUP BY " + TABLE_PURCHASE_ITEMS + "." + PI_COL_PURCHASE_ID;


    public static final String TABLE_CREDIT = "credits";
    public static final String CREDIT_COL_CUSTOMER_ID = "c_id";
    public static final String CREDIT_COL_AMOUNT = "amount";
    public static final String CREDIT_COL_DATE = "date";
    public static final String CREDIT_COL_REMARKS = "remarks";
    public static final String CREATE_TABLE_CREDIT = "create table " + TABLE_CREDIT
            + " ( "
            + ID + " integer primary key autoincrement, "
            + CREDIT_COL_CUSTOMER_ID + " integer not null, "
            + CREDIT_COL_AMOUNT + " real not null, "
            + CREDIT_COL_DATE + " text not null, "
            + CREDIT_COL_REMARKS + " text, "
            + "foreign key (" + CREDIT_COL_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMER + "(" + ID + ")"
            + " )";

    public AccountingDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_CUSTOMERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_PURCHASE);
        sqLiteDatabase.execSQL(CREATE_TABLE_PI);
        sqLiteDatabase.execSQL(CREATE_VIEW_PURCHASE);
        sqLiteDatabase.execSQL(CREATE_PI_AMOUNT_UPDATE_TRIGGER);
        sqLiteDatabase.execSQL(CREATE_TABLE_CREDIT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP table IF EXISTS " + TABLE_CUSTOMER);
        sqLiteDatabase.execSQL("DROP table IF EXISTS " + TABLE_PURCHASE);
        sqLiteDatabase.execSQL("DROP table IF EXISTS " + TABLE_PURCHASE_ITEMS);
        sqLiteDatabase.execSQL("DROP table IF EXISTS " + TABLE_CREDIT);
        onCreate(sqLiteDatabase);
    }
}
