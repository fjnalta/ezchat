package eu.ezlife.ezchat.ezchat.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;

/**
 * Created by ajo on 06.02.2017.
 */

public class myDBDataSource {

    private SQLiteDatabase database;
    private myDBHandler dbHandler;

    private String[] columns = {
            myDBHandler.COLUMN_ID,
            myDBHandler.COLUMN_FROM,
            myDBHandler.COLUMN_TO,
            myDBHandler.COLUMN_DATE,
            myDBHandler.COLUMN_TEXT
    };


    public myDBDataSource(Context context) {
        Log.d("myDBDataSource", "Unsere DataSource erzeugt jetzt den dbHandler.");
        dbHandler = new myDBHandler(context);
    }

    public void open() {
        Log.d("myDBDataSource", "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHandler.getWritableDatabase();
        Log.d("myDBDataSource", "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHandler.close();
        Log.d("myDBDataSource", "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public ChatHistoryEntry createMessage(String from, String to, String date, String text) {
        String tableName = validateTable(from, to);

        ContentValues values = new ContentValues();
        values.put(myDBHandler.COLUMN_FROM, from);
        values.put(myDBHandler.COLUMN_TO, to);
        values.put(myDBHandler.COLUMN_DATE, date);
        values.put(myDBHandler.COLUMN_TEXT, text);

        long insertId = database.insert(tableName, null, values);

        Cursor cursor = database.query(tableName,
                columns, myDBHandler.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        ChatHistoryEntry myHistoryEntry = cursorToChatHistoryEntry(cursor);
        cursor.close();

        return myHistoryEntry;
    }

    private ChatHistoryEntry cursorToChatHistoryEntry(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(myDBHandler.COLUMN_ID);
        int idFrom = cursor.getColumnIndex(myDBHandler.COLUMN_FROM);
        int idTo = cursor.getColumnIndex(myDBHandler.COLUMN_TO);
        int idDate = cursor.getColumnIndex(myDBHandler.COLUMN_DATE);
        int idText = cursor.getColumnIndex(myDBHandler.COLUMN_TEXT);

        long id = cursor.getLong(idIndex);
        String from = cursor.getString(idFrom);
        String to = cursor.getString(idTo);
        String date = cursor.getString(idDate);
        String text = cursor.getString(idText);

        ChatHistoryEntry myHistoryEntry = new ChatHistoryEntry(id, from, to, date, text);

        return myHistoryEntry;
    }

    public List<ChatHistoryEntry> getAllMessages(String tableName) {
        List<ChatHistoryEntry> chatHistoryList = new ArrayList<>();

        Cursor cursor = database.query(tableName,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        ChatHistoryEntry entry;

        while(!cursor.isAfterLast()) {
            entry = cursorToChatHistoryEntry(cursor);
            chatHistoryList.add(entry);
            cursor.moveToNext();
        }
        cursor.close();
        return chatHistoryList;
    }

    public void createNewMessageTable(String tableName) {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName +
                "(" + myDBHandler.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                myDBHandler.COLUMN_FROM  + " TEXT, " +
                myDBHandler.COLUMN_TO  + " TEXT, " +
                myDBHandler.COLUMN_DATE  + " TEXT, " +
                myDBHandler.COLUMN_TEXT  + " TEXT" + ")";
        database.execSQL(query);
    }

    public String validateTable(String from, String to){
        String tableName = "";
        if ( from.equals(myXMPPConnection.getUsername())) {
            createNewMessageTable("message_" + to);
            tableName = "message_" + to;
        } else {
            createNewMessageTable("message_" + from);
            tableName = "message_" + from;
        }

        return tableName;
    }
}
