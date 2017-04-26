package eu.ezlife.ezchat.ezchat.components.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ajo on 05.02.2017.
 * Raw Database Interface. All Tables and columns are declared here.
 * Extends SQLLiteOpenHelper which provides onCreate and onUpgrade to handle
 * basic database functionality
 */
public class DBHandler extends SQLiteOpenHelper {

    // Debug Tag
    public static final String TAG = "MyDBHelper";
    // DB-Settings
    public static final String DB_NAME = "ezChat.db";
    public static final int DB_VERSION = 33;
    // Tables
    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_GLOBAL_SETTINGS = "global_settings";
    // General Table Rows
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CONTACTS_ID = "contacts_id";
    // Settings Table Rows
    public static final String COLUMN_MESSAGES = "messages";
    public static final String COLUMN_PUSH = "push";
    // Contacts Table Rows
    public static final String COLUMN_USERNAME = "username";
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
                    COLUMN_AVATAR + " INTEGER, " +
                    COLUMN_CONTACT_NAME + " TEXT);";

    // Contacts Table SQL String
    public static final String SQL_CREATE_GLOBAL_SETTINGS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_GLOBAL_SETTINGS +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MESSAGES + " TEXT, " +
                    COLUMN_PUSH + " INTEGER);";

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

    /**
     * On create is only called if the database doesn't exist
     * @param db the database to create
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Delete Tables");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GLOBAL_SETTINGS);

            Log.d(TAG, "Contact Table wird angelegt.");
            db.execSQL(SQL_CREATE_CONTACTS_TABLE);
            Log.d(TAG, "Settings Table wird angelegt.");
            db.execSQL(SQL_CREATE_SETTINGS_TABLE);
            Log.d(TAG, "Messages Table wird angelegt.");
            db.execSQL(SQL_CREATE_MESSAGES_TABLE);
            Log.d(TAG, "Global Settings Table wird angelegt.");
            db.execSQL(SQL_CREATE_GLOBAL_SETTINGS_TABLE);
        } catch (Exception ex) {
            Log.e(TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    /**
     * OnUpgrade is called if the database version increases. it deletes the whole db
     * by calling onCreate
     * @param db the used database
     * @param oldVersion the old database version
     * @param newVersion the new database version
     */
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