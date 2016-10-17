package handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Lease;
import entities.Property;
import entities.Sale;
import entities.User;
import enums.DealType;

/**
 * Created by Paul K Szean on 24/9/2016.
 * Whenever user tries to log  in, fetch from SQLite instead of making request to server.
 */

public class SQLiteHandler extends SQLiteOpenHelper {
    // LogCat tag
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // local database name
    public static final String DATABASE_NAME = "Estate";

    // Database Version
    private static final int DATABASE_VERSION = 5;

    // user data table
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + UserCtrl.TABLE_USER + "("
            + UserCtrl.KEY_USERID + " INTEGER,"
            + UserCtrl.KEY_NAME + " TEXT, "
            + UserCtrl.KEY_EMAIL + " TEXT, "
            + UserCtrl.KEY_PASSWORD + " TEXT, "
            + UserCtrl.KEY_CONTACT + " TEXT "
            + ");";

    // property data table
    private static final String CREATE_PROPERTY_TABLE = "CREATE TABLE " + PropertyCtrl.TABLE_PROPERTY + "("
            + PropertyCtrl.KEY_PROPERTY_PROPERTYID + " INTEGER, "
            + PropertyCtrl.KEY_PROPERTY_OWNERID + " INTEGER, "
            + PropertyCtrl.KEY_PROPERTY_FLATTYPE + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_DEALTYPE + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_TITLE + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_DESC + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_PRICE + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_POSTALCODE + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_UNIT + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_ADDRESSNAME + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_PHOTO + " BLOB, "
            + PropertyCtrl.KEY_PROPERTY_STATUS + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS + " TEXT,"
            + PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS + " TEXT,"
            + PropertyCtrl.KEY_PROPERTY_FLOORAREA + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT + " TEXT, "
            + PropertyCtrl.KEY_PROPERTY_CREATEDDATE + " TEXT "
            + ");";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate()");
        // creating tables
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PROPERTY_TABLE);
        Log.d(TAG, "Database table created.");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade() - old version: " + oldVersion + ", new version: " + newVersion);
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + UserCtrl.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + PropertyCtrl.TABLE_PROPERTY);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(User user) {
        Log.i(TAG, "addUser()");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserCtrl.KEY_USERID, user.getUserID());
        values.put(UserCtrl.KEY_NAME, user.getName());
        values.put(UserCtrl.KEY_EMAIL, user.getEmail());
        values.put(UserCtrl.KEY_PASSWORD, user.getPassword());
        values.put(UserCtrl.KEY_CONTACT, user.getContact());

        // inserting a new row
        long id = db.insert(UserCtrl.TABLE_USER, null, values);
        // closing database connection
        db.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> userHashMap = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + UserCtrl.TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Move to first row
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Fetching user from sqlite: " + cursor.getString(cursor.getColumnIndex(UserCtrl.KEY_EMAIL)));
            userHashMap.put(UserCtrl.KEY_USERID, cursor.getString(cursor.getColumnIndex(UserCtrl.KEY_USERID)));
            userHashMap.put(UserCtrl.KEY_NAME, cursor.getString(cursor.getColumnIndex(UserCtrl.KEY_NAME)));
            userHashMap.put(UserCtrl.KEY_EMAIL, cursor.getString(cursor.getColumnIndex(UserCtrl.KEY_EMAIL)));
            userHashMap.put(UserCtrl.KEY_PASSWORD, cursor.getString(cursor.getColumnIndex(UserCtrl.KEY_PASSWORD)));
            userHashMap.put(UserCtrl.KEY_CONTACT, cursor.getString(cursor.getColumnIndex(UserCtrl.KEY_CONTACT)));

        } else {
            Log.d(TAG, "No userHashMap data to fetching from Sqlite.");
        }
        cursor.close();
        db.close();
        // return userHashMap


        return userHashMap;
    }

    /**
     * Re create database
     * Delete all tables and create them again
     */
    public void deleteUsers() {
        // TODO: update to server before deleting any user information.
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(UserCtrl.TABLE_USER, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /**
     * Update user details in database
     */
    public void updateUser(User user) {
        // retain user information here
        SQLiteDatabase db = this.getReadableDatabase();

        // new value for user
        ContentValues values = new ContentValues();
        // values.put(KEY_USERID, user.getUserID());
        values.put(UserCtrl.KEY_NAME, user.getName());
        values.put(UserCtrl.KEY_EMAIL, user.getEmail());
        values.put(UserCtrl.KEY_PASSWORD, user.getPassword());
        values.put(UserCtrl.KEY_CONTACT, user.getContact());

        // row to update, based on the title
        String selection = UserCtrl.KEY_USERID + " LIKE ?";
        String[] selectionArgs = {user.getUserID()};

        // Commit to storage
        int count = db.update(
                UserCtrl.TABLE_USER,
                values,
                selection,
                selectionArgs);
        db.close();
        if (count != 0)
            Log.d(TAG, "User information retained.");
        else
            Log.e(TAG, "Error retaining user information.");
    }


    /****************************************************************************************************************************************
     * property section
     ****************************************************************************************************************************************
     */

    /**
     * store property details in database
     */
    public void addProperty(Property property) {
        Log.i(TAG, "addProperty()");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, property.getPropertyID());
        values.put(PropertyCtrl.KEY_PROPERTY_OWNERID, property.getOwner().getUserID());
        values.put(PropertyCtrl.KEY_PROPERTY_FLATTYPE, property.getFlatType());
        values.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, property.getDealType());
        values.put(PropertyCtrl.KEY_PROPERTY_TITLE, property.getTitle());
        values.put(PropertyCtrl.KEY_PROPERTY_DESC, property.getDescription());
        values.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, property.getFurnishLevel());
        values.put(PropertyCtrl.KEY_PROPERTY_PRICE, property.getPrice());
        values.put(PropertyCtrl.KEY_PROPERTY_POSTALCODE, property.getPostalcode());
        values.put(PropertyCtrl.KEY_PROPERTY_UNIT, property.getUnit());
        values.put(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME, property.getAddressName());
        values.put(PropertyCtrl.KEY_PROPERTY_PHOTO, property.getPhoto());
        values.put(PropertyCtrl.KEY_PROPERTY_STATUS, property.getStatus());
        values.put(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS, property.getNoOfbedrooms());
        values.put(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS, property.getNoOfbathrooms());
        if (property instanceof Sale) {
            Log.d(TAG, "Adding instanceof Sale.getFloorArea: " + ((Sale) property).getFloorArea());
            // down casting
            values.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, ((Sale) property).getFloorArea());
        }
        if (property instanceof Lease) {
            Log.d(TAG, "Adding instanceof Lease.getWholeApartment:" + ((Lease) property).getWholeApartment());
            // down casting
            values.put(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT, ((Lease) property).getWholeApartment());
        }
        values.put(PropertyCtrl.KEY_PROPERTY_CREATEDDATE, property.getCreatedate());

        // inserting a new row
        long id = db.insert(PropertyCtrl.TABLE_PROPERTY, null, values);
        // closing database connection
        db.close();

        Log.d(TAG, "New property inserted into sqlite: " + id);
    }

    /**
     * get user property from local db
     */
    public HashMap<String, String> getUserProperty(String propertyID) {
        Log.i(TAG, "getUserProperty()");
        HashMap<String, String> userPropertyHashMap = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + PropertyCtrl.TABLE_PROPERTY + " WHERE PROPERTYID = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{propertyID});
        cursor.getColumnCount();
        Log.i(TAG, "getUserProperty count: " + cursor.getCount());
        // Move to first row
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Fetching property from sqlite: " + cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PROPERTYID)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PROPERTYID)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_OWNERID, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_OWNERID)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_FLATTYPE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLATTYPE)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DEALTYPE)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_TITLE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_TITLE)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_DESC, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DESC)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_PRICE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PRICE)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_POSTALCODE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_POSTALCODE)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_UNIT, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_UNIT)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_PHOTO, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PHOTO)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_STATUS, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_STATUS)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLOORAREA)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT)));
            userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_CREATEDDATE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_CREATEDDATE)));

        } else {
            Log.d(TAG, "No property data to fetch from Sqlite.");
        }
        cursor.close();
        db.close();
        return userPropertyHashMap;
    }

    /**
     * get user properties from local db
     */
    public ArrayList<Property> getUserProperties(User owner) {
        Log.i(TAG, "getUserProperties()");
        ArrayList<Property> userPropertyList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + PropertyCtrl.TABLE_PROPERTY + " WHERE OWNERID = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{owner.getUserID()});
        // Move to first row
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Fetching user properties from sqlite. Total count: " + cursor.getCount());
            Lease lease;
            Sale sale;
            do {
                if (cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DEALTYPE)).toLowerCase().contains(DealType.ForLease.toString().toLowerCase())) {
                    lease = new Lease(
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PROPERTYID)),
                            owner,
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLATTYPE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DEALTYPE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DESC)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PRICE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_POSTALCODE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_UNIT)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PHOTO)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_STATUS)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_CREATEDDATE)));
                    userPropertyList.add(lease);
                }
                if (cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DEALTYPE)).toLowerCase().contains(DealType.ForSale.toString().toLowerCase())) {
                    sale = new Sale(
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PROPERTYID)),
                            owner,
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLATTYPE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DEALTYPE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DESC)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PRICE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_POSTALCODE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_UNIT)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PHOTO)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_STATUS)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLOORAREA)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_CREATEDDATE)));
                    userPropertyList.add(sale);
                }
            } while (cursor.moveToNext());

        } else {
            Log.d(TAG, "No user properties to fetch from Sqlite.");
        }
        cursor.close();
        db.close();
        return userPropertyList;
    }

    /**
     * update user property to local db
     */
    public void updateUserProperty(Property property) {
        Log.i(TAG, "updateUserProperty()");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, property.getPropertyID());
        values.put(PropertyCtrl.KEY_PROPERTY_OWNERID, property.getOwner().getUserID());
        values.put(PropertyCtrl.KEY_PROPERTY_FLATTYPE, property.getFlatType());
        values.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, property.getDealType());
        values.put(PropertyCtrl.KEY_PROPERTY_TITLE, property.getTitle());
        values.put(PropertyCtrl.KEY_PROPERTY_DESC, property.getDescription());
        values.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, property.getFurnishLevel());
        values.put(PropertyCtrl.KEY_PROPERTY_PRICE, property.getPrice());
        values.put(PropertyCtrl.KEY_PROPERTY_POSTALCODE, property.getPostalcode());
        values.put(PropertyCtrl.KEY_PROPERTY_UNIT, property.getUnit());
        values.put(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME, property.getAddressName());
        values.put(PropertyCtrl.KEY_PROPERTY_PHOTO, property.getPhoto());
        values.put(PropertyCtrl.KEY_PROPERTY_STATUS, property.getStatus());
        values.put(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS, property.getNoOfbedrooms());
        values.put(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS, property.getNoOfbathrooms());

        if (property instanceof Sale) {
            Log.d(TAG, "Updating instanceof Sale.getFloorArea: " + ((Sale) property).getFloorArea());
            // down casting
            values.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, ((Sale) property).getFloorArea());
        }
        if (property instanceof Lease) {
            Log.d(TAG, "Updating instanceof Lease.getWholeApartment:" + ((Lease) property).getWholeApartment());
            // down casting
            values.put(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT, ((Lease) property).getWholeApartment());
        }
        values.put(PropertyCtrl.KEY_PROPERTY_CREATEDDATE, property.getCreatedate());

        // inserting a new row
        long id = db.update(PropertyCtrl.TABLE_PROPERTY, values, "PROPERTYID = ?", new String[]{property.getPropertyID()});
        // closing database connection
        db.close();

        if (id != 0)
            Log.d(TAG, "Property updated into sqlite.");
        else
            Log.d(TAG, "Property did not update into sqlite.");
    }


    /**
     * Re create database
     * Delete all tables and create them again
     */
    public void deleteProperty() {
        // TODO: update to server before deleting any user information.
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(PropertyCtrl.TABLE_PROPERTY, null, null);
        db.close();
        Log.d(TAG, "Deleted all user property info from sqlite");
    }

}