package eu.ezlife.ezchat.ezchat.components.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.ezlife.ezchat.ezchat.components.XMPPConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactEntry;

/**
 * Created by ajo on 06.02.2017.
 */

public class DBDataSource {

    private DBHandler dbHandler;
    private SQLiteDatabase database;

    private String[] columns_contacts = {
            DBHandler.COLUMN_ID,
            DBHandler.COLUMN_USERNAME,
            DBHandler.COLUMN_NAME,
            DBHandler.COLUMN_AVATAR,
            DBHandler.COLUMN_CONTACT_NAME,
    };

    private String[] columns_messages = {
            DBHandler.COLUMN_ID,
            DBHandler.COLUMN_SNDR,
            DBHandler.COLUMN_RCPT,
            DBHandler.COLUMN_DATE,
            DBHandler.COLUMN_CONTENT,
            DBHandler.COLUMN_CONTACTS_ID
    };

    public DBDataSource(Context context) {
        Log.d("DBDataSource", "Unsere DataSource erzeugt jetzt den dbHandler.");
        dbHandler = new DBHandler(context);
    }

    // -- General DB Handling --

    public void open() {
        Log.d("DBDataSource", "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHandler.getWritableDatabase();
        Log.d("DBDataSource", "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHandler.close();
        Log.d("DBDataSource", "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    // -- ContactEntry Handling

    public ContactEntry createContact(String username, String name, int avatar, String contactName) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_USERNAME, username);
        values.put(DBHandler.COLUMN_NAME, name);
        values.put(DBHandler.COLUMN_AVATAR, avatar);
        values.put(DBHandler.COLUMN_CONTACT_NAME, contactName);

        long insertId = database.insert(DBHandler.TABLE_CONTACTS, null, values);

        Cursor cursor = database.query(DBHandler.TABLE_CONTACTS,
                columns_contacts, DBHandler.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        ContactEntry myContact = cursorToContact(cursor);
        cursor.close();

        return myContact;
    }

    public ContactEntry getContact(String username) {
        ContactEntry myContact = null;

        Cursor cursor = database.query(DBHandler.TABLE_CONTACTS,
                columns_contacts, DBHandler.COLUMN_USERNAME + "=\"" + username + "\"",
                null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            myContact = cursorToContact(cursor);
            cursor.moveToNext();
        }

        cursor.close();
        return myContact;
    }

    public List<ContactEntry> getContacts() {
        List<ContactEntry> contactEntries = new ArrayList<>();

        Cursor cursor = database.query(DBHandler.TABLE_CONTACTS,
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
        ChatHistoryEntry currentEntry = null;
        Cursor cursor = database.query(DBHandler.TABLE_MESSAGES,
                columns_messages, DBHandler.COLUMN_CONTACTS_ID + "=" + getContact(username).getId(),
                null, null, null, DBHandler.COLUMN_ID + " DESC");
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            currentEntry = cursorToChatHistoryEntry(cursor);
            cursor.close();

            if(currentEntry != null) {
                if(isMyMessage(currentEntry)) {

                    return "-> - " + currentEntry.getBody();
                } else {
                    return "<- - " + currentEntry.getBody();
                }
            } else {
                return " - ";
            }
        } else {
            return " - ";
        }
    }

    // -- Chat History Handling --

    // Create and return new Chat History Message
    public ChatHistoryEntry createMessage(String from, String to, String text, String date, long userId) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_SNDR, from);
        values.put(DBHandler.COLUMN_RCPT, to);
        values.put(DBHandler.COLUMN_DATE, date);
        values.put(DBHandler.COLUMN_CONTENT, text);
        values.put(DBHandler.COLUMN_CONTACTS_ID, userId);

        long insertId = database.insert(DBHandler.TABLE_MESSAGES, null, values);

        Cursor cursor = database.query(DBHandler.TABLE_MESSAGES,
                columns_messages, DBHandler.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        ChatHistoryEntry myHistoryEntry = cursorToChatHistoryEntry(cursor);
        cursor.close();

        return myHistoryEntry;
    }

    // Get the Chat History for a User
    public List<ChatHistoryEntry> getChatHistory(long contactId) {
        List<ChatHistoryEntry> chatHistoryList = new ArrayList<>();

        Cursor cursor = database.query(DBHandler.TABLE_MESSAGES,
                columns_messages, DBHandler.COLUMN_CONTACTS_ID + "=\"" + contactId + "\"",
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
        long id = cursor.getLong(cursor.getColumnIndex(DBHandler.COLUMN_ID));
        String user = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_USERNAME));
        String name = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_NAME));
        int avatar = cursor.getInt(cursor.getColumnIndex(DBHandler.COLUMN_AVATAR));
        String contactName = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_CONTACT_NAME));

        ContactEntry myContact = new ContactEntry(id, user, name, avatar, contactName);

        return myContact;
    }

    private ChatHistoryEntry cursorToChatHistoryEntry(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DBHandler.COLUMN_ID));
        String from = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_SNDR));
        String to = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_RCPT));
        String date = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_DATE));
        String text = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_CONTENT));
        int contactsId = cursor.getInt(cursor.getColumnIndex(DBHandler.COLUMN_CONTACTS_ID));

        ChatHistoryEntry myHistoryEntry = new ChatHistoryEntry(id, from, to, date, text, contactsId);

        return myHistoryEntry;
    }

    private boolean isMyMessage(ChatHistoryEntry msg){
        if(msg.getTo().equals(cutResourceFromUsername(XMPPConnection.getConnection().getUser()))) {
            return false;
        } else {
            return true;
        }
    }

    private String cutResourceFromUsername(String username) {
        String str = username;
        int dotIndex = str.indexOf("@");
        str = str.substring(0, dotIndex);
        return str;
    }
}

