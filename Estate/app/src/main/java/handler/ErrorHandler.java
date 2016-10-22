package handler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;

/**
 * Created by Paul K Szean on 14/10/2016.
 */

public class ErrorHandler {
    private static final String TAG = ErrorHandler.class.getSimpleName();

    public static void errorHandler(Context context, VolleyError error) {
        Log.e(TAG, "ErrorHandler: " + error.getMessage());
        error.printStackTrace();
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            Toast.makeText(context,
                    "Server Time Out...",
                    Toast.LENGTH_LONG).show();
        } else if (error instanceof AuthFailureError) {

            Toast.makeText(context,
                    "Server Authentication Failure...",
                    Toast.LENGTH_LONG).show();
        } else if (error instanceof ServerError) {

            Toast.makeText(context,
                    "Server Error...",
                    Toast.LENGTH_LONG).show();
        } else if (error instanceof NetworkError) {

            Toast.makeText(context,
                    "Server Network Error...",
                    Toast.LENGTH_LONG).show();
        } else if (error instanceof ParseError) {

            Toast.makeText(context,
                    "Server Parse Error...",
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void errorHandler(Context context, JSONException error) {
        Log.e(TAG, "ErrorHandler: " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(context,
                "Json Error...",
                Toast.LENGTH_LONG).show();

    }
//
}
