package eu.ezlife.ezchat.ezchat.components.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jxmpp.jid.Jid;
import java.util.List;
import eu.ezlife.ezchat.ezchat.R;

import static eu.ezlife.ezchat.ezchat.components.xmppServices.XMPPService.handler;

/**
 * Created by ajo on 08.09.2019.
 * This class holds the new subscriptions adapter and handles new subscription updates.
 * it notifies the view after every update.
 * currently not used because all subscriptions are auto accepted - see XMPPHandler
 */
public class NewContactAdapter extends ArrayAdapter<Jid> {

    private List<Jid> newContactList;
    private Activity context;

    public NewContactAdapter(Activity context, List<Jid> objects) {
        super(context, R.layout.new_contact_list_view, objects);
        this.newContactList = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.new_contact_list_view, null);

        // Find the Contact
        if(newContactList.size() >= position) {
            final Jid currentEntry = newContactList.get(position);

            TextView contactNameText = (TextView) itemView.findViewById(R.id.item_text_new_contactName);
            contactNameText.setText(currentEntry.asBareJid());

            ImageButton acceptButton = (ImageButton) itemView.findViewById(R.id.imageButton);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.addContact(currentEntry.toString());
                }
            });

            ImageButton declineImage = (ImageButton) itemView.findViewById(R.id.imageButton2);
            declineImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.addContact(currentEntry.toString());
                }
            });

        }
        return itemView;
    }
}
