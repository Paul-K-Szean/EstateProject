package handler;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import controllers.InboxCtrl;
import controllers.UserCtrl;

/**
 * Created by Paul K Szean on 4/11/2016.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseInstanceIDService.class.getSimpleName();

    private InboxCtrl inboxCtrl;
    private UserCtrl userCtrl;

    @Override
    public void onTokenRefresh() {
        Log.i(TAG, "onTokenRefresh");

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, token);


    }


}