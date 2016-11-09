package handler;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import controllers.EstateCtrl;
import controllers.CommentCtrl;
import controllers.NotificationCtrl;
import controllers.UserCtrl;
import entities.User;

/**
 * Created by Paul K Szean on 7/11/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName().toString();
    private EstateCtrl estateCtrl;
    private UserCtrl userCtrl;
    private NotificationCtrl notificationCtrl;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        // sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {

        userCtrl = new UserCtrl(EstateCtrl.getInstance().getApplicationContext());
        notificationCtrl = new NotificationCtrl();
        User user = userCtrl.getUserDetails();
        notificationCtrl.serverNewNotification(user, refreshedToken);

    }


}
