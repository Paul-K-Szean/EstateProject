package handler;

import android.support.v4.app.DialogFragment;

/**
 * Created by Paul K Szean on 15/10/2016.
 */

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it. */

public interface AlertDialogResponse {
    void onDialogPositiveClick(DialogFragment dialog);

    void onDialogNegativeClick(DialogFragment dialog);
}
