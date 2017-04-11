package eu.ezlife.ezchat.ezchat.components.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.components.server.XMPPConnection;
import eu.ezlife.ezchat.ezchat.data.ChatHistoryEntry;

/**
 * Created by ajo on 11.02.2017.
 */

public class ChatHistoryAdapter extends ArrayAdapter<ChatHistoryEntry> {

    private List<ChatHistoryEntry> chatHistory;
    private Activity context;

    public ChatHistoryAdapter(Activity context, List<ChatHistoryEntry> objects) {
        super(context, R.layout.chat_view_left, objects);
        this.chatHistory = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView;

        // Find the Entry
        ChatHistoryEntry currentEntry = chatHistory.get(position);

        // check if message is outgoing or incoming
        if(isMyMessage(currentEntry)) {
            itemView = inflater.inflate(R.layout.chat_view_left, null);
        } else {
            itemView = inflater.inflate(R.layout.chat_view_right, null);
        }

        // Fill the View
        TextView timeStamp = (TextView) itemView.findViewById(R.id.item_text_timestamp);
        timeStamp.setText(currentEntry.getDate());
        TextView msg = (TextView) itemView.findViewById(R.id.item_text_message);
        msg.setBackgroundColor(1);
        msg.setText(currentEntry.getFrom() + ": " + currentEntry.getBody());

        return itemView;
    }

    private boolean isMyMessage(ChatHistoryEntry msg){

        if(msg.getTo().asBareJid().toString().equals(XMPPConnection.getConnection().getUser().asEntityBareJid().toString())) {
            return true;
        } else {
            return false;
        }
    }
}