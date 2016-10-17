package estateco.estate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controllers.EstateConfig;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Lease;
import entities.Property;
import entities.Sale;
import entities.User;
import enums.DealType;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.FragmentHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.ViewAdapterAllProperties;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllListings extends Fragment {
    private static final String TAG = FragmentAllListings.class.getSimpleName();

    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private User owner;
    private Sale sale;
    private Lease lease;


    //ArrayList for property info
    private ArrayList<Property> userProperties;
    private Map<String, String> paramValues;
    GridView gvAllListings;
    SearchView svSearch;
    TextView tvAllListingCount, itemDataID;
    Toolbar toolbar;

    public FragmentAllListings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_listings, container, false);

        // setup ctrl objects
        db = new SQLiteHandler(getActivity());
        userCtrl = new UserCtrl(getActivity());
        session = new SessionHandler(getActivity());

        setControls(view);


        // get all property listing from server
        paramValues = new HashMap<>();
        paramValues.put("searchVal", "");
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_SEARCHLISTINGS, paramValues, getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (error) {
                        String errorMsg = jObj.getString("error_msg");
                        tvAllListingCount.setText(errorMsg);
                        gvAllListings.setVisibility(View.INVISIBLE);
                    } else {
                        userProperties = new ArrayList<Property>();
                        JSONArray results = jObj.getJSONArray("result");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject propertyObj = results.getJSONObject(i);

                            owner = new User(
                                    propertyObj.getString(UserCtrl.KEY_USERID),
                                    propertyObj.getString(UserCtrl.KEY_NAME),
                                    propertyObj.getString(UserCtrl.KEY_EMAIL),
                                    propertyObj.getString(UserCtrl.KEY_CONTACT));

                            // sale type
                            if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).toString().equals(DealType.ForSale.toString())) {
                                sale = new Sale(
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                        owner,
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
                                userProperties.add(i, sale);
                            }
                            // lease type
                            if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).toString().equals(DealType.ForLease.toString())) {
                                lease = new Lease(
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                        owner,
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
                                userProperties.add(i, lease);
                            }
                        }
                        tvAllListingCount.setText(userProperties.size() + " records.");
                        // Creates ViewAdapterAllProperties Object to gvUserListings
                        gvAllListings.setVisibility(View.VISIBLE);
                        gvAllListings.setAdapter(new ViewAdapterAllProperties(getActivity(), userProperties));
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("Json Error:", e.getMessage());
                }
            }
        }).execute();
        return view;
    }

    public void setControls(View view) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("All Lisings");
        // get all listings
        tvAllListingCount = (TextView) view.findViewById(R.id.TVAllListingCount);
        gvAllListings = (GridView) view.findViewById(R.id.GVAllListings);

        // search behaviour
        svSearch = (SearchView) view.findViewById(R.id.SVSearch);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // new AsyncTask_SearchListing().execute(newText);
                Map<String, String> paramValues = new HashMap<>();
                paramValues.put("searchVal", newText);
                new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_SEARCHLISTINGS, paramValues, getActivity(), new AsyncTaskResponse() {
                    @Override
                    public void onAsyncTaskResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            // check for error in json
                            if (error) {
                                String errorMsg = jObj.getString("error_msg");
                                tvAllListingCount.setText(errorMsg);
                                gvAllListings.setVisibility(View.INVISIBLE);
                            } else {
                                userProperties = new ArrayList<Property>();
                                JSONArray results = jObj.getJSONArray("result");

                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject propertyObj = results.getJSONObject(i);
                                    owner = new User(
                                            propertyObj.getString(UserCtrl.KEY_USERID),
                                            propertyObj.getString(UserCtrl.KEY_NAME),
                                            propertyObj.getString(UserCtrl.KEY_EMAIL),
                                            propertyObj.getString(UserCtrl.KEY_CONTACT));

                                    // sale type
                                    if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).toString().equals(DealType.ForSale.toString())) {
                                        sale = new Sale(
                                                propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                                owner,
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
                                        userProperties.add(i, sale);
                                    }
                                    // lease type
                                    if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).toString().equals(DealType.ForLease.toString())) {
                                        lease = new Lease(
                                                propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                                owner,
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
                                        userProperties.add(i, lease);
                                    }
                                }
                                tvAllListingCount.setText(userProperties.size() + " records.");
                                // Creates ViewAdapterAllProperties Object to gvUserListings
                                gvAllListings.setVisibility(View.VISIBLE);
                                gvAllListings.setAdapter(new ViewAdapterAllProperties(getActivity(), userProperties));
                            }

                        } catch (JSONException error) {
                            // json error
                            error.printStackTrace();
                            ErrorHandler.errorHandler(getActivity(), error);
                        }
                    }
                }).execute();

                return false;
            }
        });


        // item selected behaviour
        gvAllListings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // retrieve property id
                itemDataID = (TextView) view.findViewById(R.id.TVLblPropertyID);
                Bundle bundlePropertyDetails = new Bundle();
                bundlePropertyDetails.putString(PropertyCtrl.KEY_PROPERTY_PROPERTYID, itemDataID.getText().toString());
                FragmentHandler.getInstance().loadFragment(FragmentAllListings.this, new FragmentPropertyDetails(), bundlePropertyDetails);
            }
        });

    }


    @Override
    public void onStart() {
        Log.w(TAG, "onStart");
        super.onStart();

    }

    @Override
    public void onResume() {
        Log.w(TAG, "onResume");
        super.onResume();
        // fetching user details from sqlite
        user = userCtrl.getUserDetails();

    }

    @Override
    public void onPause() {
        Log.w(TAG, "onPause");
        super.onPause();
        if (user != null) {
            userCtrl.updateUserDetails(user);
        } else
            Log.e(TAG, "No user to retain");
    }

    @Override
    public void onStop() {
        Log.w(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }

}


