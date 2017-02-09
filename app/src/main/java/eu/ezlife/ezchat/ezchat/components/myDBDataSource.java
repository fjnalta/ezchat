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

    private String[] columns_contacts = {
            myDBHandler.COLUMN_ID,
            myDBHandler.COLUMN_USERNAME,
            myDBHandler.COLUMN_NAME,
            myDBHandler.COLUMN_AVATAR,
            myDBHandler.COLUMN_RESOURCE,
            myDBHandler.COLUMN_CONTACTS_ID
    };

    private String[] columns_messages = {
            myDBHandler.COLUMN_ID,
            myDBHandler.COLUMN_SNDR,
            myDBHandler.COLUMN_RCPT,
            myDBHandler.COLUMN_DATE,
            myDBHandler.COLUMN_CONTENT,
            myDBHandler.COLUMN_CONTACTS_ID
    };

    public myDBDataSource(Context context) {
        Log.d("myDBDataSource", "Unsere DataSource erzeugt jetzt den dbHandler.");
        dbHandler = new myDBHandler(context);
    }

    // -- General DB Handling --

    public void open() {
        Log.d("myDBDataSource", "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHandler.getWritableDatabase();
        Log.d("myDBDataSource", "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHandler.close();
        Log.d("myDBDataSource", "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    // -- Contact List Handling --

    public String getLastMessage(String username) {
        return "";
    }

    // -- Chat History Handling --

    // Create and return new Chat History Message
    public ChatHistoryEntry createMessage(String from, String to, String date, String text, int userId) {
        ContentValues values = new ContentValues();
        values.put(myDBHandler.COLUMN_SNDR, from);
        values.put(myDBHandler.COLUMN_RCPT, to);
        values.put(myDBHandler.COLUMN_DATE, date);
        values.put(myDBHandler.COLUMN_CONTENT, text);
        values.put(myDBHandler.COLUMN_CONTACTS_ID, userId);

        long insertId = database.insert(myDBHandler.TABLE_MESSAGES, null, values);

        Cursor cursor = database.query(myDBHandler.TABLE_MESSAGES,
                columns_messages, myDBHandler.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        ChatHistoryEntry myHistoryEntry = cursorToChatHistoryEntry(cursor);
        cursor.close();

        return myHistoryEntry;
    }

    // Get the Chat History for a User
    public List<ChatHistoryEntry> getAllMessages(String username) {
        List<ChatHistoryEntry> chatHistoryList = new ArrayList<>();

        Cursor cursor = database.query(myDBHandler.TABLE_MESSAGES,
                columns_messages, myDBHandler.COLUMN_USERNAME + "=\"" + username + "\"",
                null, null, null, null);

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

    // TODO - delete chat History Entry
    // TODO - delete whole chat History

    // -- Helper Methods --
    public ChatHistoryEntry getUser(String username) {
        Cursor cursor = database.query(myDBHandler.TABLE_CONTACTS,
                columns_contacts, myDBHandler.COLUMN_USERNAME + "=\"" + username + "\"",
                null, null, null, null);

        cursor.moveToFirst();
        ChatHistoryEntry myHistoryEntry = cursorToChatHistoryEntry(cursor);
        cursor.close();

        return myHistoryEntry;
    }

    // Use Cursor to return Items from DB
    private ChatHistoryEntry cursorToChatHistoryEntry(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(myDBHandler.COLUMN_ID);
        int idFrom = cursor.getColumnIndex(myDBHandler.COLUMN_SNDR);
        int idTo = cursor.getColumnIndex(myDBHandler.COLUMN_RCPT);
        int idDate = cursor.getColumnIndex(myDBHandler.COLUMN_DATE);
        int idText = cursor.getColumnIndex(myDBHandler.COLUMN_CONTENT);
        int idContactsId = cursor.getColumnIndex(myDBHandler.COLUMN_CONTACTS_ID);

        long id = cursor.getLong(idIndex);
        String from = cursor.getString(idFrom);
        String to = cursor.getString(idTo);
        String date = cursor.getString(idDate);
        String text = cursor.getString(idText);
        int contactsId = cursor.getInt(idContactsId);

        ChatHistoryEntry myHistoryEntry = new ChatHistoryEntry(id, from, to, date, text, contactsId);

        return myHistoryEntry;
    }
}
