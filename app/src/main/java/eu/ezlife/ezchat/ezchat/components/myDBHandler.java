package eu.ezlife.ezchat.ezchat.components;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ajo on 05.02.2017.
 */

public class myDBHandler extends SQLiteOpenHelper {

    public static final String DB_NAME = "ezChat.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_SETTINGS = "settings";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MSGCOUNT = "msgcount";
    public static final String COLUMN_PUSH = "push";

    public static final String COLUMN_FROM = "sndr";
    public static final String COLUMN_TO = "rcpt";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TEXT = "text";

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MSGCOUNT + " INTEGER NOT NULL, " +
                    COLUMN_PUSH + " INTEGER NOT NULL);";


    public myDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("MYDBHELPER", "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // Die onCreate-Methode wird nur aufgerufen, falls die Datenbank noch nicht existiert
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d("MYDBHELPER", "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        } catch (Exception ex) {
            Log.e("MYDBHELPER", "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
