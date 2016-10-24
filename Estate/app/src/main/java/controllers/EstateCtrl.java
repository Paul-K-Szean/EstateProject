package controllers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import entities.Favourite;
import entities.Property;
import entities.User;
import estateco.estate.FragmentUserFavouriteListings;
import estateco.estate.MainUI;
import estateco.estate.R;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.JSONHandler;

import static controllers.FavouriteCtrl.KEY_FAVOURITEID;
import static controllers.PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BLOCK;
import static controllers.PropertyCtrl.KEY_PROPERTY_CREATEDDATE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DEALTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DESC;
import static controllers.PropertyCtrl.KEY_PROPERTY_FAVOURITECOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLATTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORAREA;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_IMAGE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PRICE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;
import static controllers.PropertyCtrl.KEY_PROPERTY_STATUS;
import static controllers.PropertyCtrl.KEY_PROPERTY_STREETNAME;
import static controllers.PropertyCtrl.KEY_PROPERTY_TITLE;
import static controllers.PropertyCtrl.KEY_PROPERTY_VIEWCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT;

/**
 * Created by Paul K Szean on 24/9/2016.
 */

public class EstateCtrl extends Application {
    public static final String TAG = EstateCtrl.class.getSimpleName();
    private RequestQueue requestQueue;
    private static EstateCtrl mInstance;
    private static UserCtrl userCtrl;
    private static PropertyCtrl propertyCtrl;
    private static FavouriteCtrl favouriteCtrl;
    private static User user;
    private static Property property;
    private static Favourite favourite;
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

    // get user account into local db
    public static void syncUserAccountToLocalDB(final User user) {
        Log.i(TAG, "syncUserAccountToLocalDB");
        userCtrl = new UserCtrl(getInstance());
        // insert user account into local DB
        userCtrl.addUserDetails(user);
    }

    // add user properties into local db (from server)
    public static void syncUserPropertiesToLocalDB(final Activity activity, final User user) {
        Log.i(TAG, "syncUserPropertiesToLocalDB");
        propertyCtrl = new PropertyCtrl(getInstance());
        // get user properties from server and insert into local DB
        paramValues = new HashMap<>();
        paramValues.put(PropertyCtrl.KEY_PROPERTY_OWNERID, user.getUserID());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_GETUSERLISTINGS, paramValues, activity, new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                try {
                    JSONArray jsonArray = JSONHandler.getResultAsArray(activity, response);
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject propertyObj = jsonArray.getJSONObject(i);
                            property = new Property(
                                    propertyObj.getString(KEY_PROPERTY_PROPERTYID),
                                    user,
                                    propertyObj.getString(KEY_PROPERTY_FLATTYPE),
                                    propertyObj.getString(KEY_PROPERTY_BLOCK),
                                    propertyObj.getString(KEY_PROPERTY_STREETNAME),
                                    propertyObj.getString(KEY_PROPERTY_FLOORLEVEL),
                                    propertyObj.getString(KEY_PROPERTY_FLOORAREA),
                                    propertyObj.getString(KEY_PROPERTY_PRICE),
                                    propertyObj.getString(KEY_PROPERTY_IMAGE),
                                    propertyObj.getString(KEY_PROPERTY_STATUS),
                                    propertyObj.getString(KEY_PROPERTY_DEALTYPE),
                                    propertyObj.getString(KEY_PROPERTY_TITLE),
                                    propertyObj.getString(KEY_PROPERTY_DESC),
                                    propertyObj.getString(KEY_PROPERTY_FURNISHLEVEL),
                                    propertyObj.getString(KEY_PROPERTY_BEDROOMCOUNT),
                                    propertyObj.getString(KEY_PROPERTY_BATHROOMCOUNT),
                                    propertyObj.getString(KEY_PROPERTY_FAVOURITECOUNT),
                                    propertyObj.getString(KEY_PROPERTY_VIEWCOUNT),
                                    propertyObj.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                                    propertyObj.getString(KEY_PROPERTY_CREATEDDATE));
                            // add into local db
                            propertyCtrl.addPropertyDetails(property);
                        }// end of for loop
                    }
                } catch (JSONException error) {
                    // JSON error
                    ErrorHandler.errorHandler(activity, error);
                }

                EstateCtrl.syncUserFavouritePropertiesToLocalDB(activity, user);

            }
        }

        ).execute();

    }

    // add user property into local db
    public static void syncUserPropertyToLocalDB(Property property) {
        Log.i(TAG, "syncUserPropertyToLocalDB");
        propertyCtrl = new PropertyCtrl(getInstance());
        // insert user property into local DB
        propertyCtrl.addPropertyDetails(property);
    }

    // update existing user property into local db
    public static void syncUserUpdatedPropertyToLocalDB(Property property) {
        Log.i(TAG, "syncUserUpdatedPropertyToLocalDB");
        propertyCtrl = new PropertyCtrl(getInstance());
        // insert user property into local DB
        propertyCtrl.updateUserPropertyDetails(property);
    }

    // add user favourite properties into local db (from server)
    public static void syncUserFavouritePropertiesToLocalDB(final Activity activity, final User user) {
        Log.i(TAG, "syncUserFavouritePropertiesToLocalDB");
        favouriteCtrl = new FavouriteCtrl(getInstance());
        // get user properties from server and insert into local DB
        paramValues = new HashMap<>();
        paramValues.put(FavouriteCtrl.KEY_FAVOURITE_OWNERID, user.getUserID());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_USERFAVOURITELISTINGS, paramValues, activity, new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                try {
                    JSONArray jsonArray = JSONHandler.getResultAsArray(activity, response);
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            property = new Property(
                                    jsonObject.getString(KEY_PROPERTY_PROPERTYID),
                                    user,
                                    jsonObject.getString(KEY_PROPERTY_FLATTYPE),
                                    jsonObject.getString(KEY_PROPERTY_BLOCK),
                                    jsonObject.getString(KEY_PROPERTY_STREETNAME),
                                    jsonObject.getString(KEY_PROPERTY_FLOORLEVEL),
                                    jsonObject.getString(KEY_PROPERTY_FLOORAREA),
                                    jsonObject.getString(KEY_PROPERTY_PRICE),
                                    jsonObject.getString(KEY_PROPERTY_IMAGE),
                                    jsonObject.getString(KEY_PROPERTY_STATUS),
                                    jsonObject.getString(KEY_PROPERTY_DEALTYPE),
                                    jsonObject.getString(KEY_PROPERTY_TITLE),
                                    jsonObject.getString(KEY_PROPERTY_DESC),
                                    jsonObject.getString(KEY_PROPERTY_FURNISHLEVEL),
                                    jsonObject.getString(KEY_PROPERTY_BEDROOMCOUNT),
                                    jsonObject.getString(KEY_PROPERTY_BATHROOMCOUNT),
                                    jsonObject.getString(KEY_PROPERTY_FAVOURITECOUNT),
                                    jsonObject.getString(KEY_PROPERTY_VIEWCOUNT),
                                    jsonObject.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                                    jsonObject.getString(KEY_PROPERTY_CREATEDDATE));

                            favourite = new Favourite(
                                    jsonObject.getString(KEY_FAVOURITEID),
                                    user,
                                    property,
                                    jsonObject.getString(KEY_PROPERTY_CREATEDDATE));
                            // add into local db
                            favouriteCtrl.addFavouritePropertyDetails(favourite);
                        }// end of for loop
                    }


                } catch (JSONException error) {
                    // JSON error
                    ErrorHandler.errorHandler(activity, error);
                }


                activity.startActivity(new Intent(activity, MainUI.class));
                activity.finish();
            }
        }

        ).execute();

    }

    // add user favourite property into local db
    public static void syncUserNewFavouritePropertyToLocalDB(Favourite favourite) {
        Log.i(TAG, "syncUserNewFavouritePropertyToLocalDB");
        favouriteCtrl = new FavouriteCtrl(getInstance());
        // insert user property into local DB
        favouriteCtrl.addFavouritePropertyDetails(favourite);
    }

    // delete existing user property from local db
    public static void syncUserDeletedFavouritePropertyToLocalDB(Favourite favourite) {
        Log.i(TAG, "syncUserDeletedFavouritePropertyToLocalDB");
        favouriteCtrl = new FavouriteCtrl(getInstance());
        // insert user property into local DB
        favouriteCtrl.deleteFavouriteProperty(favourite);
    }

    // sync drawer values
    public void updateDisplayValues(Fragment fragment) {
        userCtrl = new UserCtrl(getInstance());
        propertyCtrl = new PropertyCtrl(getInstance());
        favouriteCtrl = new FavouriteCtrl(getInstance());
        // update value at drawer
        NavigationView navigationView = (NavigationView) fragment.getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setTitle("My listings (" + propertyCtrl.getUserPropertyCountDetails() + ")");
        navigationView.getMenu().getItem(2).setTitle("Favourite Listings (" + favouriteCtrl.getUserFarvouriteCountDetails() + ")");

        // check from which fragment
        // refresh user favourite list if fragment was from FragmentUserFavouriteListings
        if (fragment.getClass().getSimpleName().equals(FragmentUserFavouriteListings.class.getSimpleName())) {
            Log.i(TAG, "Reset User Favourite Listing page recycleview");
            // get user favourite listings from server
            favouriteCtrl.serverGetUserFavouriteListings(fragment, userCtrl.getUserDetails());
        }
    }


    public static int getSpinnerItemPosition(Spinner spinner, String value) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            // Log.i(TAG, spinner.getItemAtPosition(i).toString() + " " + value + " " + spinner.getItemAtPosition(i).toString().contains(value));
            if (spinner.getItemAtPosition(i).toString().contains(value)) {
                return i;
            }
        }
        return index;
    }


}
