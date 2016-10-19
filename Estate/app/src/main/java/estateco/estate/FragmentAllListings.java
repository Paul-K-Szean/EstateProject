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

import controllers.UserCtrl;
import entities.Property;
import entities.User;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.FragmentHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.ViewAdapterAllProperties;

import static controllers.EstateConfig.URL_ALLLISTINGS;
import static controllers.EstateConfig.URL_SEARCHLISTINGS;
import static controllers.PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BLOCK;
import static controllers.PropertyCtrl.KEY_PROPERTY_CREATEDDATE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DEALTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DESC;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLATTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORAREA;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_IMAGE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PRICE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;
import static controllers.PropertyCtrl.KEY_PROPERTY_SEARCH;
import static controllers.PropertyCtrl.KEY_PROPERTY_STATUS;
import static controllers.PropertyCtrl.KEY_PROPERTY_STREETNAME;
import static controllers.PropertyCtrl.KEY_PROPERTY_TITLE;
import static controllers.PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT;
import static controllers.UserCtrl.KEY_CONTACT;
import static controllers.UserCtrl.KEY_EMAIL;
import static controllers.UserCtrl.KEY_NAME;
import static controllers.UserCtrl.KEY_USERID;


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
    private Property property;


    //ArrayList for property info
    private ArrayList<Property> allProperties;
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
        new AsyncTaskHandler(Request.Method.GET, URL_ALLLISTINGS, null, getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                showListing(response);
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

                Map<String, String> paramValues = new HashMap<>();
                paramValues.put(KEY_PROPERTY_SEARCH, newText);
                new AsyncTaskHandler(Request.Method.POST, URL_SEARCHLISTINGS, paramValues, getActivity(), new AsyncTaskResponse() {
                    @Override
                    public void onAsyncTaskResponse(String response) {
                        showListing(response);
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
                bundlePropertyDetails.putString(KEY_PROPERTY_PROPERTYID, itemDataID.getText().toString());
                FragmentHandler.loadFragment(FragmentAllListings.this, new FragmentPropertyDetails(), bundlePropertyDetails);

            }
        });

    }


    private void showListing(String response) {
        try {
            allProperties = new ArrayList<>();
            JSONArray jsonArray = JSONHandler.getResultAsArray(getActivity(), response);
            if (jsonArray != null) {
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonRecordObject = jsonArray.getJSONObject(index);
                    owner = new User(
                            jsonRecordObject.getString(KEY_USERID),
                            jsonRecordObject.getString(KEY_NAME),
                            jsonRecordObject.getString(KEY_EMAIL),
                            jsonRecordObject.getString(KEY_CONTACT));

                    property = new Property(
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
                            jsonRecordObject.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                            jsonRecordObject.getString(KEY_PROPERTY_CREATEDDATE));
                    allProperties.add(property);
                }
                tvAllListingCount.setText(allProperties.size() + " records.");
                // Creates ViewAdapterAllProperties Object to gvUserListings
                gvAllListings.setVisibility(View.VISIBLE);
                gvAllListings.setAdapter(new ViewAdapterAllProperties(getActivity(), allProperties));

            } else {
                tvAllListingCount.setText("No property found.");
                gvAllListings.setVisibility(View.INVISIBLE);
            }


        } catch (JSONException error) {

        }
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


