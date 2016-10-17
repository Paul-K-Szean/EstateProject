package estateco.estate;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import controllers.EstateConfig;
import controllers.EstateCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Property;
import entities.User;
import enums.DealType;
import enums.FlatType;
import enums.FurnishLevel;
import handler.FragmentHandler;
import handler.ImageHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNewPropertyStep2 extends Fragment {
    private static final String TAG = FragmentNewPropertyStep2.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private Property property;

    private static final int SELECTED_IMAGE = 1;
    Bitmap bitmap;
    Button btnCreateListing, btnSelectPhoto, btnRandomise;
    ImageView imgvPreview;
    Spinner spSaleNoOfBedrooms, spSaleNoOfBathrooms, spFlatType, spFurnishLevel;
    EditText etFloorArea, etPrice;
    TextView tvFloorArea;
    String valOwnerID, valFlatType, valDealType, valTitle, valDescription, valFurnishLevel, valPrice,
            valPostalCode, valUnit, valAddressName, valPhoto, valStatus,
            valSaleNoOfBedrooms, valSaleNoOfBathrooms, valFloorArea, valWholeApartment, selectedImagePath;

    CheckBox chkbxWholeApartment;

    public FragmentNewPropertyStep2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_listing_step2, container, false);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // setup ctrl objects
        db = new SQLiteHandler(getActivity());
        userCtrl = new UserCtrl(getActivity());
        session = new SessionHandler(getActivity());
        user = userCtrl.getUserDetails();

        // check if user is already logged in or not
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

    private void setControls(View view) {

        // set controls
        // spinners
        spFlatType = (Spinner) view.findViewById(R.id.SPFlatType);
        spFlatType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FlatType.values()));
        spFurnishLevel = (Spinner) view.findViewById(R.id.SPFurnishLevel);
        spFurnishLevel.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FurnishLevel.values()));

        spSaleNoOfBedrooms = (Spinner) view.findViewById(R.id.SPNoOfBedRooms);
        String[] arrBedRooms = {"Select Number Of Bed Room(s)", "1", "2", "3", "4", "5"};
        spSaleNoOfBedrooms.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrBedRooms));

        spSaleNoOfBathrooms = (Spinner) view.findViewById(R.id.SPNoOfBathRooms);
        String[] arrBathRooms = {"Select Number Of Bath Room(s)", "1", "2", "3"};
        spSaleNoOfBathrooms.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrBathRooms));

        // edit texts
        etPrice = (EditText) view.findViewById(R.id.ETEditPrice);
        etFloorArea = (EditText) view.findViewById(R.id.ETFloorArea);

        // image view
        imgvPreview = (ImageView) view.findViewById(R.id.IMGVPreview);

        // checkbox
        chkbxWholeApartment = (CheckBox) view.findViewById(R.id.CHKBXWholeApartment);

        // check deal type
        final Bundle fragmentStep1Details = getArguments();
        Log.i(TAG, fragmentStep1Details.get(PropertyCtrl.KEY_PROPERTY_DEALTYPE).toString());
        if (fragmentStep1Details.get(PropertyCtrl.KEY_PROPERTY_DEALTYPE).equals("For Lease")) {
            // hide floor area
            Log.i(TAG, "inside floorarea");
            etFloorArea.setVisibility(View.INVISIBLE);  //edittext
            tvFloorArea = (TextView) view.findViewById(R.id.TVFloorArea); // textview
            tvFloorArea.setVisibility(View.INVISIBLE);
        } else {
            // hide check box if deal type is for sale
            Log.i(TAG, "inside chkbx");
            chkbxWholeApartment.setVisibility(View.INVISIBLE);
        }
        valTitle = fragmentStep1Details.get(PropertyCtrl.KEY_PROPERTY_TITLE).toString();
        valDescription = fragmentStep1Details.get(PropertyCtrl.KEY_PROPERTY_DESC).toString();
        valPostalCode = fragmentStep1Details.get(PropertyCtrl.KEY_PROPERTY_POSTALCODE).toString();
        valUnit = fragmentStep1Details.get(PropertyCtrl.KEY_PROPERTY_UNIT).toString();
        valAddressName = fragmentStep1Details.get(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME).toString();

        // button controls

        // image btn
        btnSelectPhoto = (Button) view.findViewById(R.id.BTNSelectPhoto);
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ask for user permission to access gallery
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        });

        // create button
        btnCreateListing = (Button) view.findViewById(R.id.BTNCreateListing);
        btnCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Create listing clicked");
                valOwnerID = user.getUserID();
                valFlatType = spFlatType.getSelectedItem().toString().trim();
                valDealType = fragmentStep1Details.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE);
                valFurnishLevel = spFurnishLevel.getSelectedItem().toString().trim();
                valPrice = etPrice.getText().toString().trim();

                if (bitmap != null)
                    valPhoto = ImageHandler.getInstance().encodeImagetoString(bitmap);
                else {

                    imgvPreview.buildDrawingCache();
                    bitmap = imgvPreview.getDrawingCache();
                    // bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_menu_camera);
                    valPhoto = ImageHandler.getInstance().encodeImagetoString(bitmap);
                }

                valStatus = "open";
                valSaleNoOfBedrooms = spSaleNoOfBedrooms.getSelectedItem().toString().trim();
                valSaleNoOfBathrooms = spSaleNoOfBathrooms.getSelectedItem().toString().trim();
                valFloorArea = etFloorArea.getText().toString().trim();

                if (valDealType.equals(DealType.ForSale))
                    valWholeApartment = PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT;

                if (valDealType.equals(DealType.ForLease)) {
                    if (chkbxWholeApartment.isChecked())
                        valWholeApartment = PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT;
                    else
                        valWholeApartment = "room";
                }

                // check for no empty fields then create
                if (spFlatType.getSelectedItemId() != 0 &&
                        spFurnishLevel.getSelectedItemId() != 0 &&
                        spSaleNoOfBedrooms.getSelectedItemId() != 0 &&
                        spSaleNoOfBathrooms.getSelectedItemId() != 0 &&
                        !valPrice.isEmpty() &&
                        !valFloorArea.isEmpty()) {
                    Log.i(TAG, "Create into DB");

                    new AsyncTask_NewPropertySale().execute(valOwnerID, valFlatType, valDealType, valTitle, valDescription,
                            valFurnishLevel, valPrice, valPostalCode, valUnit, valAddressName, valPhoto, valStatus,
                            valSaleNoOfBedrooms, valSaleNoOfBathrooms, valFloorArea, valWholeApartment);

                } else {
                    // empty fields
                    if (spFlatType.getSelectedItemId() == 0)
                        ((TextView) spFlatType.getSelectedView()).setError("Required field!");
                    if (spFurnishLevel.getSelectedItemId() == 0)
                        ((TextView) spFurnishLevel.getSelectedView()).setError("Required field!");
                    if (spSaleNoOfBedrooms.getSelectedItemId() == 0)
                        ((TextView) spSaleNoOfBedrooms.getSelectedView()).setError("Required field!");
                    if (spSaleNoOfBathrooms.getSelectedItemId() == 0)
                        ((TextView) spSaleNoOfBathrooms.getSelectedView()).setError("Required field!");
                    if (valPrice.isEmpty())
                        etPrice.setError("Required field!");
                    if (valFloorArea.isEmpty())
                        etFloorArea.setError("Required field!");
                }

            }

        });

        // random btn
        btnRandomise = (Button) view.findViewById(R.id.BTNRandomise);
        btnRandomise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spFlatType.setSelection(Utility.generateNumber(1, spFlatType.getCount() - 1));
                spFurnishLevel.setSelection(Utility.generateNumber(1, spFurnishLevel.getCount() - 1));
                spSaleNoOfBedrooms.setSelection(Utility.generateNumber(1, spSaleNoOfBedrooms.getCount() - 1));
                spSaleNoOfBathrooms.setSelection(Utility.generateNumber(1, spSaleNoOfBathrooms.getCount() - 1));
                etPrice.setText(Utility.formatStringNumber(Utility.generateNumberAsString(299999, 19999999)));
                etFloorArea.setText(Utility.generateNumberAsString(36, 400));
                etPrice.setError(null);
                etFloorArea.setError(null);
                imgvPreview.setImageResource(R.drawable.ic_menu_camera);
                chkbxWholeApartment.setChecked(Utility.generateBool());
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission

            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, SELECTED_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(projection[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                selectedImagePath = filePath;
                imgvPreview.setImageBitmap(BitmapFactory.decodeFile(filePath));
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());

            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
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
    private class AsyncTask_NewPropertySale extends AsyncTask<String, Void, Void> {
        // Tag used to cancel the request
        String tag_string_req = "req_newpropertysale";
        Boolean IsInternetConnected = false;

        @Override
        protected void onPreExecute() {
            Log.w(TAG, "onPreExecute()");
            IsInternetConnected = EstateCtrl.CheckInternetConnection(getActivity());
            // loading dialog
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Creating new property...");
            showDialog();
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.w(TAG, "doInBackground()");
            for (String param : params) {
                Log.w(TAG, "param value = " + param);
            }
            if (IsInternetConnected) {
                // Connect to server
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        EstateConfig.URL_NEWPROPERTY, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "New Property Response: " + response.toString());
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (error) {
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getActivity(),
                                        errorMsg, Toast.LENGTH_LONG).show();
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

                                Toast.makeText(getActivity(),
                                        "Property created successfully", Toast.LENGTH_LONG).show();
                                // move to user listing page.
                                FragmentHandler.getInstance().loadFragment(FragmentNewPropertyStep2.this, new FragmentUserListings());
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            // hideDialog();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        Toast.makeText(getActivity(),
                                "Server is down...", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> paramsLogin = new HashMap<>();
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_OWNERID, params[0]);    // ownerID
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_FLATTYPE, params[1]); // flattype
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, params[2]);    // dealtype
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_TITLE, params[3]); // title
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_DESC, params[4]);    // description
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, params[5]); // furnishlevel
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_PRICE, params[6]);    // price
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_POSTALCODE, params[7]); // postalcode
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_UNIT, params[8]);    // unit
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME, params[9]); // addressname
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_PHOTO, params[10]);    // photo
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_STATUS, params[11]); // status
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS, params[12]); // noofbedrooms
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS, params[13]); // noofbathrooms
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, params[14]);    // floorarea
                        paramsLogin.put(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT, params[15]);    // floorarea
                        return paramsLogin;
                    }

                };

                // Adding request to request queue
                EstateCtrl.getInstance().addToRequestQueue(strReq, tag_string_req);


            } else {
                Toast.makeText(getActivity(), "Network not detected!", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            Log.w(TAG, "onPostExecute()");
            // hide soft keyboard
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
