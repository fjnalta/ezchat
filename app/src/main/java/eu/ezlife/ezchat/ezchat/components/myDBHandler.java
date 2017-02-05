package eu.ezlife.ezchat.ezchat.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;

/**
 * Created by ajo on 05.02.2017.
 */

public class myDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ezchat.db";
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FROM = "from";
    private static final String COLUMN_TEXT = "text";


    public myDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_MESSAGES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_FROM + " TEXT " +
                COLUMN_TEXT + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    public void addMessage(ChatHistoryEntry message) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FROM,message.getFrom());
        values.put(COLUMN_TEXT,message.getBody());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public void deleteMessage(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_ID + "=\"" + id + "\";");
    }

    public String getMessage(int id) {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_ID + "=\"" + id + "\";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("text")) != null ) {
                dbString += c.getString(c.getColumnIndex("text"));
                dbString += "\n";
            }
        }
        db.close();;
        return dbString;
    }
}
