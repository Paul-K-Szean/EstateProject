package controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import entities.User;
import estateco.estate.LoginUI;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.JSONHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;

/**
 * Created by Paul K Szean on 30/9/2016.
 */

public class UserCtrl {
    // table name
    public static final String TABLE_USER = "estate_user";
    // table columns names
    public static final String KEY_USERID = "userID";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CONTACT = "contact";
    private static final String TAG = UserCtrl.class.getSimpleName();
    private SessionHandler session;
    private SQLiteHandler db;
    private User user;


    public UserCtrl(Context context) {
        // SQLite database handler
        db = new SQLiteHandler(context);

    }

    public UserCtrl(Context context, SessionHandler session) {
        // SQLite database handler
        db = new SQLiteHandler(context);
        this.session = session;
    }


    // **********************************************************************
    // ********************* LOCAL SQLITE DATABASE ACCESS *******************
    // **********************************************************************
    // add user details into local db
    public void addUserDetails(User user) {
        db.addUser(user);
    }

    // get user details from local db
    public User getUserDetails() {
        // Fetching user details from sqlite
        HashMap<String, String> savedUser = db.getUserDetails();
        if (savedUser != null) {
            user = new User(
                    savedUser.get(KEY_USERID),
                    savedUser.get(KEY_NAME),
                    savedUser.get(KEY_EMAIL),
                    savedUser.get(KEY_PASSWORD),
                    savedUser.get(KEY_CONTACT)
            );
            return user;
        } else {
            Log.e(TAG, "No user data from local database.");
            return null;
        }
    }

    // update user details from local db
    public void updateUserDetails(User user) {
        db.updateUser(user);
    }

    // remove user details from local db
    public void deleteUserTable() {
        db.deleteUserTable();
    }

    // **********************************************************************
    // ********************* REMOTE WAMP SERVER ACCESS **********************
    // **********************************************************************
    public void serverUserLogin(final Activity activity, final SessionHandler session, String valLoginEmail, String valLoginPassword) {
        Log.i(TAG, "serverUserLogin");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(UserCtrl.KEY_EMAIL, valLoginEmail);
        paramValues.put(UserCtrl.KEY_PASSWORD, valLoginPassword);
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_LOGIN, paramValues, activity, new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                try {
                    JSONObject jsonObject = JSONHandler.getResultAsObject(activity, response);
                    if (jsonObject != null) {

                        session.setLogin(true);
                        // server side created account
                        user = new User(
                                jsonObject.getString(KEY_USERID),
                                jsonObject.getString(KEY_NAME),
                                jsonObject.getString(KEY_EMAIL),
                                jsonObject.getString(KEY_CONTACT)
                        );
                        EstateCtrl.syncUserAccountToLocalDB(user);
                        EstateCtrl.syncUserPropertiesToLocalDB(activity, user);
                    } else {
                        String result = JSONHandler.getResultAsString(activity, response);
                        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException error) {
                    ErrorHandler.errorHandler(activity, error);
                }
            }
        }).execute();
    }

    public void serverUserRegister(final Activity activity, String valRegName, String valRegEmail, String valRegPassword02, String valRegContact) {
        Log.i(TAG, "serverUserRegister");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(UserCtrl.KEY_NAME, valRegName);
        paramValues.put(UserCtrl.KEY_EMAIL, valRegEmail);
        paramValues.put(UserCtrl.KEY_PASSWORD, valRegPassword02);
        paramValues.put(UserCtrl.KEY_CONTACT, valRegContact);
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_REGISTER, paramValues, activity, new AsyncTaskResponse() {
            @Override
            // server side created account
            public void onAsyncTaskResponse(String response) {
                String result = JSONHandler.getResultAsString(activity, response);
                Log.i(TAG, "serverUserRegister: " + result.toString());
                Toast.makeText(activity, result, Toast.LENGTH_LONG).show();

                if (result.contains("successfully")) {
                    activity.startActivity(new Intent(activity, LoginUI.class));
                    activity.finish();
                }
            }
        }).execute();
    }
}

