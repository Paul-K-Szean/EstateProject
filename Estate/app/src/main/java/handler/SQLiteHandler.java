package handler;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import controllers.FavouriteCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Property;
import entities.User;

import static controllers.FavouriteCtrl.KEY_FAVOURITEID;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_CREATEDDATE;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_OWNERID;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_PROPERTYID;
import static controllers.FavouriteCtrl.TABLE_FAVOURITE;
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
import static controllers.PropertyCtrl.KEY_PROPERTY_OWNERID;
import static controllers.PropertyCtrl.KEY_PROPERTY_PRICE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;
import static controllers.PropertyCtrl.KEY_PROPERTY_STATUS;
import static controllers.PropertyCtrl.KEY_PROPERTY_STREETNAME;
import static controllers.PropertyCtrl.KEY_PROPERTY_TITLE;
import static controllers.PropertyCtrl.KEY_PROPERTY_VIEWCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT;
import static controllers.PropertyCtrl.TABLE_PROPERTY;

/**
 * Created by Paul K Szean on 24/9/2016.
 * Whenever user tries to log  in, fetch from SQLite instead of making request to server.
 */

public class SQLiteHandler extends SQLiteOpenHelper {
    // local database name
    public static final String DATABASE_NAME = "Estate";
    // LogCat tag
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 10;

    // user data table
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + UserCtrl.TABLE_USER + "("
            + UserCtrl.KEY_USERID + " INTEGER,"
            + UserCtrl.KEY_NAME + " TEXT, "
            + UserCtrl.KEY_EMAIL + " TEXT, "
            + UserCtrl.KEY_PASSWORD + " TEXT, "
            + UserCtrl.KEY_CONTACT + " TEXT "
            + ");";

    // property data table
    private static final String CREATE_PROPERTY_TABLE = "CREATE TABLE " + TABLE_PROPERTY + "("
            + KEY_PROPERTY_PROPERTYID + " INTEGER, "
            + KEY_PROPERTY_OWNERID + " INTEGER, "
            + KEY_PROPERTY_FLATTYPE + " TEXT, "
            + KEY_PROPERTY_BLOCK + " TEXT, "
            + KEY_PROPERTY_STREETNAME + " TEXT, "
            + KEY_PROPERTY_FLOORLEVEL + " TEXT, "
            + KEY_PROPERTY_FLOORAREA + " TEXT, "
            + KEY_PROPERTY_PRICE + " TEXT, "
            + KEY_PROPERTY_IMAGE + " BLOB, "
            + KEY_PROPERTY_STATUS + " TEXT, "
            + KEY_PROPERTY_DEALTYPE + " TEXT, "
            + KEY_PROPERTY_TITLE + " TEXT, "
            + KEY_PROPERTY_DESC + " TEXT, "
            + KEY_PROPERTY_FURNISHLEVEL + " TEXT, "
            + KEY_PROPERTY_BEDROOMCOUNT + " TEXT,"
            + KEY_PROPERTY_BATHROOMCOUNT + " TEXT,"
            + KEY_PROPERTY_FAVOURITECOUNT + " TEXT,"
            + KEY_PROPERTY_VIEWCOUNT + " TEXT,"
            + KEY_PROPERTY_WHOLEAPARTMENT + " TEXT, "
            + KEY_PROPERTY_CREATEDDATE + " TEXT "
            + ");";

    // user data table
    private static final String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + FavouriteCtrl.TABLE_FAVOURITE + "("
            + KEY_FAVOURITEID + " INTEGER,"
            + FavouriteCtrl.KEY_FAVOURITE_OWNERID + " TEXT, "
            + FavouriteCtrl.KEY_FAVOURITE_PROPERTYID + " TEXT, "
            + FavouriteCtrl.KEY_FAVOURITE_CREATEDDATE + " TEXT "
            + ");";

    private Cursor cursor;
    private SQLiteDatabase db;
    private ContentValues values;
    private String selectQuery;

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
        db.execSQL(CREATE_FAVOURITE_TABLE);

        Log.d(TAG, "Database table created.");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade() - old version: " + oldVersion + ", new version: " + newVersion);
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + UserCtrl.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTY);
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteCtrl.TABLE_FAVOURITE);
        // Create tables again
        onCreate(db);

    }


    /****************************************************************************************************************************************
     * user section
     **************************************************************************************************************************************/

    /**
     * Storing user details in database
     */
    public void addUser(User user) {
        Log.i(TAG, "addUser()");

        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(UserCtrl.KEY_USERID, user.getUserID());
        values.put(UserCtrl.KEY_NAME, user.getName());
        values.put(UserCtrl.KEY_EMAIL, user.getEmail());
        values.put(UserCtrl.KEY_PASSWORD, user.getPassword());
        values.put(UserCtrl.KEY_CONTACT, user.getContact());

        // inserting a new row
        long id = db.insert(UserCtrl.TABLE_USER, null, values);
        Log.d(TAG, "New user inserted into sqlite: " + id);
        db.close();

    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        Log.d(TAG, "getUserDetails");

        HashMap<String, String> userHashMap = new HashMap<>();
        selectQuery = "SELECT  * FROM " + UserCtrl.TABLE_USER;
        db = this.getReadableDatabase();
        cursor = db.rawQuery(selectQuery, null);

        // Move to first row
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Fetching user from sqlite: " + cursor.getString(cursor.getColumnIndex(UserCtrl.KEY_USERID)) + ", " +
                    cursor.getString(cursor.getColumnIndex(UserCtrl.KEY_EMAIL)));
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
     * Update user details in database
     */
    public void updateUser(User user) {
        Log.i(TAG, "updateUser");

        // retain user information here
        db = this.getReadableDatabase();

        // new value for user
        ContentValues values = new ContentValues();
        // values.put(KEY_USERID, user.getNotifyUserId());
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

        if (count != 0)
            Log.d(TAG, "User information retained.");
        else
            Log.e(TAG, "Error retaining user information.");

        db.close();
    }

    /**
     * re create database
     * delete tables and create them again
     */
    public void deleteUserTable() {
        Log.i(TAG, "deleteUserTable");

        db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(UserCtrl.TABLE_USER, null, null);
        Log.d(TAG, "Deleted all user info from sqlite");
        db.close();


    }


    /****************************************************************************************************************************************
     * property section
     **************************************************************************************************************************************/

    /**
     * store property details in database
     */
    public void addProperty(Property property) {
        Log.i(TAG, "addProperty()");

        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(KEY_PROPERTY_PROPERTYID, property.getPropertyID());
        values.put(KEY_PROPERTY_OWNERID, property.getOwner().getUserID());
        values.put(KEY_PROPERTY_FLATTYPE, property.getFlatType());
        values.put(KEY_PROPERTY_BLOCK, property.getBlock());
        values.put(KEY_PROPERTY_STREETNAME, property.getStreetname());
        values.put(KEY_PROPERTY_FLOORLEVEL, property.getFloorlevel());
        values.put(KEY_PROPERTY_FLOORAREA, property.getFloorarea());
        values.put(KEY_PROPERTY_PRICE, property.getPrice());
        values.put(KEY_PROPERTY_IMAGE, property.getImage());
        values.put(KEY_PROPERTY_STATUS, property.getStatus());
        values.put(KEY_PROPERTY_DEALTYPE, property.getDealType());
        values.put(KEY_PROPERTY_TITLE, property.getTitle());
        values.put(KEY_PROPERTY_DESC, property.getDescription());
        values.put(KEY_PROPERTY_FURNISHLEVEL, property.getFurnishLevel());
        values.put(KEY_PROPERTY_BEDROOMCOUNT, property.getBedroomcount());
        values.put(KEY_PROPERTY_BATHROOMCOUNT, property.getBathroomcount());
        values.put(KEY_PROPERTY_FAVOURITECOUNT, property.getFavouritecount());
        values.put(KEY_PROPERTY_VIEWCOUNT, property.getViewcount());
        values.put(KEY_PROPERTY_WHOLEAPARTMENT, property.getWholeapartment());
        values.put(KEY_PROPERTY_CREATEDDATE, property.getCreateddate());
        //   Log.i(TAG, values.toString());

        // inserting a new row
        long id = db.insert(TABLE_PROPERTY, null, values);
        Log.d(TAG, "New property inserted into sqlite: " + id);
        db.close();


    }

    /**
     * get user property from local db
     */
    public HashMap<String, String> getUserProperty(String propertyID) {
        Log.i(TAG, "getUserProperty()");

        HashMap<String, String> userPropertyHashMap = new HashMap<>();
        selectQuery = "SELECT  * FROM " + TABLE_PROPERTY + " WHERE PROPERTYID = ?";
        db = this.getReadableDatabase();
        cursor = db.rawQuery(selectQuery, new String[]{propertyID});
        cursor.getColumnCount();
        Log.i(TAG, "getUserProperty count: " + cursor.getCount());
        // Move to first row
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Fetching property from sqlite: " + cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_PROPERTYID)));
            userPropertyHashMap.put(KEY_PROPERTY_PROPERTYID, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_PROPERTYID)));
            userPropertyHashMap.put(KEY_PROPERTY_OWNERID, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_OWNERID)));
            userPropertyHashMap.put(KEY_PROPERTY_FLATTYPE, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FLATTYPE)));
            userPropertyHashMap.put(KEY_PROPERTY_BLOCK, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_BLOCK)));
            userPropertyHashMap.put(KEY_PROPERTY_STREETNAME, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_STREETNAME)));
            userPropertyHashMap.put(KEY_PROPERTY_FLOORLEVEL, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FLOORLEVEL)));
            userPropertyHashMap.put(KEY_PROPERTY_FLOORAREA, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FLOORAREA)));
            userPropertyHashMap.put(KEY_PROPERTY_PRICE, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_PRICE)));
            userPropertyHashMap.put(KEY_PROPERTY_IMAGE, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_IMAGE)));
            userPropertyHashMap.put(KEY_PROPERTY_STATUS, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_STATUS)));
            userPropertyHashMap.put(KEY_PROPERTY_DEALTYPE, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_DEALTYPE)));
            userPropertyHashMap.put(KEY_PROPERTY_TITLE, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_TITLE)));
            userPropertyHashMap.put(KEY_PROPERTY_DESC, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_DESC)));
            userPropertyHashMap.put(KEY_PROPERTY_FURNISHLEVEL, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FURNISHLEVEL)));
            userPropertyHashMap.put(KEY_PROPERTY_BEDROOMCOUNT, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_BEDROOMCOUNT)));
            userPropertyHashMap.put(KEY_PROPERTY_BATHROOMCOUNT, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_BATHROOMCOUNT)));
            userPropertyHashMap.put(KEY_PROPERTY_FAVOURITECOUNT, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FAVOURITECOUNT)));
            userPropertyHashMap.put(KEY_PROPERTY_VIEWCOUNT, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_VIEWCOUNT)));
            userPropertyHashMap.put(KEY_PROPERTY_WHOLEAPARTMENT, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_WHOLEAPARTMENT)));
            userPropertyHashMap.put(KEY_PROPERTY_CREATEDDATE, cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_CREATEDDATE)));

        } else {
            Log.d(TAG, "No property data to fetch from Sqlite.");
        }
        cursor.close();
        // closing database connection
        db.close();
        // return userHashMap
        return userPropertyHashMap;


    }

    /**
     * get user properties from local db
     */
    public ArrayList<Property> getUserProperties(User owner) {
        Log.i(TAG, "getUserProperties()");

        ArrayList<Property> userPropertyList = new ArrayList<>();
        selectQuery = "SELECT  * FROM " + TABLE_PROPERTY + " WHERE OWNERID = ?";
        db = this.getReadableDatabase();
        cursor = db.rawQuery(selectQuery, new String[]{owner.getUserID()});

        // Move to first row
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Fetching user properties from sqlite. Total count: " + cursor.getCount());
            do {
                Property property = new Property(
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_PROPERTYID)),
                        owner,
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FLATTYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_BLOCK)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_STREETNAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FLOORLEVEL)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FLOORAREA)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_PRICE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_STATUS)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_DEALTYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_DESC)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FURNISHLEVEL)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_BEDROOMCOUNT)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_BATHROOMCOUNT)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_FAVOURITECOUNT)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_VIEWCOUNT)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_WHOLEAPARTMENT)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROPERTY_CREATEDDATE)));
                userPropertyList.add(property);
                Log.i(TAG, property.getBlock() + " " + property.getStreetname());
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No user properties to fetch from Sqlite.");
        }

        cursor.close();
        // closing database connection
        db.close();
        return userPropertyList;

    }

    /**
     * update user property to local db
     */
    public void updateUserProperty(Property property) {
        Log.i(TAG, "updateUserProperty()");

        db = this.getWritableDatabase();
        values = new ContentValues();
        values.put(KEY_PROPERTY_PROPERTYID, property.getPropertyID());
        values.put(KEY_PROPERTY_OWNERID, property.getOwner().getUserID());
        values.put(KEY_PROPERTY_FLATTYPE, property.getFlatType());
        values.put(KEY_PROPERTY_BLOCK, property.getBlock());
        values.put(KEY_PROPERTY_STREETNAME, property.getStreetname());
        values.put(KEY_PROPERTY_FLOORLEVEL, property.getFloorlevel());
        values.put(KEY_PROPERTY_FLOORAREA, property.getFloorarea());
        values.put(KEY_PROPERTY_PRICE, property.getPrice());
        values.put(KEY_PROPERTY_IMAGE, property.getImage());
        values.put(KEY_PROPERTY_STATUS, property.getStatus());
        values.put(KEY_PROPERTY_DEALTYPE, property.getDealType());
        values.put(KEY_PROPERTY_TITLE, property.getTitle());
        values.put(KEY_PROPERTY_DESC, property.getDescription());
        values.put(KEY_PROPERTY_FURNISHLEVEL, property.getFurnishLevel());
        values.put(KEY_PROPERTY_BEDROOMCOUNT, property.getBedroomcount());
        values.put(KEY_PROPERTY_BATHROOMCOUNT, property.getBathroomcount());
        values.put(KEY_PROPERTY_WHOLEAPARTMENT, property.getWholeapartment());
        values.put(KEY_PROPERTY_CREATEDDATE, property.getCreateddate());

        // updating a existing row
        long id = db.update(TABLE_PROPERTY, values, "PROPERTYID = ?", new String[]{property.getPropertyID()});

        if (id != 0)
            Log.d(TAG, "Property updated into sqlite.");
        else
            Log.d(TAG, "Property did not update into sqlite.");
        db.close();

    }

    /**
     * get user properties count from local db
     */
    public int getUserPropertyCount() {
        Log.i(TAG, "getUserPropertyCount()");

        selectQuery = "SELECT * FROM " + TABLE_PROPERTY;
        db = this.getReadableDatabase();
        cursor = db.rawQuery(selectQuery, null);
        int count = 0;
        cursor.moveToFirst();
        count = cursor.getCount();

        Log.i(TAG, "Fetching user properties count from sqlite. Count = " + count);
        cursor.close();
        // closing database connection
        db.close();
        return count;

    }


    /**
     * re create database
     * delete tables and create them again
     */
    public void deletePropertyTable() {

            db = this.getWritableDatabase();
            // Delete All Rows
            db.delete(TABLE_PROPERTY, null, null);
            Log.d(TAG, "Deleted all user property info from sqlite");
            db.close();

    }


    /****************************************************************************************************************************************
     * favourite section
     **************************************************************************************************************************************/

    /**
     * store favourite property details in database
     */
    public void addFavouriteProperty(Favourite favourite) {
        Log.i(TAG, "addFavouriteProperty()");
            db = this.getWritableDatabase();
            values = new ContentValues();
            values.put(KEY_FAVOURITEID, favourite.getFavouriteID());
            values.put(KEY_FAVOURITE_OWNERID, favourite.getOwner().getUserID());
            values.put(KEY_FAVOURITE_PROPERTYID, favourite.getProperty().getPropertyID());
            values.put(KEY_FAVOURITE_CREATEDDATE, favourite.getCreateddate());

            // inserting a new row
            long id = db.insert(TABLE_FAVOURITE, null, values);
            Log.d(TAG, "New favourite property inserted into sqlite: " + id);
            // closing database connection
            db.close();

    }

    /**
     * get user favourite property from local db
     */
    public HashMap<String, String> getUserFavouriteProperty(String ownerID, String propertyID) {
        Log.i(TAG, "getUserFavouriteProperty()");

        HashMap<String, String> favoruiteHashMap = null;
        selectQuery = "SELECT  * FROM " + TABLE_FAVOURITE + " WHERE " + KEY_FAVOURITE_OWNERID + " = ? AND " + KEY_FAVOURITE_PROPERTYID + " = ?";
        db = this.getReadableDatabase();
        cursor = db.rawQuery(selectQuery, new String[]{ownerID, propertyID});
        // Move to first row
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Fetching favourite property from sqlite: " + cursor.getString(cursor.getColumnIndex(KEY_FAVOURITEID)) + ", ownerID: " +
                    cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_OWNERID)) + ", propertyID: " +
                    cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_PROPERTYID)));
            favoruiteHashMap = new HashMap<>();
            favoruiteHashMap.put(KEY_FAVOURITEID, cursor.getString(cursor.getColumnIndex(KEY_FAVOURITEID)));
            favoruiteHashMap.put(KEY_FAVOURITE_OWNERID, cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_OWNERID)));
            favoruiteHashMap.put(KEY_FAVOURITE_PROPERTYID, cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_PROPERTYID)));
            favoruiteHashMap.put(KEY_FAVOURITE_CREATEDDATE, cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_CREATEDDATE)));
        } else {
            Log.d(TAG, "No favourite property data to fetch from Sqlite.");
        }
        cursor.close();
        // closing database connection
        db.close();
        return favoruiteHashMap;

    }

    /**
     * get user favourite properties from local db
     */
    public ArrayList<Favourite> getUserFavouriteProperties(User user, Property property) {
        Log.i(TAG, "getUserProperties()");

        ArrayList<Favourite> userFavouritePropertyList = new ArrayList<>();
        selectQuery = "SELECT  * FROM " + TABLE_FAVOURITE + " WHERE OWNERID = ?";
        db = this.getReadableDatabase();
        cursor = db.rawQuery(selectQuery, new String[]{user.getUserID()});
        // Move to first row
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Fetching user favourite properties from sqlite. Total count: " + cursor.getCount());
            do {
                Favourite favourite = new Favourite(
                        cursor.getString(cursor.getColumnIndex(KEY_FAVOURITEID)),
                        user,
                        property,
                        cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_CREATEDDATE)));
                userFavouritePropertyList.add(favourite);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No user favourite properties to fetch from Sqlite.");
        }
        cursor.close();
        // closing database connection
        db.close();
        return userFavouritePropertyList;
}

    /**
     * get user favourite count from local db
     */
    public int getUserFavouriteCount() {
        Log.i(TAG, "getUserFavouriteCount()");

        db = this.getReadableDatabase();
        selectQuery = "SELECT * FROM " + TABLE_FAVOURITE;
        cursor = db.rawQuery(selectQuery, null);
        int count = 0;
        cursor.moveToFirst();
        count = cursor.getCount();
        Log.i(TAG, "Fetching user favourite count from sqlite. Count = " + count);
        cursor.close();
        // closing database connection
        db.close();
        return count;

    }

    /**
     * delete user favourited property
     */
    public void deleteFavouriteProperty(Favourite favourite) {

        db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_FAVOURITE, "FAVOURITEID = ?", new String[]{favourite.getFavouriteID()});
        Log.d(TAG, "Deleted user favourite property info from sqlite");
        // closing database connection
        db.close();
    }


    /**
     * re create database
     * delete tables and create them again
     */
    public void deleteFavouritePropertyTable() {

        db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_FAVOURITE, null, null);
        Log.d(TAG, "Deleted all user favourite property info from sqlite");
        // closing database connection
        db.close();
    }


}