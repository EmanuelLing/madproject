package com.example.madproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "foodease.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME_CUSTOMERORDER = "Customer_Order";
    private static String COLUMN_ORDERID = "order_id";
    private  static String COLUMN_CUSTID = "cust_id";
    private static String COLUMN_TOTALPRICE = "total_price";
    private static String COLUMN_STATUS = "status";
    private static String COLUMN_QUANTITY= "quantity";
    private static String COLUMN_DATE = "date";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_NAME_CUSTOMERORDER + "(" +
                COLUMN_ORDERID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CUSTID + " TEXT," +
                COLUMN_TOTALPRICE + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_QUANTITY + " TEXT," +
                COLUMN_DATE + " TEXT)";
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CUSTOMERORDER);
        onCreate(db);
    }

    public boolean insertOrder(String custId, String totprice, String status, String quantity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CUSTID, custId);
        values.put(COLUMN_TOTALPRICE, totprice);
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_DATE, date); // Insert the current date

        long result = db.insert(TABLE_NAME_CUSTOMERORDER, null, values);
        return result != -1;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);

        int rowsUpdated = db.update(
                TABLE_NAME_CUSTOMERORDER,
                values,
                COLUMN_ORDERID + "=?",
                new String[]{String.valueOf(orderId)}
        );

        db.close();
        return rowsUpdated > 0; // Return true if at least one row was updated
    }



    public Cursor getAllOrder(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME_CUSTOMERORDER,null);
    }

    public boolean deleteTask(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_CUSTOMERORDER,COLUMN_ORDERID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

}
