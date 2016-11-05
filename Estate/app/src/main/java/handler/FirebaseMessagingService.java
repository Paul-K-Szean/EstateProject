package handler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import estateco.estate.PropertyDetailsUI;

import static android.R.attr.smallIcon;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;

/**
 * Created by Paul K Szean on 4/11/2016.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived");
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        // notify user
        String propertyID = remoteMessage.getData().get(KEY_PROPERTY_PROPERTYID);
        String nTitle = remoteMessage.getData().get("title");
        String nMessage = remoteMessage.getData().get("message");

        // Creates an explicit intent for an Activity in your app
        Intent intent = new Intent(this, PropertyDetailsUI.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY_PROPERTY_PROPERTYID, propertyID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(smallIcon)
                        .setContentTitle(nTitle)
                        .setContentText(nMessage)
                        .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // allows you to update the notification later on.
        notificationManager.notify(0, nBuilder.build());
    }
}
