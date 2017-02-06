package eu.ezlife.ezchat.ezchat.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.util.List;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 06.02.2017.
 */

public class myContactListAdapter extends ArrayAdapter<ContactListEntry> {

    private final List<ContactListEntry> contactList;
    private final Activity context;

    public myContactListAdapter(Activity context, List<ContactListEntry> objects) {
        super(context, R.layout.contact_list_view, objects);
        this.contactList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.contact_list_view, null);

        // Find the Contact
        ContactListEntry currentEntry = contactList.get(position);

        // Fill the View
//        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon_contactlist);
//        imageView.setImageResource(currentEntry.getIconID());

        TextView contactNameText = (TextView) itemView.findViewById(R.id.item_text_contactName);
        contactNameText.setText(currentEntry.getContactName());

        TextView lastMessageText = (TextView) itemView.findViewById(R.id.item_text_lastMessage);
        if (currentEntry.isTyping()) {
            lastMessageText.setText("is typing ...");
        } else {
            lastMessageText.setText(currentEntry.getLastMessage());
        }

 //       Log.d("ContactListAdapter", currentEntry.getIconID() + "");

 //       imageView = (ImageView) imageView.findViewById(R.id.item_icon_status);
 //       imageView.setImageResource(currentEntry.getIconID());

        return itemView;
    }
}
