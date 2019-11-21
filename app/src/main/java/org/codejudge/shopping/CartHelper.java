package org.codejudge.shopping;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class CartHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DBName";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table IF NOT EXISTS cart(id primary key not null, name text not null, price decimal(10,2) not null, image_url text not null, quantity integer not null);";

    public CartHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i(TAG, "onCreate: creating table cart");
        database.execSQL(DATABASE_CREATE);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(CartHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS songs");
        onCreate(database);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(TAG, "onOpen: dropping table cart");
//        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

}
