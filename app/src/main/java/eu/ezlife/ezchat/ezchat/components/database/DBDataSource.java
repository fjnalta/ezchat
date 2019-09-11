package eu.ezlife.ezchat.ezchat.components.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;

import eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPService;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;
import eu.ezlife.ezchat.ezchat.data.ContactEntry;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 06.02.2017.
 * This class represents the Database Controller. It should be only used in Message Handler
 */
public class DBDataSource implements XMPPService {

    private DBHandler dbHandler;
    private SQLiteDatabase database;

    private String[] columns_settings = {
            DBHandler.COLUMN_ID,
            DBHandler.COLUMN_SHORT_DESCRIPTION,
            DBHandler.COLUMN_DESCRIPTION,
            DBHandler.COLUMN_VALUE,
    };

    private String[] columns_contacts = {
            DBHandler.COLUMN_ID,
            DBHandler.COLUMN_USERNAME,
            DBHandler.COLUMN_AVATAR,
            DBHandler.COLUMN_CONTACT_NAME,
    };

    private String[] columns_messages = {
            DBHandler.COLUMN_ID,
            DBHandler.COLUMN_SNDR,
            DBHandler.COLUMN_RCPT,
            DBHandler.COLUMN_DATE,
            DBHandler.COLUMN_CONTENT,
            DBHandler.COLUMN_USERNAME
    };

    /**
     * Creates the database Connection
     *
     * @param context the actual application context
     */
    public DBDataSource(Context context) {
        Log.d("DBDataSource", "Erzeuge dbHandler.");
        dbHandler = new DBHandler(context);
    }

    // -- General DB Handling --
    /**
     * Opens the Database connection.
     * needs to be called before every sql statement
     */
    public void open() {
        database = dbHandler.getWritableDatabase();
    }

    /**
     * Closes the Database connection.
     * has to be called after every executed sql statement
     */
    public void close() {
        dbHandler.close();
    }

    // -- ContactEntry Handling
    public ContactEntry createContact(String username, int avatar, String contactName) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_USERNAME, username);
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

    public boolean deleteContact(String username) {
        ContactEntry contactEntry = getContact(username);
        return database.delete(DBHandler.TABLE_CONTACTS, DBHandler.COLUMN_ID + "=" + contactEntry.getId(), null) > 0;
    }

    public boolean renameContact(String username, String contactName) {
        ContactEntry contactEntry = getContact(username);
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_CONTACT_NAME, contactName);

        return database.update(DBHandler.TABLE_CONTACTS, values, DBHandler.COLUMN_ID + "=?", new String[] { Long.toString(contactEntry.getId()) } ) > 0;
    }

    public ContactEntry getContact(String username) {
        ContactEntry myContact = null;

        Cursor cursor = database.query(DBHandler.TABLE_CONTACTS,
                columns_contacts, DBHandler.COLUMN_USERNAME + "=\"" + username + "\"",
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                myContact = cursorToContact(cursor);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return myContact;
    }

    public boolean contactExists(String username) {
        Cursor cursor = database.rawQuery("SELECT 1 FROM " + DBHandler.TABLE_CONTACTS + " WHERE " + "\"" + DBHandler.COLUMN_USERNAME + "\"" + "= ?",
                new String[]{username});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    /*public List<ContactListEntry> getContacts() {
        List<ContactEntry> contactEntries = new ArrayList<>();
        List<ContactListEntry> myEntries = new ArrayList<>();

        Cursor cursor = database.query(DBHandler.TABLE_CONTACTS,
                columns_contacts, null,
                null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            ContactEntry entry = cursorToContact(cursor);
            contactEntries.add(entry);
            cursor.moveToNext();
        }

        cursor.close();


        for (ContactEntry entry : contactEntries) {
            myEntries.add(new ContactListEntry(entry, 0, false, false));
        }

        return myEntries;
    }*/

    // -- Contact List Handling --

    public String getLastMessage(String username) {
        ChatHistoryEntry currentEntry = null;

        Cursor cursor = database.query(DBHandler.TABLE_MESSAGES,
                columns_messages, DBHandler.COLUMN_USERNAME + "=" + getContact(username).getUsername(),
                null, null, null, DBHandler.COLUMN_ID + " DESC");
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            currentEntry = cursorToChatHistoryEntry(cursor);
            cursor.close();

            if (currentEntry != null) {
                return currentEntry.getBody();
            } else {
                return " - ";
            }
        } else {
            return " - ";
        }

    }

    // -- Chat History Handling --

    // Create and return new Chat History Message
    public ChatHistoryEntry createMessage(String from, String to, String text, String date, long username) {
        Log.d("CreateMessage", "Called");
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_SNDR, from);
        values.put(DBHandler.COLUMN_RCPT, to);
        values.put(DBHandler.COLUMN_DATE, date);
        values.put(DBHandler.COLUMN_CONTENT, text);
        values.put(DBHandler.COLUMN_USERNAME, username);

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
                columns_messages, DBHandler.COLUMN_USERNAME + "=\"" + contactId + "\"",
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

    // TODO - delete whole chat History for one contact

    // TODO - delete chat History Entry

    // Use Cursor to return Items from DB
    private ContactEntry cursorToContact(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DBHandler.COLUMN_ID));
        String user = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_USERNAME));
        int avatar = cursor.getInt(cursor.getColumnIndex(DBHandler.COLUMN_AVATAR));
        String contactName = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_CONTACT_NAME));

        ContactEntry myContact = new ContactEntry(id, user, avatar, contactName);

        return myContact;
    }

    private ChatHistoryEntry cursorToChatHistoryEntry(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DBHandler.COLUMN_ID));
        Jid from = null;
        Jid to = null;
        try {
            from = JidCreate.from(cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_SNDR)));
            to = JidCreate.from(cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_RCPT)));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        String date = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_DATE));
        String text = cursor.getString(cursor.getColumnIndex(DBHandler.COLUMN_CONTENT));
        int contactsId = cursor.getInt(cursor.getColumnIndex(DBHandler.COLUMN_USERNAME));

        ChatHistoryEntry myHistoryEntry = new ChatHistoryEntry(id, from, to, date, text, contactsId);

        return myHistoryEntry;
    }
}


