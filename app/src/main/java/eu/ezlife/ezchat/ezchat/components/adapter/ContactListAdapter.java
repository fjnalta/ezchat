package eu.ezlife.ezchat.ezchat.components.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 06.02.2017.
 * This class holds the contact list adapter and handles the contact list updates.
 * it notifies the view after every update.
 */
public class ContactListAdapter extends ArrayAdapter<ContactListEntry> {

    private List<ContactListEntry> contactList;
    private Activity context;

    public ContactListAdapter(Activity context, List<ContactListEntry> objects) {
        super(context, R.layout.contact_list_view, objects);
        this.contactList = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.contact_list_view, null);

        // Find the Contact
        if(contactList.size() >= position && contactList.size() != 0) {
            ContactListEntry currentEntry = contactList.get(position);

            // TODO - insert custom image
            // Fill the View
//          ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon_contactlist);
//          imageView.setImageResource(currentEntry.getAvatar());

            TextView contactNameText = (TextView) itemView.findViewById(R.id.item_text_contactName);
            contactNameText.setText(currentEntry.getNickName());

            TextView lastMessageText = (TextView) itemView.findViewById(R.id.item_text_lastMessage);
            if (currentEntry.isTyping()) {
                lastMessageText.setText("is typing ...");
            } else {
                lastMessageText.setText(currentEntry.getLastMessage());
            }

            ImageView statusImage = (ImageView) itemView.findViewById(R.id.item_icon_status);
            statusImage.setImageResource(currentEntry.getStatus());
        }

        return itemView;
    }
}
