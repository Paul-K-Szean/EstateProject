package controllers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Property;
import entities.User;
import enums.DealType;
import estateco.estate.FragmentUserListings;
import estateco.estate.R;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.FragmentHandler;
import handler.JSONHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.ViewAdapterRecyclerProperty;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static controllers.UserCtrl.KEY_CONTACT;
import static controllers.UserCtrl.KEY_EMAIL;
import static controllers.UserCtrl.KEY_NAME;
import static controllers.UserCtrl.KEY_USERID;

/**
 * Created by Paul K Szean on 26/9/2016.
 */

public class PropertyCtrl {
    // table name
    public static final String TABLE_PROPERTY = "estate_property";
    private static final String TAG = PropertyCtrl.class.getSimpleName();
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
    public static String KEY_PROPERTY_FAVOURITECOUNT = "favouritecount";
    public static String KEY_PROPERTY_VIEWCOUNT = "viewcount";
    public static String KEY_PROPERTY_WHOLEAPARTMENT = "wholeapartment";
    public static String KEY_PROPERTY_CREATEDDATE = "createddate";
    // extras
    public static String KEY_PROPERTY_SEARCH = "searchvalue";
    public static String KEY_PROPERTY_ROOM = "room";
    public static String KEY_ACTION_INCREASEFAVOURITE = "increasefavourite";
    public static String KEY_ACTION_DECREASEFAVOURITE = "decreasefavourite";
    public static String KEY_ACTION_INCREASEVIEW = "increaseview";
    public static String KEY_ACTION_DECREASEVIEW = "decreaseview";
    private SessionHandler session;
    private SQLiteHandler db;
    private ArrayList<Property> propertyArrayList;
    private Property property;
    private User owner;


    private RecyclerView recycler;
    private ViewAdapterRecyclerProperty viewAdapter;

    public PropertyCtrl(Context context) {
        // SQLite database handler
        db = new SQLiteHandler(context);
    }

    public PropertyCtrl(Context context, SessionHandler session) {
        // SQLite database handler
        db = new SQLiteHandler(context);
        this.session = session;
    }

    // **********************************************************************
    // ********************* LOCAL SQLITE DATABASE ACCESS *******************
    // **********************************************************************

    // add property details into local db
    public void addPropertyDetails(Property property) {
        db.addProperty(property);
    }

    // get user property details from local db
    public Property getUserPropertyDetails(String propertyID, User owner) {
        Log.i(TAG, "getUserPropertyDetails");
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
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_FAVOURITECOUNT),
                    savedProperty.get(PropertyCtrl.KEY_PROPERTY_VIEWCOUNT),
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

    // get user property count details from local db
    public int getUserPropertyCountDetails() {
        return db.getUserPropertyCount();
    }

    // remove all property from local db
    public void deletePropertyTable() {
        db.deletePropertyTable();
    }


    // **********************************************************************
    // ********************* REMOTE WAMP SERVER ACCESS **********************
    // **********************************************************************
    // create new property
    public void serverNewProperty(final Fragment fragment, final Property property, final User user) {
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
        paramValues.put(KEY_PROPERTY_FAVOURITECOUNT, property.getFavouritecount());
        paramValues.put(KEY_PROPERTY_VIEWCOUNT, property.getViewcount());
        paramValues.put(KEY_PROPERTY_WHOLEAPARTMENT, property.getWholeapartment());

        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_NEWPROPERTY, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {

                try {
                    JSONObject propertyObj = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                    if (propertyObj != null) {
                        // server side created property
                        Toast.makeText(fragment.getActivity(), "Property successfully created!", Toast.LENGTH_LONG).show();
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
                                propertyObj.getString(KEY_PROPERTY_FAVOURITECOUNT),
                                propertyObj.getString(KEY_PROPERTY_VIEWCOUNT),
                                propertyObj.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                                propertyObj.getString(KEY_PROPERTY_CREATEDDATE));
                        // save to local DB
                        EstateCtrl.syncUserPropertyToLocalDB(property);
                        fragment.getFragmentManager().popBackStack();
                        FragmentHandler.loadFragment(fragment, new FragmentUserListings());
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

    public void serverUpdateUserProperty(final Fragment fragment, final Property property) {
        Log.i(TAG, "serverUpdateUserProperty");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_PROPERTY_OWNERID, property.getOwner().getUserID());
        paramValues.put(KEY_PROPERTY_PROPERTYID, property.getPropertyID());
        paramValues.put(KEY_PROPERTY_FLATTYPE, property.getFlatType()); // flattype
        paramValues.put(KEY_PROPERTY_BLOCK, property.getBlock());    // block
        paramValues.put(KEY_PROPERTY_STREETNAME, property.getStreetname()); // streentname
        paramValues.put(KEY_PROPERTY_FLOORLEVEL, property.getFloorlevel());    // floorarea
        paramValues.put(KEY_PROPERTY_FLOORAREA, property.getFloorarea());    // floorarea
        paramValues.put(KEY_PROPERTY_PRICE, property.getPrice());    // price
        paramValues.put(KEY_PROPERTY_IMAGE, property.getImage());    // photo
        paramValues.put(KEY_PROPERTY_STATUS, property.getStatus()); // status
        paramValues.put(KEY_PROPERTY_DEALTYPE, property.getDealType());    // dealtype
        paramValues.put(KEY_PROPERTY_TITLE, property.getTitle()); // title
        paramValues.put(KEY_PROPERTY_DESC, property.getDescription());    // description
        paramValues.put(KEY_PROPERTY_FURNISHLEVEL, property.getFurnishLevel()); // furnishlevel
        paramValues.put(KEY_PROPERTY_BEDROOMCOUNT, property.getBedroomcount()); // noofbedrooms
        paramValues.put(KEY_PROPERTY_BATHROOMCOUNT, property.getBathroomcount()); // noofbathrooms
        paramValues.put(KEY_PROPERTY_WHOLEAPARTMENT, property.getWholeapartment());    // wholeapartment

//        Log.i(TAG, "Param size: " + String.valueOf(paramValues.size()));
//        for (Map.Entry<String, String> val : paramValues.entrySet()) {
//            Log.i(TAG, val.getKey() + " == " + val.getValue());
//        }
        // save to remote server
        if (paramValues != null) {
            new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_UPDATEUSERPROPERTY, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
                @Override
                public void onAsyncTaskResponse(String response) {
                    JSONObject jsonObject = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                    if (jsonObject != null) {
                        // save to local db
                        EstateCtrl.syncUserUpdatedPropertyToLocalDB(property);
                        fragment.getFragmentManager().popBackStack();
                        fragment.getFragmentManager().popBackStack();
                        FragmentHandler.loadFragment(fragment, new FragmentUserListings());
                    } else {
                        String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                        Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute();
        }
    }

    public void serverGetAllListing(final Fragment fragment, final String URL_ADDRESS, final TextView tvAllListingCount) {
        // get all property listing from server
        new AsyncTaskHandler(Request.Method.GET, URL_ADDRESS, null, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                getAllListings(fragment, response, tvAllListingCount);
            }
        }).execute();
    }

    private void getAllListings(Fragment fragment, String response, TextView tvAllListingCount) {
        try {
            propertyArrayList = new ArrayList<>();
            recycler = (RecyclerView) fragment.getView().findViewById(R.id.recycleView);
            viewAdapter = new ViewAdapterRecyclerProperty(fragment, propertyArrayList);

            JSONArray jsonArray = JSONHandler.getResultAsArray(fragment.getActivity(), response);
            if (jsonArray != null) {
                Log.i(TAG, "Results: " + jsonArray.length());
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    owner = new User(
                            jsonObject.getString(KEY_USERID),
                            jsonObject.getString(KEY_NAME),
                            jsonObject.getString(KEY_EMAIL),
                            jsonObject.getString(KEY_CONTACT));

                    property = new Property(
                            jsonObject.getString(KEY_PROPERTY_PROPERTYID),
                            owner,
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
                    propertyArrayList.add(property);


                    recycler.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
                    recycler.setVisibility(VISIBLE);
                    recycler.setAdapter(viewAdapter);


                    if (propertyArrayList.size() > 1)
                        tvAllListingCount.setText(propertyArrayList.size() + " records");
                    else
                        tvAllListingCount.setText(propertyArrayList.size() + " record");
                }


            } else {
                recycler.setVisibility(GONE);
                String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                tvAllListingCount.setText(result);
            }

        } catch (JSONException error) {

        }
    }


    //  OTHERS
    public String calculatePropertyPrice(String valNewDealType, String valNewWholeApartment, String valNewPrice) {
        Log.i(TAG, "calculatePropertyPrice");
        Log.i(TAG, "valNewPrice: " + valNewPrice);
        if (valNewDealType.equals(DealType.ForLease.toString()) && valNewWholeApartment.equals(KEY_PROPERTY_ROOM)) {
            String value = String.valueOf((Double.valueOf(valNewPrice)) / 1000);
            return value;
        }
        if (valNewWholeApartment.equals(KEY_PROPERTY_WHOLEAPARTMENT) && !valNewPrice.isEmpty()) {
            String value = String.valueOf((Double.valueOf(valNewPrice)) * 1000);
            Log.i(TAG, value);
            return value;
        }
        return "000";
    }

    public ViewAdapterRecyclerProperty getViewAdapter() {
        return viewAdapter;
    }
}

