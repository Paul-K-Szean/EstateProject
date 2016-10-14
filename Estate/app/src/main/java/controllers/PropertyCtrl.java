package controllers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import entities.Lease;
import entities.Property;
import entities.Sale;
import entities.User;
import handler.SQLiteHandler;

/**
 * Created by Paul K Szean on 26/9/2016.
 */

public class PropertyCtrl {
    private static final String TAG = PropertyCtrl.class.getSimpleName();
    private SQLiteHandler db;

    private Property property;


    // local database for property
    public static final String DATABASE_NAME = "Estate";

    // table name
    public static final String TABLE_PROPERTY = "estate_property";

    // table columns names
    public static String KEY_PROPERTY_PROPERTYID = "propertyID";
    public static String KEY_PROPERTY_OWNER = "ownerID";
    public static String KEY_PROPERTY_FLATTYPE = "flattype";
    public static String KEY_PROPERTY_DEALTYPE = "dealtype";
    public static String KEY_PROPERTY_TITLE = "title";
    public static String KEY_PROPERTY_DESC = "description";
    public static String KEY_PROPERTY_FURNISHLEVEL = "furnishlevel";
    public static String KEY_PROPERTY_PRICE = "price";
    public static String KEY_PROPERTY_POSTALCODE = "postalcode";
    public static String KEY_PROPERTY_UNIT = "unit";
    public static String KEY_PROPERTY_ADDRESSNAME = "addressname";
    public static String KEY_PROPERTY_PHOTO = "photo";
    public static String KEY_PROPERTY_STATUS = "status";
    public static String KEY_PROPERTY_NOOFBEDROOMS = "noofbedrooms";
    public static String KEY_PROPERTY_NOOFBATHROOMS = "noofbathrooms";
    public static String KEY_PROPERTY_FLOORAREA = "floorarea";
    public static String KEY_PROPERTY_WHOLEAPARTMENT = "wholeapartment";
    public static String KEY_PROPERTY_CREATEDDATE = "createddate";

    public PropertyCtrl(Context context) {
        // SQLite database handler
        db = new SQLiteHandler(context);
    }

    // add property details into local db
    public void addPropertyDetails(Property property) {
        db.addProperty(property);
    }

    // get property details from local db
    public Property getUserPropertyDetails(String propertyID, User owner) {
        // Fetching user details from sqlite
        HashMap<String, String> savedProperty = db.getUserProperty(propertyID);

        if (savedProperty != null) {

            Sale sale = new Sale(
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                    owner,
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_FLATTYPE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_TITLE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_DESC),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_PRICE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_POSTALCODE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_UNIT),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_PHOTO),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_STATUS),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_FLOORAREA),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_CREATEDDATE)
            );

            Lease lease = new Lease(
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                    owner,
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_FLATTYPE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_TITLE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_DESC),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_PRICE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_POSTALCODE),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_UNIT),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_PHOTO),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_STATUS),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));

            if (savedProperty.get(PropertyCtrl.KEY_PROPERTY_DEALTYPE).toLowerCase().contains("rent")) {
                Log.i(TAG, lease.getOwner().getName());
                return lease;
            } else {
                Log.i(TAG, sale.getOwner().getName());
                return sale;
            }
        } else {
            Log.e(TAG, "No property data from local database.");
            return null;
        }
    }

    public ArrayList<Property> getUserProperties(User owner) {
        return db.getUserProperties(owner);
    }

    public void deletePropertyDetails() {
        db.deleteProperty();
    }

}
