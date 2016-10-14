package handler;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import controllers.EstateCtrl;

/**
 * Created by Paul K Szean on 14/10/2016.
 */

public class BackgroundTaskHandler extends AsyncTask<String, String, String> {
    private static final String TAG = BackgroundTaskHandler.class.getSimpleName();
    private static String URLResponse;
    private static String URLAddress;
    private static int methodType;
    private static Map<String, String> paramToPost;
    private static Activity activity;
    private AsyncTaskResponse asyncTaskResponse;
    // Tag used to cancel the request
    String tag_string_req = "req_backgroundtask";
    Boolean IsInternetConnected = false;

    public BackgroundTaskHandler(int MethodType, String URLAddress, Map<String, String> paramToPost, Activity activity, AsyncTaskResponse asyncTaskResponse) {
        this.methodType = MethodType;
        this.URLAddress = URLAddress;
        this.paramToPost = paramToPost;
        this.activity = activity;
        this.asyncTaskResponse = asyncTaskResponse;
    }

    @Override
    protected void onPreExecute() {
        Log.w("onPreExecute", "onPreExecute()");
        IsInternetConnected = EstateCtrl.CheckInternetConnection(activity);
    }

    @Override
    protected String doInBackground(final String... params) {
        Log.w("doInBackground", "doInBackground()");
        if (IsInternetConnected) {
            // Connect to server
            StringRequest strReq = new StringRequest(methodType,
                    URLAddress, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "BackgroundTaskHandler Response: " + response.toString());

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (error) {
                            // not json error, user input error or etc(db)...
                            String errorMsg = jObj.getString("error_msg");
                            //  display response msg
                            ErrorHandler.errorHandler(activity.getApplicationContext(), errorMsg);
                        } else {

                        }
                        URLResponse = response;
                        publishProgress(response);
                    } catch (JSONException error) {
                        // json error
                        ErrorHandler.errorHandler(activity.getApplicationContext(), error);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // volley error
                    ErrorHandler.errorHandler(activity.getApplicationContext(), error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Log.i(TAG, "getParams()");
                    // posting params to server side
                    return paramToPost;
                }
            };

            // Adding request to request queue
            EstateCtrl.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
        return URLResponse;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        Log.w("onProgressUpdate", "onProgressUpdate()");
        asyncTaskResponse.onAsyncTaskResponse(progress[0]);
    }

    @Override
    protected void onPostExecute(String response) {
        Log.w("onPostExecute", "onPostExecute()");
        // asyncTaskResponse.onAsyncTaskResponse(response);
    }
}
