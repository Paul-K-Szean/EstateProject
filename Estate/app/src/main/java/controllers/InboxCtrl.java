package controllers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import entities.Favourite;
import entities.Inbox;
import entities.User;
import handler.JSONHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;

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
    // add favourite property details into local db
    public void addInbox(Inbox inbox) {
        db.addInbox(inbox);
    }

    // get user favourite property details from local db
    public Inbox getUserInbox(String propertyID) {
        // Fetching user details from sqlite
        HashMap<String, String> savedInbox = db.getUserFavouriteProperty(propertyID);
        if (savedInbox != null) {
            Log.i(TAG, "Retrieving propertyID: " + savedInbox.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
            inbox = new Inbox(
                    savedInbox.get(KEY_INBOXID),
                    savedInbox.get(KEY_SENDERID),
                    savedInbox.get(KEY_RECIPIENTID),
                    savedInbox.get(KEY_INBOXTYPE),
                    savedInbox.get(KEY_INBOXTITLE),
                    savedInbox.get(KEY_INBOXMESSAGE),
                    savedInbox.get(KEY_CREATEDDATE));
            return inbox;
        } else {
            Log.e(TAG, "No favourite property data from local database.");
            return null;
        }
    }

    // get user favourite properties from local db
    public ArrayList<Favourite> getInboxs(User owner) {
        return db.getUserFavouriteProperties(owner);
    }

    // delete a favourite property from local db
    public void deleteFavouriteProperty(Favourite favourite) {
        db.deleteFavouriteProperty(favourite);
    }

    // remove all favourite property from local db
    public void deleteFavouritePropertyTable() {
        db.deleteFavouritePropertyTable();
    }

    // **********************************************************************
    // ********************* REMOTE WAMP SERVER ACCESS **********************
    // **********************************************************************


}
