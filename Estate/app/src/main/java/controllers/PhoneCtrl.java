package controllers;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Paul K Szean on 11/11/2016.
 */

public class PhoneCtrl {

    public void makeCall(Fragment fragment, String contact) {
        fragment.getActivity().startActivity(new Intent(Intent.ACTION_CALL)
                .setData(Uri.parse("tel:" + contact))
        );
    }

    public void sendMessage(Fragment fragment, String contact, String title) {
        fragment.getActivity().startActivity(new Intent(Intent.ACTION_VIEW)
                .putExtra("sms_body", "Hi, i found your listing: " + title +
                        " from Estate App and I would like to find out more.")
                .setType("vnd.android-dir/mms-sms")
                .setData(Uri.parse("sms:" + contact))

        );
    }

}
