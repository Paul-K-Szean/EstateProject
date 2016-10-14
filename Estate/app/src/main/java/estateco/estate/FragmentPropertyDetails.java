package estateco.estate;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import enums.DealType;
import handler.ImageHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPropertyDetails extends Fragment {
    private static final String TAG = FragmentPropertyDetails.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;
    private Property property;
    private Sale sale;
    private Lease lease;

    TextView tvTitle, tvDesc, tvFlatType, tvDealType, tvFurnishLevel, tvPrice, tvNoOfBedrooms, tvNoOfBathrooms, tvFloorArea,
            tvAddressName, tvPostalCode, tvUnit, tvOwnerName, tvOwnerEmail, tvOwnerContact;
    ImageView imgvPreview;

    public FragmentPropertyDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);
        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // setup ctrl objects
        db = new SQLiteHandler(getActivity());
        userCtrl = new UserCtrl(getActivity());
        user = userCtrl.getUserDetails();
        propertyCtrl = new PropertyCtrl(getActivity());
        savedInstanceState = getArguments();

        // setControls
        setControls(view, savedInstanceState);

        return view;
    }

    private void setControls(View view, Bundle savedInstanceState) {

        imgvPreview = (ImageView) view.findViewById(R.id.IMGVPreview);
        tvTitle = (TextView) view.findViewById(R.id.TVLblUpdateTitle);
        tvDesc = (TextView) view.findViewById(R.id.TVDesc);
        tvFlatType = (TextView) view.findViewById(R.id.TVFlatType);
        tvDealType = (TextView) view.findViewById(R.id.TVDealType);
        tvFurnishLevel = (TextView) view.findViewById(R.id.TVFurnishLevel);
        tvPrice = (TextView) view.findViewById(R.id.TVPrice);
        tvNoOfBedrooms = (TextView) view.findViewById(R.id.TVNoOfBedrooms);
        tvNoOfBathrooms = (TextView) view.findViewById(R.id.TVNoOfBathrooms);
        tvFloorArea = (TextView) view.findViewById(R.id.TVFloorArea);
        tvAddressName = (TextView) view.findViewById(R.id.TVAddressName);
        tvPostalCode = (TextView) view.findViewById(R.id.TVPostalCode);
        tvUnit = (TextView) view.findViewById(R.id.TVunit);
        tvOwnerName = (TextView) view.findViewById(R.id.TVOwnerName);
        tvOwnerEmail = (TextView) view.findViewById(R.id.TVOwnerEmail);
        tvOwnerContact = (TextView) view.findViewById(R.id.TVOwnerContact);

        // get property details from server
        new AsyncTask_PropertyDetails().execute(savedInstanceState.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID));


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
    private class AsyncTask_PropertyDetails extends AsyncTask<String, Property, Void> {
        // Tag used to cancel the request
        String tag_string_req = "req_propertydetails";
        Boolean IsInternetConnected = false;
        ArrayList<Property> userPropertyList;

        @Override
        protected void onPreExecute() {
            Log.w("onPreExecute", "onPreExecute()");
            IsInternetConnected = EstateCtrl.CheckInternetConnection(getActivity());
            userPropertyList = new ArrayList<>();
            // loading dialog
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Loading property detail ...");
            showDialog();
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.w("doInBackground", "doInBackground()");
            if (IsInternetConnected) {

                // Connect to server
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        EstateConfig.URL_PROPERTYDETAILS, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Property Details Response: " + response.toString());
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (error) {
                                // Error. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Log.e("Json Response Error", errorMsg);
                            } else {
                                JSONObject propertyObj = jObj.getJSONObject("property");
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
                                        propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));

                                tvFlatType.setText(property.getFlatType());
                                tvDealType.setText(property.getDealType());
                                tvTitle.setText(property.getTitle());
                                tvDesc.setText(property.getDescription());

                                tvFurnishLevel.setText(property.getFurnishLevel());
                                tvPrice.setText("SGD$" + property.getPrice());
                                tvPostalCode.setText("S'pore(" + property.getPostalcode() + ")");
                                tvUnit.setText("#" + property.getUnit());
                                tvAddressName.setText(property.getAddressName());
                                tvNoOfBedrooms.setText(property.getNoOfbedrooms() + " bedroom(s)");
                                tvNoOfBathrooms.setText(property.getNoOfbathrooms() + " bathroom(s)");


                                tvOwnerName.setText(property.getOwner().getName());
                                tvOwnerEmail.setText(property.getOwner().getEmail());
                                tvOwnerContact.setText(property.getOwner().getContact());
                                // TODO WHOLEAPARTMENT
                                if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).equals(DealType.ForSale)) {
                                    lease = new Lease(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT));
                                } else {
                                    sale = new Sale(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA));
                                    tvFloorArea.setText(sale.getFloorArea() + "Sq Meters");
                                }


                                // check if photo is empty
                                String photoData = property.getPhoto();
                                if (photoData.isEmpty())
                                    imgvPreview.setImageResource(R.drawable.ic_menu_camera);
                                else
                                    imgvPreview.setImageBitmap(ImageHandler.getInstance().decodeStringToImage(photoData));


//                                tvTitle.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_TITLE));
//                                tvDesc.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DESC));
//                                tvUnit.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_UNIT));
//                                tvFlatType.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLATTYPE));
//                                tvDealType.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE));
//                                tvFurnishLevel.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL));
//                                tvPrice.setText("SGD$" + propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PRICE));
//                                tvPostalCode.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_POSTALCODE));
//                                tvUnit.setText("#" + propertyObj.getString(PropertyCtrl.KEY_PROPERTY_UNIT));
//                                tvAddressName.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME));
//                                tvNoOfBedrooms.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS + "bedroom(s)"));
//                                tvNoOfBathrooms.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS + "bathroom(s)"));
//                                tvFloorArea.setText(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA));
//
//                                tvOwnerName.setText(user.getName());
//                                tvOwnerEmail.setText(user.getEmail());
//                                tvOwnerContact.setText(user.getContact());
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
                        paramsUser.put("propertyID", params[0]);
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
