package eu.ezlife.ezchat.ezchat.components;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

/**
 * Created by ajo on 06.02.2017.
 */

public class myContactListAdapter extends ArrayAdapter<ContactListEntry> {

    private final List<ContactListEntry> contactList;
    private final Activity context;

    private myDBDataSource dbHandler;

    public myContactListAdapter(Activity context, List<ContactListEntry> objects) {
        super(context, R.layout.contact_list_view, objects);
        this.contactList = objects;
        this.context = context;
    }

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
        if (currentEntry.getIsTyping() == 1) {
            lastMessageText.setText("is typing ...");
        } else {
            lastMessageText.setText(" - ");
        }

 //       Log.d("ContactListAdapter", currentEntry.getIconID() + "");

 //       imageView = (ImageView) imageView.findViewById(R.id.item_icon_status);
 //       imageView.setImageResource(currentEntry.getIconID());

        return itemView;
    }
}
