package handler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import controllers.FavouriteCtrl;
import controllers.InboxCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Inbox;
import entities.Property;
import entities.User;

import static controllers.FavouriteCtrl.KEY_FAVOURITEID;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_CREATEDDATE;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_OWNERID;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_PROPERTYID;
import static controllers.FavouriteCtrl.TABLE_FAVOURITE;
import static controllers.InboxCtrl.KEY_INBOXID;
import static controllers.InboxCtrl.KEY_INBOXMESSAGE;
import static controllers.InboxCtrl.KEY_INBOXTITLE;
import static controllers.InboxCtrl.KEY_INBOXTYPE;
import static controllers.InboxCtrl.KEY_RECIPIENTID;
import static controllers.InboxCtrl.KEY_SENDERID;

/**
 * Created by Paul K Szean on 19/10/2016.
 */

public class JSONHandler {
    private static final String TAG = JSONHandler.class.getSimpleName();


    // JSON DATA
    public static String JSON_SUCCESS = "success";
    public static String JSON_RESULT = "result";
    public static String JSON_RECORDS = "records";

    static JSONObject jsonResponseObject;
    static JSONObject jsonResultObject;
    static JSONObject jsonRecordObject;
    static JSONArray jsonRecordArray;


    public static String getResultAsString(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            String message = jsonResponseObject.getString(JSON_RESULT);
            return message;

        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }
    }


    public static JSONObject getResultAsObject(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            if (success) {
                jsonResultObject = jsonResponseObject.getJSONObject(JSON_RESULT);
                return jsonResultObject;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }

    }

    public static JSONArray getResultAsArray(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            if (success) {
                jsonRecordArray = jsonResponseObject.getJSONArray(JSON_RESULT);
                return jsonRecordArray;
            } else {

                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }

    }

    public static JSONObject getRecordsAsObject(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            if (success) {
                jsonResultObject = jsonResponseObject.getJSONObject(JSON_RESULT);
                jsonRecordObject = jsonResultObject.getJSONObject(JSON_RECORDS);
                return jsonRecordObject;
            } else {
                return jsonResultObject;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }
    }

    public static JSONArray getRecordsAsArray(final Activity activity, String response) {
        try {
            jsonResponseObject = new JSONObject(response);
            boolean success = jsonResponseObject.getBoolean(JSON_SUCCESS);
            if (success) {
                jsonResultObject = jsonResponseObject.getJSONObject(JSON_RESULT);
                jsonRecordArray = jsonResultObject.getJSONArray(JSON_RECORDS);
                return jsonRecordArray;
            } else {
                Toast.makeText(activity, jsonResultObject.getString(JSON_RESULT), Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorHandler.errorHandler(activity, e);
            return null;
        }

    }


    /**
     * Created by Paul K Szean on 24/9/2016.
     * Whenever user tries to log  in, fetch from SQLite instead of making request to server.
     */

    public static class SQLiteHandler extends SQLiteOpenHelper {
        // LogCat tag
        private static final String TAG = SQLiteHandler.class.getSimpleName();

        // local database name
        public static final String DATABASE_NAME = "Estate";

        // Database Version
        private static final int DATABASE_VERSION = 8;

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
                + PropertyCtrl.KEY_PROPERTY_BLOCK + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_STREETNAME + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_FLOORLEVEL + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_FLOORAREA + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_PRICE + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_IMAGE + " BLOB, "
                + PropertyCtrl.KEY_PROPERTY_STATUS + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_DEALTYPE + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_TITLE + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_DESC + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT + " TEXT,"
                + PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT + " TEXT,"
                + PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT + " TEXT, "
                + PropertyCtrl.KEY_PROPERTY_CREATEDDATE + " TEXT "
                + ");";

        // user data table
        private static final String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + FavouriteCtrl.TABLE_FAVOURITE + "("
                + KEY_FAVOURITEID + " INTEGER,"
                + FavouriteCtrl.KEY_FAVOURITE_OWNERID + " TEXT, "
                + FavouriteCtrl.KEY_FAVOURITE_PROPERTYID + " TEXT, "
                + FavouriteCtrl.KEY_FAVOURITE_CREATEDDATE + " TEXT "
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
            db.execSQL(CREATE_FAVOURITE_TABLE);
            Log.d(TAG, "Database table created.");
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "onUpgrade() - old version: " + oldVersion + ", new version: " + newVersion);
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + UserCtrl.TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + PropertyCtrl.TABLE_PROPERTY);
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

        /**
         * re create database
         * delete tables and create them again
         */
        public void deleteUserTable() {
            // TODO: update to server before deleting any user information.
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete(UserCtrl.TABLE_USER, null, null);
            db.close();
            Log.d(TAG, "Deleted all user info from sqlite");
        }


        /****************************************************************************************************************************************
         * property section
         **************************************************************************************************************************************/

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
            values.put(PropertyCtrl.KEY_PROPERTY_BLOCK, property.getBlock());
            values.put(PropertyCtrl.KEY_PROPERTY_STREETNAME, property.getStreetname());
            values.put(PropertyCtrl.KEY_PROPERTY_FLOORLEVEL, property.getFloorlevel());
            values.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, property.getFloorarea());
            values.put(PropertyCtrl.KEY_PROPERTY_PRICE, property.getPrice());
            values.put(PropertyCtrl.KEY_PROPERTY_IMAGE, property.getImage());
            values.put(PropertyCtrl.KEY_PROPERTY_STATUS, property.getStatus());
            values.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, property.getDealType());
            values.put(PropertyCtrl.KEY_PROPERTY_TITLE, property.getTitle());
            values.put(PropertyCtrl.KEY_PROPERTY_DESC, property.getDescription());
            values.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, property.getFurnishLevel());
            values.put(PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT, property.getBedroomcount());
            values.put(PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT, property.getBathroomcount());
            values.put(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT, property.getWholeapartment());
            values.put(PropertyCtrl.KEY_PROPERTY_CREATEDDATE, property.getCreateddate());

            Log.i(TAG, values.toString());

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
                cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_OWNERID, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_OWNERID)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_FLATTYPE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLATTYPE)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_BLOCK, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_BLOCK)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_STREETNAME, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_STREETNAME)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_FLOORLEVEL, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLOORLEVEL)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLOORAREA)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_PRICE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PRICE)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_IMAGE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_IMAGE)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_STATUS, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_STATUS)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DEALTYPE)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_TITLE, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_TITLE)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_DESC, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DESC)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT)));
                userPropertyHashMap.put(PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT, cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT)));
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

                do {
                    Property property = new Property(
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PROPERTYID)),
                            owner,
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLATTYPE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_BLOCK)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_STREETNAME)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLOORLEVEL)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FLOORAREA)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_PRICE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_STATUS)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DEALTYPE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_DESC)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT)),
                            cursor.getString(cursor.getColumnIndex(PropertyCtrl.KEY_PROPERTY_CREATEDDATE)));
                    userPropertyList.add(property);

                } while (cursor.moveToNext());

            } else

            {
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
            values.put(PropertyCtrl.KEY_PROPERTY_BLOCK, property.getBlock());
            values.put(PropertyCtrl.KEY_PROPERTY_STREETNAME, property.getStreetname());
            values.put(PropertyCtrl.KEY_PROPERTY_FLOORLEVEL, property.getFloorlevel());
            values.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, property.getFloorarea());
            values.put(PropertyCtrl.KEY_PROPERTY_PRICE, property.getPrice());
            values.put(PropertyCtrl.KEY_PROPERTY_IMAGE, property.getImage());
            values.put(PropertyCtrl.KEY_PROPERTY_STATUS, property.getStatus());
            values.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, property.getDealType());
            values.put(PropertyCtrl.KEY_PROPERTY_TITLE, property.getTitle());
            values.put(PropertyCtrl.KEY_PROPERTY_DESC, property.getDescription());
            values.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, property.getFurnishLevel());
            values.put(PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT, property.getBedroomcount());
            values.put(PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT, property.getBathroomcount());
            values.put(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT, property.getWholeapartment());
            values.put(PropertyCtrl.KEY_PROPERTY_CREATEDDATE, property.getCreateddate());

            // updating a existing row
            long id = db.update(PropertyCtrl.TABLE_PROPERTY, values, "PROPERTYID = ?", new String[]{property.getPropertyID()});
            // closing database connection
            db.close();

            if (id != 0)
                Log.d(TAG, "Property updated into sqlite.");
            else
                Log.d(TAG, "Property did not update into sqlite.");
        }

        /**
         * re create database
         * delete tables and create them again
         */
        public void deletePropertyTable() {
            // TODO: update to server before deleting any user information.
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete(PropertyCtrl.TABLE_PROPERTY, null, null);
            db.close();
            Log.d(TAG, "Deleted all user property info from sqlite");
        }


        /****************************************************************************************************************************************
         * favourite section
         **************************************************************************************************************************************/

        /**
         * store favourite property details in database
         */
        public void addFavouriteProperty(Favourite favourite) {
            Log.i(TAG, "addFavouriteProperty()");
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_FAVOURITEID, favourite.getFavouriteID());
            values.put(KEY_FAVOURITE_OWNERID, favourite.getOwnerID());
            values.put(KEY_FAVOURITE_PROPERTYID, favourite.getPropertyID());
            values.put(KEY_FAVOURITE_CREATEDDATE, favourite.getCreateddate());

            Log.i(TAG, values.toString());

            // inserting a new row
            long id = db.insert(TABLE_FAVOURITE, null, values);
            // closing database connection
            db.close();

            Log.d(TAG, "New favourite property inserted into sqlite: " + id);
        }

        /**
         * get user favourite property from local db
         */
        public HashMap<String, String> getUserFavouriteProperty(String propertyID) {
            Log.i(TAG, "getUserFavouriteProperty()");
            HashMap<String, String> favoruiteHashMap = new HashMap<>();
            String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITE + " WHERE propertyID = ?";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, new String[]{propertyID});
            cursor.getColumnCount();
            Log.i(TAG, "getUserFavouriteProperty count: " + cursor.getCount());
            // Move to first row
            if (cursor.moveToFirst()) {
                Log.d(TAG, "Fetching favourite property from sqlite: " + cursor.getString(cursor.getColumnIndex(KEY_FAVOURITEID)));
                favoruiteHashMap.put(KEY_FAVOURITEID, cursor.getString(cursor.getColumnIndex(KEY_FAVOURITEID)));
                favoruiteHashMap.put(KEY_FAVOURITE_OWNERID, cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_OWNERID)));
                favoruiteHashMap.put(KEY_FAVOURITE_PROPERTYID, cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_PROPERTYID)));
                favoruiteHashMap.put(KEY_FAVOURITE_CREATEDDATE, cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_CREATEDDATE)));

            } else {
                Log.d(TAG, "No favourite property data to fetch from Sqlite.");
            }
            cursor.close();
            db.close();
            return favoruiteHashMap;
        }

        /**
         * get user favourite properties from local db
         */
        public ArrayList<Favourite> getUserFavouriteProperties(User owner) {
            Log.i(TAG, "getUserProperties()");
            ArrayList<Favourite> userFavouritePropertyList = new ArrayList<>();
            String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITE + " WHERE OWNERID = ?";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, new String[]{owner.getUserID()});
            // Move to first row
            if (cursor.moveToFirst()) {
                Log.d(TAG, "Fetching user favourite properties from sqlite. Total count: " + cursor.getCount());
                do {

                    Favourite favourite = new Favourite(
                            cursor.getString(cursor.getColumnIndex(KEY_FAVOURITEID)),
                            cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_OWNERID)),
                            cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_PROPERTYID)),
                            cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE_CREATEDDATE)));
                    userFavouritePropertyList.add(favourite);
                } while (cursor.moveToNext());

            } else {
                Log.d(TAG, "No user favourite properties to fetch from Sqlite.");
            }
            cursor.close();
            db.close();
            return userFavouritePropertyList;
        }

        /**
         * delete user favourited property
         */
        public void deleteFavouriteProperty(Favourite favourite) {
            // TODO: update to server before deleting any local data.
            SQLiteDatabase db = this.getWritableDatabase();

            // Delete All Rows
            db.delete(TABLE_FAVOURITE, "FAVOURITEID = ?", new String[]{favourite.getFavouriteID()});
            db.close();
            Log.d(TAG, "Deleted all user favourite property info from sqlite");
        }


        /**
         * re create database
         * delete tables and create them again
         */
        public void deleteFavouritePropertyTable() {
            // TODO: update to server before deleting any local data.
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete(TABLE_FAVOURITE, null, null);
            db.close();
            Log.d(TAG, "Deleted all user favourite property info from sqlite");
        }


        /****************************************************************************************************************************************
         * inbox section
         **************************************************************************************************************************************/
        /**
         * store favourite property details in database
         */
        public void addInbox(Inbox inbox) {
            Log.i(TAG, "addInbox()");
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_INBOXID, inbox.getInboxID());
            values.put(KEY_SENDERID, inbox.getSenderID());
            values.put(KEY_RECIPIENTID, inbox.getSenderID());
            values.put(KEY_INBOXTYPE, inbox.getInboxtype());
            values.put(KEY_INBOXTITLE, inbox.getInboxtitle());
            values.put(KEY_INBOXMESSAGE, inbox.getInboxmessage());
            values.put(InboxCtrl.KEY_CREATEDDATE, inbox.getCreateddate());

            Log.i(TAG, values.toString());

            // inserting a new row
            long id = db.insert(TABLE_FAVOURITE, null, values);
            // closing database connection
            db.close();

            Log.d(TAG, "New favourite property inserted into sqlite: " + id);
        }


    }
}
