package controllers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Property;
import entities.User;
import estateco.estate.FragmentUserListings;
import estateco.estate.JSONHandler;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.FragmentHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;

/**
 * Created by Paul K Szean on 26/9/2016.
 */

public class PropertyCtrl {
    private static final String TAG = PropertyCtrl.class.getSimpleName();

    private SessionHandler session;
    private SQLiteHandler db;
    private Property property;
    // local database for property
    public static final String DATABASE_NAME = "Estate";

    // table name
    public static final String TABLE_PROPERTY = "estate_property";

    // table columns names
    public static String KEY_PROPERTY_PROPERTYID = "propertyID";
    public static String KEY_PROPERTY_OWNERID = "ownerID";
    public static String KEY_PROPERTY_FLATTYPE = "flattype";
    public static String KEY_PROPERTY_BLOCK = "block";
    public static String KEY_PROPERTY_STREETNAME = "streetname";
    public static String KEY_PROPERTY_FLOORLEVEL = "floorlevel";
    public static String KEY_PROPERTY_FLOORAREA = "floorarea";
    public static String KEY_PROPERTY_PRICE = "price";
    public static String KEY_PROPERTY_IMAGE = "image";
    public static String KEY_PROPERTY_STATUS = "status";
    public static String KEY_PROPERTY_DEALTYPE = "dealtype";
    public static String KEY_PROPERTY_TITLE = "title";
    public static String KEY_PROPERTY_DESC = "description";
    public static String KEY_PROPERTY_FURNISHLEVEL = "furnishlevel";
    public static String KEY_PROPERTY_BEDROOMCOUNT = "bedroomcount";
    public static String KEY_PROPERTY_BATHROOMCOUNT = "bathroomcount";
    public static String KEY_PROPERTY_WHOLEAPARTMENT = "wholeapartment";
    public static String KEY_PROPERTY_CREATEDDATE = "createddate";
    // extras
    public static String KEY_PROPERTY_SEARCH = "searchvalue";
    public static String KEY_PROPERTY_ROOM = "room";

    public PropertyCtrl(Context context) {
        // SQLite database handler
        db = new SQLiteHandler(context);
    }

    public PropertyCtrl(Context context, SessionHandler session) {
        // SQLite database handler
        db = new SQLiteHandler(context);
        this.session = session;
    }

    // add property details into local db
    public void addPropertyDetails(Property property) {
        db.addProperty(property);
    }

    // get user property details from local db
    public Property getUserPropertyDetails(String propertyID, User owner) {
        // Fetching user details from sqlite
        HashMap<String, String> savedProperty = db.getUserProperty(propertyID);
        if (savedProperty != null) {
            Log.i(TAG, "Retrieving propertyID: " + savedProperty.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
            property = new Property(
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                    owner,
                    savedProperty.get(KEY_PROPERTY_FLATTYPE),
                    savedProperty.get(KEY_PROPERTY_BLOCK),
                    savedProperty.get(KEY_PROPERTY_STREETNAME),
                    savedProperty.get(KEY_PROPERTY_FLOORLEVEL),
                    savedProperty.get(KEY_PROPERTY_FLOORAREA),
                    savedProperty.get(KEY_PROPERTY_PRICE),
                    savedProperty.get(KEY_PROPERTY_IMAGE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_STATUS),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_TITLE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_DESC),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));
            return property;
        } else {
            Log.e(TAG, "No property data from local database.");
            return null;
        }
    }

    // get user properties from local db
    public ArrayList<Property> getUserProperties(User owner) {
        return db.getUserProperties(owner);
    }

    // update user property from local db

    public void updateUserPropertyDetails(Property property) {
        db.updateUserProperty(property);
    }

    // remove all property from local db
    public void deletePropertyDetails() {
        db.deleteProperty();
    }


    // **********************************************************************
    // ********************* LOCAL SQLITE DATABASE ACCESS *******************
    // **********************************************************************
    public void serverNewProperty(final Activity activity, final Fragment fragment, final Property property, final User user) {
        Log.i(TAG, "serverNewProperty");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_PROPERTY_OWNERID, property.getOwner().getUserID());
        paramValues.put(KEY_PROPERTY_FLATTYPE, property.getFlatType());
        paramValues.put(KEY_PROPERTY_BLOCK, property.getBlock());
        paramValues.put(KEY_PROPERTY_STREETNAME, property.getStreetname());
        paramValues.put(KEY_PROPERTY_FLOORLEVEL, property.getFloorlevel());
        paramValues.put(KEY_PROPERTY_FLOORAREA, property.getFloorarea());
        paramValues.put(KEY_PROPERTY_PRICE, property.getPrice());
        paramValues.put(KEY_PROPERTY_IMAGE, property.getImage());
        paramValues.put(KEY_PROPERTY_STATUS, property.getStatus());
        paramValues.put(KEY_PROPERTY_DEALTYPE, property.getDealType());
        paramValues.put(KEY_PROPERTY_TITLE, property.getTitle());
        paramValues.put(KEY_PROPERTY_DESC, property.getDescription());
        paramValues.put(KEY_PROPERTY_FURNISHLEVEL, property.getFurnishLevel());
        paramValues.put(KEY_PROPERTY_BEDROOMCOUNT, property.getBedroomcount());
        paramValues.put(KEY_PROPERTY_BATHROOMCOUNT, property.getBathroomcount());
        paramValues.put(KEY_PROPERTY_WHOLEAPARTMENT, property.getWholeapartment());

        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_NEWPROPERTY, paramValues, activity, new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {

                try {
                    JSONObject propertyObj = JSONHandler.getResultAsObject(activity, response);
                    if (propertyObj != null) {
                        // server side created property
                        Toast.makeText(activity, propertyObj.toString(), Toast.LENGTH_LONG);
                        Log.i(TAG, "Property successfully created!");
                        Property property = new Property(
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
                                propertyObj.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                                propertyObj.getString(KEY_PROPERTY_CREATEDDATE));
                        // save to local DB
                        EstateCtrl.syncUserPropertyToLocalDB(property);
                        FragmentHandler.loadFragment(fragment, new FragmentUserListings());
                    }
                } catch (JSONException error) {
                    ErrorHandler.errorHandler(activity, error);
                }
            }
        }).execute();
    }

}
