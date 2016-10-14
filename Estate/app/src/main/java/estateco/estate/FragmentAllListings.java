package estateco.estate;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controllers.EstateConfig;
import controllers.EstateCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Property;
import entities.User;
import helper.FragmentHandler;
import helper.SQLiteHandler;
import helper.SessionManager;
import helper.ViewAdapterAllProperties;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllListings extends Fragment {
    private static final String TAG = FragmentAllListings.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private Property property;

    //ArrayList for property info
    private ArrayList<Property> properties;

    GridView gvAllListings;
    SearchView svSearch;
    TextView tvAllListingCount, itemDataPropertyID;

    public FragmentAllListings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_listings, container, false);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // setup ctrl objects
        db = new SQLiteHandler(getActivity());
        userCtrl = new UserCtrl(getActivity());
        session = new SessionManager(getActivity());

        setControls(view);
        // new AsyncTask_AllListing().execute();

        new AsyncTask_SearchListing().execute("");
        return view;
    }

    public void setControls(View view) {

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

                new AsyncTask_SearchListing().execute(newText);
                return false;
            }
        });


        // TODO get property details from server
        // item selected behaviour
        gvAllListings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // retrieve property id
                itemDataPropertyID = (TextView) view.findViewById(R.id.TVPropertyID);

                Bundle bundlePropertyDetails = new Bundle();
                bundlePropertyDetails.putString(PropertyCtrl.KEY_PROPERTY_PROPERTYID, itemDataPropertyID.getText().toString());
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
        // Fetching user details from sqlite
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

    // Async Task
    private class AsyncTask_SearchListing extends AsyncTask<String, Property, Void> {
        // Tag used to cancel the request
        String tag_string_req = "req_propertyCtrl";
        Boolean IsInternetConnected = false;
        ArrayList<Property> properties;
        User owner;

        @Override
        protected void onPreExecute() {
            Log.w("onPreExecute", "onPreExecute()");
            IsInternetConnected = EstateCtrl.CheckInternetConnection(getActivity());
            properties = new ArrayList<>();
            // loading dialog
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Loading properties ...");
            showDialog();
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.w("doInBackground", "doInBackground()");
            if (IsInternetConnected) {
                // Connect to server
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        EstateConfig.URL_SEARCHLISTINGS, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Property Ctrl Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (error) {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Log.d("Json Response Error", errorMsg);
                            } else {
                                JSONArray results = jObj.getJSONArray("result");

                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject propertyObj = results.getJSONObject(i);

                                    owner = new User(
                                            propertyObj.getString(UserCtrl.KEY_USERID),
                                            propertyObj.getString(UserCtrl.KEY_NAME),
                                            propertyObj.getString(UserCtrl.KEY_EMAIL),
                                            propertyObj.getString(UserCtrl.KEY_CONTACT));

                                    property = new Property(
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                            user,
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
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE)
                                    );

                                    properties.add(i, property);
                                }
                            }
                            // Creates ViewAdapterAllProperties Object to gvMyListings
                            gvAllListings.setAdapter(new ViewAdapterAllProperties(getActivity(), properties));
                            tvAllListingCount.setText(properties.size() + " records.");
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Log.e("Json Error:", e.getMessage());
                        }
                        hideDialog();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        Toast.makeText(getActivity(), "Server is down...", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Log.i(TAG, "getParams()");
                        // Posting params to register url
                        Map<String, String> paramsSearch = new HashMap<>();
                        paramsSearch.put("searchVal", params[0]);
                        return paramsSearch;
                    }
                };

                // Adding request to request queue
                EstateCtrl.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Property... property) {
            Log.w("onProgressUpdate", "onProgressUpdate()");
        }

        @Override
        protected void onPostExecute(Void response) {
            Log.w("onPostExecute", "onPostExecute()");
            // hide soft key board
            EstateCtrl.getInstance().hideSoftKeyboard(getActivity());
        }

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}


