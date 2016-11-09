package controllers;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import entities.Notification;
import entities.Property;
import entities.User;

import static controllers.EstateConfig.URL_NEWNOTIFICATION;
import static controllers.EstateConfig.URL_PUSHNOTIFICATION;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;

/**
 * Created by Paul K Szean on 9/11/2016.
 */

public class NotificationCtrl {

    private static final String TAG = NotificationCtrl.class.getSimpleName();

    public static String KEY_NOTIFICATIONUSERID = "userID";
    public static String KEY_NOTIFICATIONFCMTOKEN = "fcmtoken";


    // create new notification into fcm
    public void serverNewNotification(final User user, final String refreshedToken) {
        Log.i(TAG, "serverNewNotification");
        Log.i(TAG, refreshedToken);
        String tag_string_req = "req_" + TAG;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_NEWNOTIFICATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "serverNewNotification onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "serverNewNotification onErrorResponse: " + error.getMessage());
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.i(TAG, "serverNewNotification getParams()");
                // posting params to server side
                Map<String, String> paramValues = new HashMap<>();
                paramValues.put(KEY_NOTIFICATIONUSERID, user.getUserID());
                paramValues.put(KEY_NOTIFICATIONFCMTOKEN, refreshedToken);
                return paramValues;
            }
        };
        // Adding request to request queue
        EstateCtrl.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    // create new notification into fcm
    public void serverPushNotification(final Notification notification, final Property property) {
        Log.i(TAG, "serverPushNotification");
        Log.i(TAG,property.getOwner().getUserID() + " lol " + property.getPropertyID());
        String tag_string_req = "req_" + TAG;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_PUSHNOTIFICATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "serverPushNotification onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "serverPushNotification onErrorResponse: " + error.getMessage());
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.i(TAG, "serverPushNotification getParams()");
                // posting params to server side
                Map<String, String> paramValues = new HashMap<>();
                paramValues.put(KEY_NOTIFICATIONUSERID, property.getOwner().getUserID());
                paramValues.put(KEY_PROPERTY_PROPERTYID, property.getPropertyID());
                paramValues.put("title", notification.getNotifyTitle());
                paramValues.put("message", notification.getNotifyMessage());
                return paramValues;
            }
        };
        // Adding request to request queue
        EstateCtrl.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

}
