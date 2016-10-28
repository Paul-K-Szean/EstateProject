package controllers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Favourite;
import entities.Property;
import entities.User;
import estateco.estate.R;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.JSONHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.ViewAdapterRecycler;

import static android.view.View.VISIBLE;
import static controllers.EstateConfig.URL_USERFAVOURITELISTINGS;
import static controllers.PropertyCtrl.KEY_ACTION_DECREASEFAVOURITE;
import static controllers.PropertyCtrl.KEY_ACTION_INCREASEFAVOURITE;
import static controllers.PropertyCtrl.KEY_ACTION_INCREASEVIEW;
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
import static controllers.UserCtrl.KEY_CONTACT;
import static controllers.UserCtrl.KEY_EMAIL;
import static controllers.UserCtrl.KEY_NAME;
import static controllers.UserCtrl.KEY_USERID;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class FavouriteCtrl {
    // table name
    public static final String TABLE_FAVOURITE = "estate_favourite";
    private static final String TAG = FavouriteCtrl.class.getSimpleName();
    // table columns names
    public static String KEY_FAVOURITEID = "favouriteID";
    public static String KEY_FAVOURITE_PROPERTYID = "propertyID";
    public static String KEY_FAVOURITE_OWNERID = "ownerID";
    public static String KEY_FAVOURITE_CREATEDDATE = "createddate";
    private SessionHandler session;
    private SQLiteHandler db;
    private View view;
    private User user;
    private Favourite favourite;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;


    public FavouriteCtrl(Context context) {
        // SQLite database handler
        db = new SQLiteHandler(context);
        userCtrl = new UserCtrl(context);
        propertyCtrl = new PropertyCtrl(context);
    }

    public FavouriteCtrl(Context context, SessionHandler session) {
        // SQLite database handler
        db = new SQLiteHandler(context);
        this.session = session;
        userCtrl = new UserCtrl(context);
        propertyCtrl = new PropertyCtrl(context);
    }

    // **********************************************************************
    // ********************* LOCAL SQLITE DATABASE ACCESS *******************
    // **********************************************************************
    // add favourite property details into local db
    public void addFavouritePropertyDetails(Favourite favourite) {
        db.addFavouriteProperty(favourite);
    }

    // get user favourite property details from local db
    public Favourite getUserFavouritePropertyDetails(User user, Property property) {
        // Fetching user details from sqlite
        HashMap<String, String> savedFavouriteProperty = db.getUserFavouriteProperty(user.getUserID(), property.getPropertyID());
        if (savedFavouriteProperty != null) {
            Log.i(TAG, "Retrieving propertyID: " + savedFavouriteProperty.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
            favourite = new Favourite(
                    savedFavouriteProperty.get(KEY_FAVOURITEID),
                    user,
                    property,
                    savedFavouriteProperty.get(KEY_FAVOURITE_CREATEDDATE));
            return favourite;
        } else {
            return null;
        }
    }

    // get user favourite properties from local db
    public ArrayList<Favourite> getUserFavouriteProperties(User user, Property property) {
        return db.getUserFavouriteProperties(user, property);
    }


    public int getUserFarvouriteCountDetails() {
        return db.getUserFavouriteCount();
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
    public void serverNewFavouriteProperty(final Fragment fragment, final User user, final Property property) {
        Log.i(TAG, "serverNewFavouriteProperty");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_FAVOURITE_OWNERID, user.getUserID());
        paramValues.put(KEY_FAVOURITE_PROPERTYID, property.getPropertyID());

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
                                user,
                                property,
                                jsonObject.getString(KEY_FAVOURITE_CREATEDDATE)
                        );
                        // save to local DB
                        EstateCtrl.syncUserNewFavouritePropertyToLocalDB(favourite);
                        // update drawer values
                        new EstateCtrl().updateDisplayValues(fragment);
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
    public void serverDeleteFavouriteProperty(final Fragment fragment, final User user, final Property property) {
        Log.i(TAG, "serverDeleteFavouriteProperty");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_FAVOURITE_OWNERID, user.getUserID());
        paramValues.put(KEY_FAVOURITE_PROPERTYID, property.getPropertyID());

        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_DELETEFAVOURITEPROPERTY, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            // server side deleted favourite property
            public void onAsyncTaskResponse(String response) {
                String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_LONG).show();
                favourite = getUserFavouritePropertyDetails(user, property);
                // delete from local DB
                EstateCtrl.syncUserDeletedFavouritePropertyToLocalDB(favourite);
                // update drawer values
                new EstateCtrl().updateDisplayValues(fragment);
            }
        }).execute();

    }


    public void serverUpdatePropertyCount(final Fragment fragment, final Property property, final String action, final MenuItem menuItem) {
        Log.i(TAG, "serverUpdatePropertyCount");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_FAVOURITE_PROPERTYID, property.getPropertyID());
        paramValues.put("action", action);
        Log.i(TAG, paramValues.toString());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_UPDATEPROPERTYCOUNT, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            // server side update favourite property
            public void onAsyncTaskResponse(String response) {
                try {
                    JSONObject jsonObject = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                    if (jsonObject != null) {

                        String count = "";
                        if (action.toString().equals(KEY_ACTION_INCREASEVIEW.toString())) {
                            count = jsonObject.getString(KEY_PROPERTY_VIEWCOUNT);
                        } else {
                            count = jsonObject.getString(KEY_PROPERTY_FAVOURITECOUNT);
                        }
                        menuItem.setTitle(count);
                    } else {
                        String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                        Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_LONG).show();
                    }

                    user = userCtrl.getUserDetails();
                    if (action.equals(KEY_ACTION_INCREASEFAVOURITE)) {
                        // add property to favourite 
                        serverNewFavouriteProperty(fragment, user, property);
                    }
                    if (action.equals(KEY_ACTION_DECREASEFAVOURITE)) {
                        // remove property from favourite 
                        serverDeleteFavouriteProperty(fragment, user, property);
                    }

                } catch (JSONException error) {
                }
            }
        }).execute();
    }

    public void serverUpdatePropertyCount(final Fragment fragment, final Property property, final String action, final TextView textView) {
        Log.i(TAG, "serverUpdatePropertyCount");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_FAVOURITE_PROPERTYID, property.getPropertyID());
        paramValues.put("action", action);
        Log.i(TAG, paramValues.toString());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_UPDATEPROPERTYCOUNT, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            // server side deleted favourite property
            public void onAsyncTaskResponse(String response) {
                try {
                    JSONObject jsonObject = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                    if (jsonObject != null) {
                        String count = jsonObject.getString(KEY_PROPERTY_FAVOURITECOUNT);
                        textView.setText(count);
                    } else {
                        String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                        Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_LONG).show();
                    }

                    user = userCtrl.getUserDetails();
                    if (action.equals(KEY_ACTION_INCREASEFAVOURITE)) {
                        // add property to favourite list
                        serverNewFavouriteProperty(fragment, user, property);
                    }
                    if (action.equals(KEY_ACTION_DECREASEFAVOURITE)) {
                        // remove property from favourite list
                        serverDeleteFavouriteProperty(fragment, user, property);
                    }

                } catch (JSONException error) {
                }
            }
        }).execute();
    }

    // get user favourite listings
    public void serverGetUserFavouriteListings(final Fragment fragment, User user) {
        Log.i(TAG, "serverGetUserFavouriteListings");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_FAVOURITE_OWNERID, user.getUserID());
        // get user favourite listings from server
        new AsyncTaskHandler(Request.Method.POST, URL_USERFAVOURITELISTINGS, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                displayListings(fragment, response);
            }
        }).execute();
    }

    private void displayListings(final Fragment fragment, String response) {
        try {
            Log.i(TAG, "displayListings");
            RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.recycleView);
            TextView tvUserFavouriteListingsCount = (TextView) fragment.getView().findViewById(R.id.TVUserFavouriteListingsCount);
            ArrayList<Property> propertyArrayList = new ArrayList<>();
            JSONArray jsonArray = JSONHandler.getResultAsArray(fragment.getActivity(), response);
            if (jsonArray != null) {
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonRecordObject = jsonArray.getJSONObject(index);
                    User owner = new User(
                            jsonRecordObject.getString(KEY_USERID),
                            jsonRecordObject.getString(KEY_NAME),
                            jsonRecordObject.getString(KEY_EMAIL),
                            jsonRecordObject.getString(KEY_CONTACT));

                    Property property = new Property(
                            jsonRecordObject.getString(KEY_PROPERTY_PROPERTYID),
                            owner,
                            jsonRecordObject.getString(KEY_PROPERTY_FLATTYPE),
                            jsonRecordObject.getString(KEY_PROPERTY_BLOCK),
                            jsonRecordObject.getString(KEY_PROPERTY_STREETNAME),
                            jsonRecordObject.getString(KEY_PROPERTY_FLOORLEVEL),
                            jsonRecordObject.getString(KEY_PROPERTY_FLOORAREA),
                            jsonRecordObject.getString(KEY_PROPERTY_PRICE),
                            jsonRecordObject.getString(KEY_PROPERTY_IMAGE),
                            jsonRecordObject.getString(KEY_PROPERTY_STATUS),
                            jsonRecordObject.getString(KEY_PROPERTY_DEALTYPE),
                            jsonRecordObject.getString(KEY_PROPERTY_TITLE),
                            jsonRecordObject.getString(KEY_PROPERTY_DESC),
                            jsonRecordObject.getString(KEY_PROPERTY_FURNISHLEVEL),
                            jsonRecordObject.getString(KEY_PROPERTY_BEDROOMCOUNT),
                            jsonRecordObject.getString(KEY_PROPERTY_BATHROOMCOUNT),
                            jsonRecordObject.getString(KEY_PROPERTY_FAVOURITECOUNT),
                            jsonRecordObject.getString(KEY_PROPERTY_VIEWCOUNT),
                            jsonRecordObject.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                            jsonRecordObject.getString(KEY_PROPERTY_CREATEDDATE));
                    propertyArrayList.add(property);
                }

                ViewAdapterRecycler viewAdapter = new ViewAdapterRecycler(fragment, propertyArrayList);
                recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
                recyclerView.setVisibility(VISIBLE);
                recyclerView.setAdapter(viewAdapter);
                tvUserFavouriteListingsCount.setText("You have " + propertyArrayList.size() + " favourite properties.");
            } else {
                String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                tvUserFavouriteListingsCount.setText(result);
            }

        } catch (JSONException error) {

        }
    }

}
