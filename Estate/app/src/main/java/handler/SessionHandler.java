package handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Paul K Szean on 24/9/2016.
 * Maintains session data across the app using the SharedPreferences.
 * Store a boolean flag isLoggedIn in shared preferences to check the login status.
 */

public class SessionHandler {
    // Shared preferences file name
    private static final String PREF_NAME = "EstateLocalUser";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    // LogCat tag
    private static String TAG = SessionHandler.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    public SessionHandler(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified to " + isLoggedIn());
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
