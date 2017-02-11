package eu.ezlife.ezchat.ezchat.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactEntry;

/**
 * Created by ajo on 06.02.2017.
 */

public class myDBDataSource {

    private myDBHandler dbHandler;
    private SQLiteDatabase database;

    private String[] columns_contacts = {
            myDBHandler.COLUMN_ID,
            myDBHandler.COLUMN_USERNAME,
            myDBHandler.COLUMN_NAME,
            myDBHandler.COLUMN_AVATAR,
            myDBHandler.COLUMN_CONTACT_NAME,
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

    // -- ContactEntry Handling

    public ContactEntry createContact(String username, String name, int avatar, String contactName) {
        ContentValues values = new ContentValues();
        values.put(myDBHandler.COLUMN_USERNAME, username);
        values.put(myDBHandler.COLUMN_NAME, name);
        values.put(myDBHandler.COLUMN_AVATAR, avatar);
        values.put(myDBHandler.COLUMN_CONTACT_NAME, contactName);

        long insertId = database.insert(myDBHandler.TABLE_CONTACTS, null, values);

        Cursor cursor = database.query(myDBHandler.TABLE_CONTACTS,
                columns_messages, myDBHandler.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        ContactEntry myContact = cursorToContact(cursor);
        cursor.close();

        return myContact;
    }

    public ContactEntry getContact(String username) {
        Cursor cursor = database.query(myDBHandler.TABLE_CONTACTS,
                columns_contacts, myDBHandler.COLUMN_USERNAME + "=\"" + username + "\"",
                null, null, null, null);
        cursor.moveToFirst();
        ContactEntry myContact = cursorToContact(cursor);
        cursor.close();
        return myContact;
    }

    public List<ContactEntry> getContacts() {
        List<ContactEntry> contactEntries = new ArrayList<>();

        Cursor cursor = database.query(myDBHandler.TABLE_CONTACTS,
                columns_contacts, null,
                null, null, null, null);
        cursor.moveToFirst();
        ContactEntry entry;

        while (!cursor.isAfterLast()) {
            entry = cursorToContact(cursor);
            contactEntries.add(entry);
            cursor.moveToNext();
        }
        cursor.close();

        return contactEntries;
    }

    public boolean deleteContact(String username) {

        return false;
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
    public List<ChatHistoryEntry> getChatHistory(String username) {
        List<ChatHistoryEntry> chatHistoryList = new ArrayList<>();

        Cursor cursor = database.query(myDBHandler.TABLE_MESSAGES,
                columns_messages, myDBHandler.COLUMN_USERNAME + "=\"" + username + "\"",
                null, null, null, null);

        cursor.moveToFirst();
        ChatHistoryEntry entry;

        while (!cursor.isAfterLast()) {
            entry = cursorToChatHistoryEntry(cursor);
            chatHistoryList.add(entry);
            cursor.moveToNext();
        }
        cursor.close();

        return chatHistoryList;

    }

    // TODO - delete whole chat History
    // TODO - delete chat History Entry

    // Use Cursor to return Items from DB
    private ContactEntry cursorToContact(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(myDBHandler.COLUMN_ID));
        String user = cursor.getString(cursor.getColumnIndex(myDBHandler.COLUMN_USERNAME));
        String name = cursor.getString(cursor.getColumnIndex(myDBHandler.COLUMN_NAME));
        int avatar = cursor.getInt(cursor.getColumnIndex(myDBHandler.COLUMN_AVATAR));
        String contactName = cursor.getString(cursor.getColumnIndex(myDBHandler.COLUMN_CONTACT_NAME));

        ContactEntry myContact = new ContactEntry(id, user, name, avatar, contactName);

        return myContact;
    }

    private ChatHistoryEntry cursorToChatHistoryEntry(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(myDBHandler.COLUMN_ID));
        String from = cursor.getString(cursor.getColumnIndex(myDBHandler.COLUMN_SNDR));
        String to = cursor.getString(cursor.getColumnIndex(myDBHandler.COLUMN_RCPT));
        String date = cursor.getString(cursor.getColumnIndex(myDBHandler.COLUMN_DATE));
        String text = cursor.getString(cursor.getColumnIndex(myDBHandler.COLUMN_CONTENT));
        int contactsId = cursor.getInt(cursor.getColumnIndex(myDBHandler.COLUMN_CONTACTS_ID));

        ChatHistoryEntry myHistoryEntry = new ChatHistoryEntry(id, from, to, date, text, contactsId);

        return myHistoryEntry;
    }
}
