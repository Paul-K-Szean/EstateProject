package estateco.estate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import controllers.EstateConfig;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Lease;
import entities.Sale;
import entities.User;
import enums.DealType;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.ImageHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPropertyDetails extends Fragment {
    private static final String TAG = FragmentPropertyDetails.class.getSimpleName();
    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private User owner;
    private PropertyCtrl propertyCtrl;
    private Sale sale;
    private Lease lease;

    TextView tvTitle, tvDesc, tvFlatType, tvDealType, tvFurnishLevel, tvPrice, tvNoOfBedrooms, tvNoOfBathrooms, tvFloorArea,
            tvAddressName, tvPostalCode, tvUnit, tvWholeApartment, tvOwnerName, tvOwnerEmail, tvOwnerContact;
    ImageView imgvPreview;
    String valProDetPropertyID, valProDetOwnerID, valProDetOwnerName, valProDetOwnerEmail, valProDetOwnerContact, valProDetFlatType, valProDetDealType, valProDetTitle, valProDetDescription, valProDetFurnishLevel, valProDetPrice,
            valProDetPostalCode, valProDetUnit, valProDetAddressName, valProDetPhoto, valProDetStatus,
            valProDetSaleNoOfBedrooms, valProDetSaleNoOfBathrooms, valProDetFloorArea, valProDetWholeApartment, valProDetCreatedDate;
    Toolbar toolbar;

    public FragmentPropertyDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);

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
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Property Details");
        // TODO SHOW FAVOURITE ICON
        Log.i(TAG, toolbar.getMenu().getItem(0).toString());
        Log.i(TAG, toolbar.getMenu().getItem(1).toString());
        Log.i(TAG, toolbar.getMenu().getItem(2).toString());

        imgvPreview = (ImageView) view.findViewById(R.id.IMGVPreview);
        tvTitle = (TextView) view.findViewById(R.id.TVLblTitle);
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
        tvWholeApartment = (TextView) view.findViewById(R.id.TVWholeApartment);
        tvOwnerName = (TextView) view.findViewById(R.id.TVOwnerName);
        tvOwnerEmail = (TextView) view.findViewById(R.id.TVOwnerEmail);
        tvOwnerContact = (TextView) view.findViewById(R.id.TVOwnerContact);


        // get property details from server
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, savedInstanceState.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID).toString());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_PROPERTYDETAILS, paramValues, getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // check for error in json
                    if (error) {
                        // ALREADY HANDLED
                    } else {
                        JSONObject propertyObj = jObj.getJSONObject("property");
                        owner = new User(
                                valProDetOwnerID = propertyObj.getString(UserCtrl.KEY_USERID),
                                valProDetOwnerName = propertyObj.getString(UserCtrl.KEY_NAME),
                                valProDetOwnerEmail = propertyObj.getString(UserCtrl.KEY_EMAIL),
                                valProDetOwnerContact = propertyObj.getString(UserCtrl.KEY_CONTACT));

                        // sale type
                        if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).toString().equals(DealType.ForSale.toString())) {
                            sale = new Sale(
                                    valProDetPropertyID = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                    owner,
                                    valProDetFlatType = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLATTYPE),
                                    valProDetDealType = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                                    valProDetTitle = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_TITLE),
                                    valProDetDescription = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DESC),
                                    valProDetFurnishLevel = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                                    valProDetPrice = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PRICE),
                                    valProDetPostalCode = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_POSTALCODE),
                                    valProDetUnit = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_UNIT),
                                    valProDetAddressName = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME),
                                    valProDetPhoto = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PHOTO),
                                    valProDetStatus = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_STATUS),
                                    valProDetSaleNoOfBedrooms = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS),
                                    valProDetSaleNoOfBathrooms = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS),
                                    valProDetFloorArea = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA),
                                    valProDetCreatedDate = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));
                        }
                        // lease type
                        if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).toString().equals(DealType.ForLease.toString())) {
                            lease = new Lease(
                                    valProDetPropertyID = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                    owner,
                                    valProDetFlatType = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLATTYPE),
                                    valProDetDealType = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                                    valProDetTitle = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_TITLE),
                                    valProDetDescription = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DESC),
                                    valProDetFurnishLevel = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                                    valProDetPrice = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PRICE),
                                    valProDetPostalCode = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_POSTALCODE),
                                    valProDetUnit = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_UNIT),
                                    valProDetAddressName = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME),
                                    valProDetPhoto = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PHOTO),
                                    valProDetStatus = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_STATUS),
                                    valProDetSaleNoOfBedrooms = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS),
                                    valProDetSaleNoOfBathrooms = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS),
                                    valProDetWholeApartment = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT),
                                    valProDetCreatedDate = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));
                            // leasing whole apartment
                            valProDetFloorArea = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA);
                        }


                    }
                } catch (JSONException error) {
                    error.printStackTrace();
                    ErrorHandler.errorHandler(getActivity(), error);
                }

                tvFlatType.setText(valProDetFlatType);
                tvDealType.setText(valProDetDealType);
                tvTitle.setText(valProDetTitle);
                tvDesc.setText(valProDetDescription);
                tvFurnishLevel.setText(valProDetFurnishLevel);
                tvPrice.setText("SGD$" + valProDetPrice);
                tvPostalCode.setText("S'pore(" + valProDetPostalCode + ")");
                tvUnit.setText("#" + valProDetUnit);
                tvAddressName.setText(valProDetAddressName);
                tvNoOfBedrooms.setText(valProDetSaleNoOfBedrooms + " bedroom(s)");
                tvNoOfBathrooms.setText(valProDetSaleNoOfBathrooms + " bathroom(s)");
                tvFloorArea.setText(valProDetFloorArea + " Sq Meters");

                tvOwnerName.setText(valProDetOwnerName);
                tvOwnerEmail.setText(valProDetOwnerEmail);
                tvOwnerContact.setText(valProDetOwnerContact);
                int roomCount = Integer.valueOf(valProDetSaleNoOfBedrooms);
                if (lease != null) {
                    // room
                    if (valProDetWholeApartment.equals("room")) {
                        if (roomCount <= 1) {
                            tvWholeApartment.setText(valProDetSaleNoOfBedrooms + " " + valProDetWholeApartment);
                        } else {
                            tvWholeApartment.setText(valProDetSaleNoOfBedrooms + " " + valProDetWholeApartment + "s");
                        }
                    }
                    if (valProDetWholeApartment.equals(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT)) {
                        tvWholeApartment.setText("Leasing whole apartment");
                    }

                }
                if (sale != null) {

                }


                // check if photo is empty

                if (valProDetPhoto.isEmpty())
                    imgvPreview.setImageResource(R.drawable.ic_menu_camera);
                else
                    imgvPreview.setImageBitmap(ImageHandler.getInstance().decodeStringToImage(valProDetPhoto));
            }
        }).execute();


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


}
