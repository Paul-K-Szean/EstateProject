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
import android.widget.Button;
import android.widget.GridView;
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
import entities.Lease;
import entities.Property;
import entities.Sale;
import entities.User;
import handler.FragmentHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.ViewAdapterAllProperties;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUserListings extends Fragment {
    private static final String TAG = FragmentUserListings.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;
    private Property property;
    private Sale sale;
    private Lease lease;


    Button btnNewListing;
    GridView gvUserListings;
    TextView tvUserMsg, itemDataID;


    public FragmentUserListings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_listings, container, false);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // setup ctrl objects
        db = new SQLiteHandler(getActivity());
        userCtrl = new UserCtrl(getActivity());
        user = userCtrl.getUserDetails();
        propertyCtrl = new PropertyCtrl(getActivity());


        // check if user is already logged in or not
        session = new SessionHandler(getActivity());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity

        } else {
            userCtrl.deleteUserDetails();
            session.setLogin(false);
            // getActivity().finish();
        }

        setControls(view);


        return view;
    }

    public void setControls(View view) {

        // error msg
        tvUserMsg = (TextView) view.findViewById(R.id.TVUserMsg);

        // get user listings
        gvUserListings = (GridView) view.findViewById(R.id.GVUserListings);

        // TODO: DO SOMETHING WHEN RECORD ALREADY EXIST INSTEAD OR REMOVING AND ADDING BACK TO LOCAL DB
        // clear existing property data
        propertyCtrl.deletePropertyDetails();
        // fetch from server, copy to local DB
        new AsyncTask_UserListings().execute(user.getUserID());

        // new property listing
        btnNewListing = (Button) view.findViewById(R.id.BTNNewListing);
        btnNewListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to new listing step 1
                FragmentHandler.getInstance().loadFragment(FragmentUserListings.this, new FragmentNewPropertyStep1());
            }
        });

        // TODO ALLOW USER TO EDIT THEIR PROPERTIES
        // item selected behaviour
        gvUserListings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // retrieve property id
                itemDataID = (TextView) view.findViewById(R.id.TVPropertyID);
                Bundle bundlePropertyDetails = new Bundle();
                bundlePropertyDetails.putString(PropertyCtrl.KEY_PROPERTY_PROPERTYID, itemDataID.getText().toString());
                FragmentHandler.getInstance().loadFragment(FragmentUserListings.this, new FragmentUpdateUserProperty(), bundlePropertyDetails);
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
    private class AsyncTask_UserListings extends AsyncTask<String, Property, Void> {
        // Tag used to cancel the request
        String tag_string_req = "req_propertyCtrl";
        Boolean IsInternetConnected = false;
        ArrayList<Property> userPropertyList;

        @Override
        protected void onPreExecute() {
            Log.w("onPreExecute", "onPreExecute()");
            IsInternetConnected = EstateCtrl.CheckInternetConnection(getActivity());
            userPropertyList = new ArrayList<>();
            // loading dialog
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Loading user property ...");
            showDialog();
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.w("doInBackground", "doInBackground()");
            if (IsInternetConnected) {

                // Connect to server
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        EstateConfig.URL_USERLISTINGS, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Property Ctrl Response: " + response.toString());
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (error) {
                                // Error. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                gvUserListings.setAdapter(new ViewAdapterAllProperties(getActivity(), userPropertyList));
                                tvUserMsg.setText("You do not have any property created.");
                                Log.e("Json Response Error", errorMsg);
                            } else {

                                JSONArray results = jObj.getJSONArray("result");
                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject propertyObj = results.getJSONObject(i);
                                    if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).contains("Sale")) {
                                        sale = new Sale(
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
                                                propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA),
                                                propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));
                                        // insert data into local db
                                        propertyCtrl.addPropertyDetails(sale);
                                        // publishProgress(sale);
                                    } else {
                                        lease = new Lease(
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
                                                propertyObj.getString(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT),
                                                propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));
                                        // insert data into local db
                                        propertyCtrl.addPropertyDetails(lease);
                                        // publishProgress(lease);
                                    }
                                }

                                // retrieves data from local db
                                userPropertyList = propertyCtrl.getUserProperties(user);

                                // displays into grid view
                                gvUserListings.setAdapter(new ViewAdapterAllProperties(getActivity(), userPropertyList));

                                if (results.length() > 1)
                                    tvUserMsg.setText("You have a total of " + results.length() + " properties");
                                else
                                    tvUserMsg.setText("You have a total of " + results.length() + " property");
                            }
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
                        Log.e(TAG, "Property Ctrl Error: " + error.getMessage());
                        Toast.makeText(getActivity(), "Server is down...", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to url
                        Map<String, String> paramsUser = new HashMap<>();
                        paramsUser.put("ownerID", params[0]);

                        return paramsUser;
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
