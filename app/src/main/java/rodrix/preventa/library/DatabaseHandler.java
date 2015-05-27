package rodrix.preventa.library;

/**
 * Created by usuario on 20/05/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import rodrix.preventa.MainActivity;

public class DatabaseHandler extends SQLiteOpenHelper{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "cloud_contacts";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table registro (producto text primari key, cantidad text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists registro");
        db.execSQL("create table registro (producto text primari key, cantidad text)");
    }

    public void registro(String producto, String cantidad){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valor = new ContentValues();
        valor.put("producto", producto);
        valor.put("cantidad", cantidad);

        db.insert("registro", null, valor);
        db.close();
    }

}
