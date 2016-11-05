package handler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

import controllers.EstateConfig;
import controllers.EstateCtrl;

/**
 * Created by Paul K Szean on 14/10/2016.
 */

public class AsyncTaskHandler extends AsyncTask<String, String, String> {
    private static final String TAG = AsyncTaskHandler.class.getSimpleName();
    private static String URLResponse;
    private static String URLAddress;
    private static int methodType;
    private static Map<String, String> paramToPost;
    public ProgressDialog pDialog;
    // Tag used to cancel the request
    String tag_string_req = "req_" + TAG;
    Boolean IsInternetConnected = false;
    private Activity activity;
    private AsyncTaskResponse asyncTaskResponse;

    public AsyncTaskHandler(int MethodType, String URLAddress, Map<String, String> paramToPost, Activity activity, AsyncTaskResponse asyncTaskResponse) {
        Log.i(TAG, URLAddress);
        methodType = MethodType;
        AsyncTaskHandler.URLAddress = URLAddress;
        AsyncTaskHandler.paramToPost = paramToPost;
        this.activity = activity;
        this.asyncTaskResponse = asyncTaskResponse;
    }

    @Override
    protected void onPreExecute() {
        Log.w("onPreExecute", "onPreExecute()");
        IsInternetConnected = EstateCtrl.CheckInternetConnection(activity);
        // Progress dialog
        if (pDialog == null)
            pDialog = new ProgressDialog(activity);
        pDialog.setCancelable(false);
        // disable dialog for some request
        if (
                URLAddress.toLowerCase().equals(EstateConfig.URL_NEWFAVOURITEPROPERTY) ||
                        URLAddress.toLowerCase().equals(EstateConfig.URL_DELETEFAVOURITEPROPERTY)) {
            // disable loading dialog
            pDialog.setIndeterminate(false);
            pDialog.setMessage("");
            pDialog.dismiss();
        } else {
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Loading ...");

            if (pDialog.isShowing())
                pDialog.hide();
            pDialog.show();
        }
        Utility.hideSoftKeyboard(activity);
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
                    Log.d(TAG, "onResponse: " + response.toString());
                    URLResponse = response;
                    publishProgress(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // volley error
                    ErrorHandler.errorHandler(activity.getApplicationContext(), error);
                    pDialog.dismiss();
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

        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    protected void onPostExecute(String response) {
        Log.w("onPostExecute", "onPostExecute()");
        // asyncTaskResponse.onAsyncTaskResponse(response);
    }

}

