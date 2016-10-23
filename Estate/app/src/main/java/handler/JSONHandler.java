package handler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import controllers.FavouriteCtrl;
import controllers.InboxCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Inbox;
import entities.Property;
import entities.User;

import static controllers.FavouriteCtrl.KEY_FAVOURITEID;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_CREATEDDATE;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_OWNERID;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_PROPERTYID;
import static controllers.FavouriteCtrl.TABLE_FAVOURITE;
import static controllers.InboxCtrl.KEY_INBOXID;
import static controllers.InboxCtrl.KEY_INBOXMESSAGE;
import static controllers.InboxCtrl.KEY_INBOXTITLE;
import static controllers.InboxCtrl.KEY_INBOXTYPE;
import static controllers.InboxCtrl.KEY_RECIPIENTID;
import static controllers.InboxCtrl.KEY_SENDERID;

/**
 * Created by Paul K Szean on 19/10/2016.
 */

public class JSONHandler {
    private static final String TAG = JSONHandler.class.getSimpleName();


    // JSON DATA
    public static String JSON_SUCCESS = "success";
    public static String JSON_RESULT = "result";
    public static String JSON_RECORDS = "records";

    static JSONObject jsonResponseObject;
    static JSONObject jsonResultObject;
    static JSONObject jsonRecordObject;
    static JSONArray jsonRecordArray;


    public static String getResultAsString(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            String message = jsonResponseObject.getString(JSON_RESULT);
            return message;

        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }
    }


    public static JSONObject getResultAsObject(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            if (success) {
                jsonResultObject = jsonResponseObject.getJSONObject(JSON_RESULT);
                return jsonResultObject;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }

    }

    public static JSONArray getResultAsArray(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            if (success) {
                jsonRecordArray = jsonResponseObject.getJSONArray(JSON_RESULT);
                return jsonRecordArray;
            } else {

                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }

    }

    public static JSONObject getRecordsAsObject(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            if (success) {
                jsonResultObject = jsonResponseObject.getJSONObject(JSON_RESULT);
                jsonRecordObject = jsonResultObject.getJSONObject(JSON_RECORDS);
                return jsonRecordObject;
            } else {
                return jsonResultObject;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }
    }

    public static JSONArray getRecordsAsArray(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            if (success) {
                jsonResultObject = jsonResponseObject.getJSONObject(JSON_RESULT);
                jsonRecordArray = jsonResultObject.getJSONArray(JSON_RECORDS);
                return jsonRecordArray;
            } else {
                Toast.makeText(activity, jsonResultObject.getString(JSON_RESULT), Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }

    }


}
