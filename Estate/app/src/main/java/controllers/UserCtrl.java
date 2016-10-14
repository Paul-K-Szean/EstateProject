package controllers;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import entities.User;
import helper.SQLiteHandler;

/**
 * Created by Paul K Szean on 30/9/2016.
 */

public class UserCtrl {
    private static final String TAG = UserCtrl.class.getSimpleName();
    private SQLiteHandler db;
    private User user;

    // table name
    public static final String TABLE_USER = "estate_user";

    // table columns names
    public static final String KEY_USERID = "userID";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CONTACT = "contact";

    public UserCtrl(Context context) {
        // SQLite database handler
        db = new SQLiteHandler(context);
    }

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
                    savedUser.get("userID"),
                    savedUser.get("name"),
                    savedUser.get("email"),
                    savedUser.get("password"),
                    savedUser.get("contact")
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
    public void deleteUserDetails() {
        db.deleteUsers();
    }

}
