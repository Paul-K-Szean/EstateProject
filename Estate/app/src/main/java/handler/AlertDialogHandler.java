package handler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by Paul K Szean on 15/10/2016.
 */

public class AlertDialogHandler extends DialogFragment {
    private static final String TAG = AlertDialogHandler.class.getSimpleName();
    public static Fragment fragment;
    // Use this instance of the interface to deliver action events
    AlertDialogResponse mListener;

    public static void showMyDialog(Fragment fragment) {
        AlertDialogHandler dialog = new AlertDialogHandler();
        dialog.show(fragment.getFragmentManager(), "AlertDialogResponse");
        AlertDialogHandler.fragment = fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure?");
        builder.setMessage("Hit 'yes' to continue the update.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(AlertDialogHandler.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(AlertDialogHandler.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AlertDialogResponse) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement AlertDialogResponse");
        }
    }


}

