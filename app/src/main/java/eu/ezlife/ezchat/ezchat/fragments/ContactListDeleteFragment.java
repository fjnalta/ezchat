package eu.ezlife.ezchat.ezchat.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import eu.ezlife.ezchat.ezchat.data.ContactListEntry;

public class ContactListDeleteFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ContactListDialogListener {
        void onDialogDeleteClick(DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ContactListDialogListener listener;


    private ContactListEntry currentContact;

    // Instantiate ContactListDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ContactListDialogListener so we can send events to the host
            listener = (ContactListDialogListener) context;
        } catch (ClassCastException e) {
            // Throw exception if the host doesn't implement the Interface
            throw new ClassCastException("must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you really want to remote the contact?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogDeleteClick(ContactListDeleteFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogCancelClick(ContactListDeleteFragment.this);
                    }
                });
        // Create the AlertDialog object
        return builder.create();
    }

    /*
     * Interface Setter & Getter
     */
    public ContactListEntry getCurrentContact() {
        return currentContact;
    }

    public void setCurrentContact(ContactListEntry currentContact) {
        this.currentContact = currentContact;
    }
}
