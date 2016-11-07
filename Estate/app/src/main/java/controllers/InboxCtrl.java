package controllers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Inbox;
import entities.Property;
import entities.User;
import estateco.estate.FragmentComment;
import estateco.estate.R;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.JSONHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.ViewAdapterRecyclerComments;

import static android.view.View.VISIBLE;
import static controllers.EstateConfig.URL_GETINBOX;
import static controllers.EstateConfig.URL_NEWNOTIFICATION;
import static controllers.EstateConfig.URL_PUSHNOTIFICATION;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;
import static controllers.UserCtrl.KEY_CONTACT;
import static controllers.UserCtrl.KEY_EMAIL;
import static controllers.UserCtrl.KEY_NAME;
import static controllers.UserCtrl.KEY_USERID;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class InboxCtrl {
    // table name
    public static final String TABLE_INBOX = "estate_inbox";
    private static final String TAG = InboxCtrl.class.getSimpleName();
    // table columns names
    public static String KEY_INBOXID = "inboxID";
    public static String KEY_SENDERID = "senderID";
    public static String KEY_RECIPIENTID = "recipientID";
    public static String KEY_INBOXTYPE = "inboxtype";
    public static String KEY_INBOXTITLE = "inboxtitle";
    public static String KEY_INBOXMESSAGE = "inboxmessage";
    public static String KEY_INBOXSTATUS = "inboxstatus";
    public static String KEY_CREATEDDATE = "createddate";


    public static String KEY_INBOXTYPE_COMMENT = "Property Comment";
    public static String KEY_INBOXTYPE_FAVOURITENOTIFICATION = "Favourite Notification";

    public static String KEY_INBOXSTATUS_DISPLAY = "Display";
    public static String KEY_INBOXSTATUS_NOTREAD = "Not Read";
    public static String KEY_INBOXSTATUS_READ = "Read";
    public static String KEY_INBOXSTATUS_CANCELLED = "Cancelled";

    public static String KEY_FCMTOKEN = "fcmtoken";

    private SessionHandler session;
    private SQLiteHandler db;
    private Inbox inbox;


    public InboxCtrl(Context context) {
        // SQLite database handler
        db = new SQLiteHandler(context);
    }

    public InboxCtrl(Context context, SessionHandler session) {
        // SQLite database handler
        db = new SQLiteHandler(context);
        this.session = session;
    }

    // **********************************************************************
    // ********************* REMOTE WAMP SERVER ACCESS **********************
    // **********************************************************************

    // create new property
    public void serverNewInbox(final Fragment fragment, final Inbox inbox) {
        Log.i(TAG, "serverNewInbox");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_SENDERID, inbox.getSender().getUserID());
        paramValues.put(KEY_RECIPIENTID, inbox.getRecipientID());
        paramValues.put(KEY_INBOXTYPE, inbox.getInboxtype());
        paramValues.put(KEY_INBOXTITLE, inbox.getInboxtitle());
        paramValues.put(KEY_INBOXMESSAGE, inbox.getInboxmessage());
        paramValues.put(KEY_INBOXSTATUS, inbox.getInboxstatus());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_NEWINBOX, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                JSONObject jsonObject = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                if (jsonObject != null) {
                    // server side created comment
                    Toast.makeText(fragment.getActivity(), "You have commented!", Toast.LENGTH_LONG).show();
                    if (inbox.getInboxtype().equals(KEY_INBOXTYPE_COMMENT))
                        Log.i(TAG, "Comment successfully created!");

                } else {
                    String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                    Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_SHORT).show();
                }

                if (fragment.getClass().getSimpleName().equals(FragmentComment.class.getSimpleName()))
                    // refresh recycle list
                    serverGetInboxPropertyComment(fragment, inbox);

            }
        }).execute();
    }

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
                paramValues.put(KEY_USERID, user.getUserID());
                paramValues.put(KEY_FCMTOKEN, refreshedToken);
                return paramValues;
            }
        };
        // Adding request to request queue
        EstateCtrl.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    // create new notification into fcm
    public void serverPushNotification(final Activity activity, final Inbox inbox, final Property property) {
        Log.i(TAG, "serverPushNotification");
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
                paramValues.put(KEY_USERID, property.getOwner().getUserID());
                paramValues.put(KEY_PROPERTY_PROPERTYID, property.getPropertyID());
                paramValues.put("title", inbox.getInboxtitle());
                paramValues.put("message", inbox.getInboxmessage());
                return paramValues;
            }
        };
        // Adding request to request queue
        EstateCtrl.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    // update inbox status
    public void serverUpdateInbox(final Fragment fragment, final Inbox inbox) {
        Log.i(TAG, "serverUpdateInbox");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_INBOXID, inbox.getInboxID());
        paramValues.put(KEY_INBOXTYPE, inbox.getInboxtitle());
        paramValues.put(KEY_INBOXMESSAGE, inbox.getInboxmessage());
        paramValues.put(KEY_INBOXSTATUS, inbox.getInboxstatus());
        Log.i(TAG, paramValues.toString());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_UPDATEINBOX, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            // server side update favourite property
            public void onAsyncTaskResponse(String response) {
                JSONObject jsonObject = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                if (jsonObject != null) {
                    Log.i(TAG, jsonObject.toString());
                }
            }
        }).execute();
    }


    // get comments for a listings
    public void serverGetInboxPropertyComment(final Fragment fragment, final Inbox inbox) {
        Log.i(TAG, "serverGetInboxPropertyComment");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_RECIPIENTID, inbox.getRecipientID());
        paramValues.put(KEY_INBOXTYPE, inbox.getInboxtype());

        // get user favourite listings from server
        new AsyncTaskHandler(Request.Method.POST, URL_GETINBOX, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                displayPropertyComments(fragment, response, inbox);
            }
        }).execute();
    }

    private void displayPropertyComments(final Fragment fragment, String response, Inbox inbox) {
        try {
            Log.i(TAG, "displayPropertyComments");

            RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.recycleView);

            ArrayList<Inbox> inboxArrayList = new ArrayList<>();
            JSONArray jsonArray = JSONHandler.getResultAsArray(fragment.getActivity(), response);
            if (jsonArray != null) {
                User owner = null;
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    User sender = new User(
                            jsonObject.getString(KEY_USERID),
                            jsonObject.getString(KEY_NAME),
                            jsonObject.getString(KEY_EMAIL),
                            jsonObject.getString(KEY_CONTACT));

                    owner = new User(
                            jsonObject.getString("ownerid"),
                            jsonObject.getString("ownername"),
                            jsonObject.getString("owneremail"),
                            jsonObject.getString("ownercontact"));


                    Inbox inboxToShow = new Inbox(
                            jsonObject.getString(KEY_INBOXID),
                            sender,
                            jsonObject.getString(KEY_RECIPIENTID),
                            jsonObject.getString(KEY_INBOXTYPE),
                            jsonObject.getString(KEY_INBOXTITLE),
                            jsonObject.getString(KEY_INBOXMESSAGE),
                            jsonObject.getString(KEY_INBOXSTATUS),
                            jsonObject.getString(KEY_CREATEDDATE));
                    inboxArrayList.add(inboxToShow);
                    Log.i(TAG, "inboxToShow user: " + inboxToShow.getSender().getUserID() + ", " + inboxToShow.getSender().getName());
                }

                ViewAdapterRecyclerComments viewAdapter = new ViewAdapterRecyclerComments(fragment, inboxArrayList, owner);
                recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
                recyclerView.setVisibility(VISIBLE);
                recyclerView.setAdapter(viewAdapter);

            } else {
                String result = JSONHandler.getResultAsString(fragment.getActivity(), response);

            }

        } catch (JSONException error) {
            ErrorHandler.errorHandler(fragment.getActivity(), error);
        }
    }
}