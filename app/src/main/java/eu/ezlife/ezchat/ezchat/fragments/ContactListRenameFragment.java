package eu.ezlife.ezchat.ezchat.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import eu.ezlife.ezchat.ezchat.R;
import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

public class ContactListRenameFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ContactListRenameListener {
        void onDialogRenameClick(DialogFragment dialog, String name);
        void onDialogCancelClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ContactListRenameFragment.ContactListRenameListener listener;


    public ContactListEntry getCurrentContact() {
        return currentContact;
    }

    public void setCurrentContact(ContactListEntry currentContact) {
        this.currentContact = currentContact;
    }

    private ContactListEntry currentContact;

    // Instantiate ContactListDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ContactListDialogListener so we can send events to the host
            listener = (ContactListRenameFragment.ContactListRenameListener) context;
        } catch (ClassCastException e) {
            // Throw exception if the host doesn't implement the Interface
            throw new ClassCastException("must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rename, null);

        final EditText name = (EditText) view.findViewById(R.id.dialog_rename_username);

        builder.setView(view);
        builder.setMessage("Rename Contact")
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("ContactListRenameFrag", currentContact.getJid().toString());
                        listener.onDialogRenameClick(ContactListRenameFragment.this, name.getText().toString());
                        name.setText("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogCancelClick(ContactListRenameFragment.this);
                    }
                });
        // Create the AlertDialog object
        return builder.create();
    }

}
