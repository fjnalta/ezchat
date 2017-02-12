package eu.ezlife.ezchat.ezchat.components;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ajo on 05.02.2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    // Debug Tag
    public static final String TAG = "MyDBHelper";
    // DB-Settings
    public static final String DB_NAME = "ezChat.db";
    public static final int DB_VERSION = 20;
    // Tables
    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_MESSAGES = "messages";
    // General Table Rows
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CONTACTS_ID = "contacts_id";
    // Settings Table Rows
    public static final String COLUMN_MESSAGES = "messages";
    public static final String COLUMN_PUSH = "push";
    // Contacts Table Rows
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_CONTACT_NAME = "contact_name";
    // Message Table Rows
    public static final String COLUMN_SNDR = "sndr";
    public static final String COLUMN_RCPT = "rcpt";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONTENT = "content";

    // Contacts Table SQL String
    public static final String SQL_CREATE_CONTACTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_AVATAR + " INTEGER, " +
                    COLUMN_CONTACT_NAME + " TEXT);";

    // Settings Table SQL String
    public static final String SQL_CREATE_SETTINGS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MESSAGES + " INTEGER NOT NULL, " +
                    COLUMN_PUSH + " INTEGER NOT NULL, " +
                    COLUMN_CONTACTS_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_CONTACTS_ID + ") REFERENCES " +
                    TABLE_CONTACTS + "(" + COLUMN_ID + "));";

    // Messages Table SQL String
    public static final String SQL_CREATE_MESSAGES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGES +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SNDR + " TEXT, " +
                    COLUMN_RCPT + " TEXT, " +
                    COLUMN_DATE  + " TEXT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_CONTACTS_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_CONTACTS_ID + ") REFERENCES " +
                    TABLE_CONTACTS + "(" + COLUMN_ID + "));";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // Die onCreate-Methode wird nur aufgerufen, falls die Datenbank noch nicht existiert
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "GAYDELETE");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);

            Log.d(TAG, "Contact Table wird angelegt.");
            db.execSQL(SQL_CREATE_CONTACTS_TABLE);
            Log.d(TAG, "Settings Table wird angelegt.");
            db.execSQL(SQL_CREATE_SETTINGS_TABLE);
            Log.d(TAG, "Messages Table wird angelegt.");
            db.execSQL(SQL_CREATE_MESSAGES_TABLE);
        } catch (Exception ex) {
            Log.e(TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Die Datenbank wird gelöscht.");
            onCreate(db);
        } catch (Exception ex) {
            Log.e(TAG, "Fehler beim Löschen der Datenbank" + ex.getMessage());
        }
    }
}
