package com.example.poste.testxmpp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Poste on 18/05/2015.
 */
public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "messagedb";
    private static final String TABLE_NAME = "message";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "nom";
    private static final String KEY_CONTENU = "contenu";
    private static final String KEY_TIME = "time";
    private static final String[] COLONNES = { KEY_ID, KEY_NAME, KEY_CONTENU,KEY_TIME };

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {

        String CREATION_TABLE_EXEMPLE = "CREATE TABLE message ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nom TEXT, "
                + "contenu TEXT ,"+ "time DATE)";

        arg0.execSQL(CREATION_TABLE_EXEMPLE);
        Log.i("SQLite DB", "Creation");

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

        arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(arg0);
        Log.i("SQLite DB", "Upgrade");


    }

    public void deleteOne(message mess) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, // table
                "id = ?", new String[] { String.valueOf(mess.getId()) });
        db.close();
        Log.i("SQLite DB : Delete : ", mess.toString());

    }

    public message showOne(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLONNES, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        message food = new message();
        food.setId(Integer.parseInt(cursor.getString(0)));
        food.setNom(cursor.getString(1));
        food.setContenu(cursor.getString(2));
        food.setTime(Timestamp.valueOf(cursor.getString(3)));
        // log
        Log.w("information","SQLite DB : Show one  : id=  " + id+ food.toString());

        return food;
    }

    public List<message> showAll(int start,int end) {

        List<message> foods = new LinkedList<message>();
        String query = "SELECT  * FROM " + TABLE_NAME +" ORDER BY time DESC LIMIT "+start+" , "+end;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        message mes = null;
        if (cursor.moveToFirst()) {
            do {
                mes = new message();
                mes.setId(Integer.parseInt(cursor.getString(0)));
                mes.setNom(cursor.getString(1));
                mes.setContenu(cursor.getString(2));
                mes.setTime(Timestamp.valueOf(cursor.getString(3)));
                foods.add(mes);
            } while (cursor.moveToNext());
        }
        return foods;
    }

    public void addOne(message food) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, food.getNom());
        values.put(KEY_CONTENU, food.getContenu());
        values.put(KEY_TIME, String.valueOf(food.getTime()));
        // insertion
        db.insert(TABLE_NAME, // table
                null, values);

        db.close();
        Log.w("information","SQLite DB : Add one  : id=  " + food.getId()+ food.toString());
    }

    public int updateOne(message food) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, food.getNom());
        values.put(KEY_CONTENU, food.getContenu());
        values.put(KEY_TIME, String.valueOf(food.getTime()));
        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(food.getId()) });

        db.close();
        Log.w("information","SQLite DB : Update one  : id=  "+food.getId()+ food.toString());

        return i;
    }

}
