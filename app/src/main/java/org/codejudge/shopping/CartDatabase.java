package org.codejudge.shopping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CartDatabase {

    private CartHelper dbHelper;
    private SQLiteDatabase database;

    public final static String TABLE="cart"; // name of table
    public final static  String ID="id";
    public final static String NAME="name";  // name column
    public final static String PRICE="price"; // artist column
    public final static String IMAGE_URL="image_url"; // artist column
    public final static String QUANTITY="quantity";  // album column


    /**
     *
     * @param context
     */
    public CartDatabase(Context context){
        dbHelper = new CartHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long createRecords(Integer id, String name, Double price, String imageUrl, Integer quantity){
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(ID, id);
        values.put(PRICE, price);
        values.put(IMAGE_URL, imageUrl);
        values.put(QUANTITY, quantity);
        Log.i(TAG, "createRecords: inserting values into table " + name + " " + price + " " + imageUrl + " " + quantity);
        long value =  database.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + TABLE, null);
        if (cur != null) {
            cur.moveToFirst();                       // Always one row returned.
            Log.i(TAG, "createRecords: total records in the table is " + cur.getInt (0));
        }
        return value;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<CartProduct> selectAllRecords() {
        String[] columns = new String[] {ID, NAME, PRICE, IMAGE_URL, QUANTITY};
        List<CartProduct> cartProducts = new ArrayList<>();
        cartProducts.clear();
        Cursor mCursor = database.rawQuery("select * from " + TABLE, null);
        mCursor.moveToFirst();
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                do {
                    CartProduct cartProduct = new CartProduct();
                    cartProduct.setId(mCursor.getInt(mCursor.getColumnIndex(ID)));
                    cartProduct.setName(mCursor.getString(mCursor.getColumnIndex(NAME)));
                    cartProduct.setPrice(mCursor.getDouble(mCursor.getColumnIndex(PRICE)));
                    cartProduct.setImageUrl(mCursor.getString(mCursor.getColumnIndex(IMAGE_URL)));
                    cartProduct.setQuantity(mCursor.getInt(mCursor.getColumnIndex(QUANTITY)));
                    cartProducts.add(cartProduct);
                }while (mCursor.moveToNext());
            }
        }
        return cartProducts;
    }

    public boolean deleteById(CartProduct cartProduct) {

        int result = database.delete(TABLE,  "id=" + cartProduct.getId(),null);
        if (result != 0) {
            return true;
        }
        return false;
    }
}
