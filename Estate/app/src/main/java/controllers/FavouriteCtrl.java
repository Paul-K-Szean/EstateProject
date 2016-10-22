package controllers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Favourite;
import entities.User;
import estateco.estate.R;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.JSONHandler;
import handler.SessionHandler;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class FavouriteCtrl {
    private static final String TAG = FavouriteCtrl.class.getSimpleName();

    private SessionHandler session;
    private JSONHandler.SQLiteHandler db;

    private View view;
    private Favourite favourite;

    // table name
    public static final String TABLE_FAVOURITE = "estate_favourite";

    // table columns names
    public static String KEY_FAVOURITEID = "favouriteID";
    public static String KEY_FAVOURITE_PROPERTYID = "propertyID";
    public static String KEY_FAVOURITE_OWNERID = "ownerID";
    public static String KEY_FAVOURITE_CREATEDDATE = "createddate";


    public FavouriteCtrl(Context context) {
        // SQLite database handler
        db = new JSONHandler.SQLiteHandler(context);
    }

    public FavouriteCtrl(Context context, SessionHandler session) {
        // SQLite database handler
        db = new JSONHandler.SQLiteHandler(context);
        this.session = session;
    }

    // **********************************************************************
    // ********************* LOCAL SQLITE DATABASE ACCESS *******************
    // **********************************************************************
    // add favourite property details into local db
    public void addFavouritePropertyDetails(Favourite favourite) {
        db.addFavouriteProperty(favourite);
    }

    // get user favourite property details from local db
    public Favourite getUserFavouritePropertyDetails(String propertyID) {
        // Fetching user details from sqlite
        HashMap<String, String> savedFavouriteProperty = db.getUserFavouriteProperty(propertyID);
        if (savedFavouriteProperty != null) {
            Log.i(TAG, "Retrieving propertyID: " + savedFavouriteProperty.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
            favourite = new Favourite(
                    savedFavouriteProperty.get(KEY_FAVOURITEID),
                    savedFavouriteProperty.get(KEY_FAVOURITE_OWNERID),
                    savedFavouriteProperty.get(KEY_FAVOURITE_PROPERTYID),
                    savedFavouriteProperty.get(KEY_FAVOURITE_CREATEDDATE));
            return favourite;
        } else {
            Log.e(TAG, "No favourite property data from local database.");
            return null;
        }
    }

    // get user favourite properties from local db
    public ArrayList<Favourite> getUserFavouriteProperties(User owner) {
        return db.getUserFavouriteProperties(owner);
    }

    // delete a favourite property from local db
    public void deleteFavouriteProperty(Favourite favourite) {
        db.deleteFavouriteProperty(favourite);
    }

    // remove all favourite property from local db
    public void deleteFavouritePropertyTable() {
        db.deleteFavouritePropertyTable();
    }

    // **********************************************************************
    // ********************* REMOTE WAMP SERVER ACCESS **********************
    // **********************************************************************

    // create new favourite property
    public void serverNewFavouriteProperty(final Fragment fragment, final Favourite favourite) {
        Log.i(TAG, "serverNewFavouriteProperty");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_FAVOURITE_OWNERID, favourite.getOwnerID());
        paramValues.put(KEY_FAVOURITE_PROPERTYID, favourite.getPropertyID());

        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_NEWFAVOURITEPROPERTY, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {

                try {
                    JSONObject jsonObject = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                    if (jsonObject != null) {
                        // server side created property
                        Toast.makeText(fragment.getActivity(), "Favourited your property!", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Favourited your property!");
                        Favourite favourite = new Favourite(
                                jsonObject.getString(KEY_FAVOURITEID),
                                jsonObject.getString(KEY_FAVOURITE_OWNERID),
                                jsonObject.getString(KEY_FAVOURITE_PROPERTYID),
                                jsonObject.getString(KEY_FAVOURITE_CREATEDDATE)
                        );
                        // save to local DB
                        EstateCtrl.syncUserFavouritePropertyToLocalDB(favourite);
                        // display total count
                        serverGetFavouriteCount(fragment, favourite);
                    } else {
                        String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                        Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException error) {
                    ErrorHandler.errorHandler(fragment.getActivity(), error);
                }

            }
        }).execute();
    }

    // delete favourite property
    public void serverDeleteFavouriteProperty(final Fragment fragment, final Favourite favourite) {
        Log.i(TAG, "serverDeleteFavouriteProperty");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_FAVOURITE_OWNERID, favourite.getOwnerID());
        paramValues.put(KEY_FAVOURITE_PROPERTYID, favourite.getPropertyID());

        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_DELETEFAVOURITEPROPERTY, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            // server side deleted favourite property
            public void onAsyncTaskResponse(String response) {
                String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                Log.i(TAG, "serverDeleteFavouriteProperty: " + result);
                Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_LONG).show();
                // delete from local DB
                EstateCtrl.syncUserDeletedFavouritePropertyToLocalDB(favourite);
                // display total count
                serverGetFavouriteCount(fragment, favourite);
            }
        }).execute();

    }

    // get favourite count of a property
    public void serverGetFavouriteCount(final Fragment fragment, final Favourite favourite) {
        Log.i(TAG, "serverGetFavouriteCount");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, favourite.getPropertyID());

        if (paramValues != null) {
            new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_FAVOURITECOUNT, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
                @Override
                public void onAsyncTaskResponse(String response) {
                    String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                    if (result != null) {
                        Toolbar toolBarBottom = (Toolbar) fragment.getActivity().findViewById(R.id.toolbar_bottom);
                        MenuItem menuItemFavouriteCount = toolBarBottom.getMenu().findItem(R.id.action_favouritecount);
                        menuItemFavouriteCount.setTitle(result);
                    }
                }
            }).execute();
        }

    }


}
