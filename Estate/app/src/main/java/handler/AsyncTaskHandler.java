package handler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.lang.ref.WeakReference;
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

class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private static final String TAG = BitmapWorkerTask.class.getSimpleName();
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;
    private AsyncTaskResponse asyncTaskResponse;
    private Activity activity;

    public BitmapWorkerTask(Activity activity, ImageView imageView, AsyncTaskResponse asyncTaskResponse) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.activity = activity;
        this.asyncTaskResponse = asyncTaskResponse;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        // options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return decodeSampledBitmapFromResource(activity.getResources(), data, 100, 100);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}