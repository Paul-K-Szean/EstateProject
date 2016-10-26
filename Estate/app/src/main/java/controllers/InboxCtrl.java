package controllers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Inbox;
import entities.Property;
import entities.User;
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
import static controllers.PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BLOCK;
import static controllers.PropertyCtrl.KEY_PROPERTY_CREATEDDATE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DEALTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DESC;
import static controllers.PropertyCtrl.KEY_PROPERTY_FAVOURITECOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLATTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORAREA;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_IMAGE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PRICE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;
import static controllers.PropertyCtrl.KEY_PROPERTY_STATUS;
import static controllers.PropertyCtrl.KEY_PROPERTY_STREETNAME;
import static controllers.PropertyCtrl.KEY_PROPERTY_TITLE;
import static controllers.PropertyCtrl.KEY_PROPERTY_VIEWCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT;
import static controllers.UserCtrl.KEY_CONTACT;
import static controllers.UserCtrl.KEY_EMAIL;
import static controllers.UserCtrl.KEY_NAME;
import static controllers.UserCtrl.KEY_USERID;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class InboxCtrl {
    private static final String TAG = InboxCtrl.class.getSimpleName();

    private SessionHandler session;
    private SQLiteHandler db;
    private Inbox inbox;
    // table name
    public static final String TABLE_INBOX = "estate_inbox";

    // table columns names
    public static String KEY_INBOXID = "inboxID";
    public static String KEY_SENDERID = "senderID";
    public static String KEY_RECIPIENTID = "recipientID";
    public static String KEY_INBOXTYPE = "inboxtype";
    public static String KEY_INBOXTITLE = "inboxtitle";
    public static String KEY_INBOXMESSAGE = "inboxmessage";
    public static String KEY_CREATEDDATE = "createddate";


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
    // ********************* LOCAL SQLITE DATABASE ACCESS *******************
    // **********************************************************************
//    // add favourite property details into local db
//    public void addInbox(Inbox inbox) {
//        db.addInbox(inbox);
//    }
//
//    // get user favourite property details from local db
//    public Inbox getUserInbox(String propertyID) {
//        // Fetching user details from sqlite
//        HashMap<String, String> savedInbox = db.getUserFavouriteProperty(user.getUserID(), propertyID);
//        if (savedInbox != null) {
//            Log.i(TAG, "Retrieving propertyID: " + savedInbox.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
//            inbox = new Inbox(
//                    savedInbox.get(KEY_INBOXID),
//                    savedInbox.get(KEY_SENDERID),
//                    savedInbox.get(KEY_RECIPIENTID),
//                    savedInbox.get(KEY_INBOXTYPE),
//                    savedInbox.get(KEY_INBOXTITLE),
//                    savedInbox.get(KEY_INBOXMESSAGE),
//                    savedInbox.get(KEY_CREATEDDATE));
//            return inbox;
//        } else {
//            Log.e(TAG, "No favourite property data from local database.");
//            return null;
//        }
//    }
//
//    // get user favourite properties from local db
//    public ArrayList<Favourite> getInboxs(User owner) {
//        return db.getUserFavouriteProperties(owner);
//    }
//
//    // delete a favourite property from local db
//    public void deleteFavouriteProperty(Favourite favourite) {
//        db.deleteFavouriteProperty(favourite);
//    }
//
//    // remove all favourite property from local db
//    public void deleteFavouritePropertyTable() {
//        db.deleteFavouritePropertyTable();
//    }

    // **********************************************************************
    // ********************* REMOTE WAMP SERVER ACCESS **********************
    // **********************************************************************

    // create new property
    public void serverNewInbox(final Fragment fragment, final Inbox inbox) {
        Log.i(TAG, "serverNewInbox");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_SENDERID, inbox.getSenderID());
        paramValues.put(KEY_RECIPIENTID, inbox.getRecipientID());
        paramValues.put(KEY_INBOXTYPE, inbox.getInboxtype());
        paramValues.put(KEY_INBOXTITLE, inbox.getInboxtitle());
        paramValues.put(KEY_INBOXMESSAGE, inbox.getInboxmessage());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_NEWINBOX, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                JSONObject jsonObject = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                if (jsonObject != null) {
                    // server side created comment
                    Toast.makeText(fragment.getActivity(), "You have commented!", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Comment successfully created!");

                } else {
                    String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                    Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_SHORT).show();
                }

                // refresh recycle list
                serverGetInboxPropertyComment(fragment, inbox);
            }
        }).execute();
    }

    // get user favourite listings
    public void serverGetInboxPropertyComment(final Fragment fragment, final Inbox inbox) {
        Log.i(TAG, "serverGetInboxPropertyComment");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_RECIPIENTID, inbox.getRecipientID());
        paramValues.put(KEY_INBOXTYPE, inbox.getInboxtype());

        // get user favourite listings from server
        new AsyncTaskHandler(Request.Method.POST, URL_GETINBOX, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                displayPropertyComments(fragment, response);
            }
        }).execute();
    }

    private void displayPropertyComments(final Fragment fragment, String response) {
        try {
            Log.i(TAG, "displayPropertyComments");
            RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.recycleView);

            ArrayList<Inbox> inboxArrayList = new ArrayList<>();
            JSONArray jsonArray = JSONHandler.getResultAsArray(fragment.getActivity(), response);
            if (jsonArray != null) {
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    User owner = new User(
                            jsonObject.getString(KEY_USERID),
                            jsonObject.getString(KEY_NAME),
                            jsonObject.getString(KEY_EMAIL),
                            jsonObject.getString(KEY_CONTACT));

                    Property property = new Property(
                            jsonObject.getString(KEY_PROPERTY_PROPERTYID),
                            owner,
                            jsonObject.getString(KEY_PROPERTY_FLATTYPE),
                            jsonObject.getString(KEY_PROPERTY_BLOCK),
                            jsonObject.getString(KEY_PROPERTY_STREETNAME),
                            jsonObject.getString(KEY_PROPERTY_FLOORLEVEL),
                            jsonObject.getString(KEY_PROPERTY_FLOORAREA),
                            jsonObject.getString(KEY_PROPERTY_PRICE),
                            jsonObject.getString(KEY_PROPERTY_IMAGE),
                            jsonObject.getString(KEY_PROPERTY_STATUS),
                            jsonObject.getString(KEY_PROPERTY_DEALTYPE),
                            jsonObject.getString(KEY_PROPERTY_TITLE),
                            jsonObject.getString(KEY_PROPERTY_DESC),
                            jsonObject.getString(KEY_PROPERTY_FURNISHLEVEL),
                            jsonObject.getString(KEY_PROPERTY_BEDROOMCOUNT),
                            jsonObject.getString(KEY_PROPERTY_BATHROOMCOUNT),
                            jsonObject.getString(KEY_PROPERTY_FAVOURITECOUNT),
                            jsonObject.getString(KEY_PROPERTY_VIEWCOUNT),
                            jsonObject.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                            jsonObject.getString(KEY_PROPERTY_CREATEDDATE));

                    inbox = new Inbox(
                            jsonObject.getString(KEY_INBOXID),
                            jsonObject.getString(KEY_SENDERID),
                            jsonObject.getString(KEY_RECIPIENTID),
                            jsonObject.getString(KEY_INBOXTYPE),
                            jsonObject.getString(KEY_INBOXTITLE),
                            jsonObject.getString(KEY_INBOXMESSAGE),
                            jsonObject.getString(KEY_CREATEDDATE));
                    inboxArrayList.add(inbox);
                }

                ViewAdapterRecyclerComments viewAdapter = new ViewAdapterRecyclerComments(fragment, inboxArrayList);
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