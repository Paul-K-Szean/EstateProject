package controllers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import entities.Lease;
import entities.Property;
import entities.Sale;
import entities.User;
import enums.DealType;
import handler.AsyncTaskResponse;
import handler.AsyncTaskHandler;
import handler.ErrorHandler;

/**
 * Created by Paul K Szean on 24/9/2016.
 */

public class EstateCtrl extends Application {
    public static final String TAG = EstateCtrl.class.getSimpleName();
    private RequestQueue requestQueue;
    private static EstateCtrl mInstance;
    private static UserCtrl userCtrl;
    private static PropertyCtrl propertyCtrl;
    private static User user;
    private static Property property;
    private static Sale sale;
    private static Lease lease;

    private static Map<String, String> paramValues;

    public EstateCtrl() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized EstateCtrl getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static void hideSoftKeyboard(Activity activity) {
        Log.i(TAG, "hideSoftKeyboard()");
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            View focusedView = activity.getCurrentFocus();
            if (focusedView != null) {
                inputMethodManager.hideSoftInputFromWindow(
                        focusedView.getWindowToken(), 0);
            }
        }
    }

    public static Boolean CheckInternetConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if ((networkInfo == null) || (networkInfo.isConnected() == false))
            return false;

        return (networkInfo != null && networkInfo.isConnected());
    }

    // get user data from server into local db
    public static void syncToLocalDB(final Activity activity, final User user) {
        Log.i(TAG, "syncToLocalDB");
        userCtrl = new UserCtrl(getInstance());
        propertyCtrl = new PropertyCtrl(getInstance());
        // insert user into local DB
        userCtrl.addUserDetails(user);

        // insert user properties into local DB
        paramValues = new HashMap<>();
        paramValues.put(PropertyCtrl.KEY_PROPERTY_OWNERID, user.getUserID());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_GETUSERLISTINGS, paramValues, activity, new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                Log.i(TAG, response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (error) {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        ErrorHandler.errorHandler(activity, errorMsg);
                    } else {
                        JSONArray results = jObj.getJSONArray("result");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject propertyObj = results.getJSONObject(i);
                            if (propertyObj.get(PropertyCtrl.KEY_PROPERTY_DEALTYPE).equals(DealType.ForSale.toString())) {
                                Log.i(TAG, "Syncing propertyID: " + propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
                                sale = new Sale(
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                        user,
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLATTYPE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_TITLE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DESC),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PRICE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_POSTALCODE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_UNIT),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PHOTO),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_STATUS),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));
                                // insert data into local db
                                Log.i(TAG, "Floor Area: " + sale.getFloorArea() + ", CreatedDate: " + sale.getCreatedate());
                                propertyCtrl.addPropertyDetails(sale);

                            }
                            if (propertyObj.get(PropertyCtrl.KEY_PROPERTY_DEALTYPE).equals(DealType.ForLease.toString())) {
                                Log.i(TAG, "Syncing propertyID: " + propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
                                lease = new Lease(
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                        user,
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLATTYPE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_TITLE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DESC),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PRICE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_POSTALCODE),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_UNIT),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PHOTO),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_STATUS),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT),
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));
                                // insert data into local db
                                Log.i(TAG, "Floor Area: " + lease.getWholeApartment() + ", CreatedDate: " + lease.getCreatedate());
                                propertyCtrl.addPropertyDetails(lease);
                            }
                        }// end of for loop
                    }// end of else
                } catch (JSONException error) {
                    // JSON error
                    ErrorHandler.errorHandler(activity, error);
                }
            }
        }).execute();

    }

}
