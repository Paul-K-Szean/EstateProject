package estateco.estate;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import entities.Property;
import entities.User;
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

    TextView tvPropDetTitle, tvPropDetDesc, tvPropDetFlatType, tvPropDetDealType, tvPropDetFurnishLevel, tvPropDetPrice, tvPropDetBedroomCount,
            tvPropDetBathroomCount, tvPropDetFloorArea, tvPropDetStreetName, tvPropDetFloorLevel, tvPropDetBlock, tvPropDetWholeApartment,
            tvPropDetOwnerName, tvPropDetOwnerEmail, tvPropDetOwnerContact;
    ImageView imgvPropDetImage;
    String valProDetPropertyID, valProDetOwnerID, valProDetOwnerName, valProDetOwnerEmail, valProDetOwnerContact, valProDetFlatType,
            valProDetDealType, valProDetTitle, valProDetDescription, valProDetFurnishLevel, valProDetPrice,
            valProDetBlock, valProDetStreetName, valProDetImage, valProDetStatus, valProDetBedroomCount, valProDetBathroomCount,
            valProDetFloorLevel, valProDetFloorArea, valProDetWholeApartment, valProDetCreatedDate;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;

    public FragmentPropertyDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);
        savedInstanceState = getArguments();
        setControls(view, savedInstanceState);

        // setup ctrl objects
        db = new SQLiteHandler(getActivity());
        userCtrl = new UserCtrl(getActivity());
        user = userCtrl.getUserDetails();
        propertyCtrl = new PropertyCtrl(getActivity());


        return view;
    }

    private void setControls(View view, Bundle savedInstanceState) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Property Details");
        // TODO SHOW FAVOURITE ICON
        //        Log.i(TAG, toolbar.getMenu().getItem(0).toString());
        //        Log.i(TAG, toolbar.getMenu().getItem(1).toString());
        //        Log.i(TAG, toolbar.getMenu().getItem(2).toString());

        //
        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.show();

        imgvPropDetImage = (ImageView) view.findViewById(R.id.IMGVPropDetImage);
        tvPropDetTitle = (TextView) view.findViewById(R.id.TVPropDet_Title);
        tvPropDetDesc = (TextView) view.findViewById(R.id.TVPropDet_Desc);
        tvPropDetFlatType = (TextView) view.findViewById(R.id.TVPropDet_FlatType);
        tvPropDetDealType = (TextView) view.findViewById(R.id.TVPropDet_DealType);
        tvPropDetFurnishLevel = (TextView) view.findViewById(R.id.TVPropDet_FurnishLevel);
        tvPropDetPrice = (TextView) view.findViewById(R.id.TVPropDet_Price);
        tvPropDetBedroomCount = (TextView) view.findViewById(R.id.TVPropDet_BedroomCount);
        tvPropDetBathroomCount = (TextView) view.findViewById(R.id.TVPropDet_BathroomCount);
        tvPropDetFloorArea = (TextView) view.findViewById(R.id.TVPropDet_FloorArea);
        tvPropDetStreetName = (TextView) view.findViewById(R.id.TVPropDet_AddressName);
        tvPropDetFloorLevel = (TextView) view.findViewById(R.id.TVPropDet_FloorLevel);

        tvPropDetWholeApartment = (TextView) view.findViewById(R.id.TVPropDet_WholeApartment);
        tvPropDetOwnerName = (TextView) view.findViewById(R.id.TVPropDet_OwnerName);
        tvPropDetOwnerEmail = (TextView) view.findViewById(R.id.TVPropDet_OwnerEmail);
        tvPropDetOwnerContact = (TextView) view.findViewById(R.id.TVPropDet_OwnerContact);


        // get property details from server
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, savedInstanceState.get(PropertyCtrl.KEY_PROPERTY_PROPERTYID).toString());
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_PROPERTYDETAILS, paramValues, getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                try {
                    JSONObject propertyObj = JSONHandler.getResultAsObject(getActivity(), response);
                    // check for error in json
                    if (propertyObj != null) {
                        owner = new User(
                                valProDetOwnerID = propertyObj.getString(UserCtrl.KEY_USERID),
                                valProDetOwnerName = propertyObj.getString(UserCtrl.KEY_NAME),
                                valProDetOwnerEmail = propertyObj.getString(UserCtrl.KEY_EMAIL),
                                valProDetOwnerContact = propertyObj.getString(UserCtrl.KEY_CONTACT));

                        Property property = new Property(
                                valProDetPropertyID = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                owner,
                                valProDetFlatType = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLATTYPE),
                                valProDetBlock = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_BLOCK),
                                valProDetStreetName = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_STREETNAME),
                                valProDetFloorLevel = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORLEVEL),
                                valProDetFloorArea = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA),
                                valProDetPrice = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PRICE),
                                valProDetImage = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_IMAGE),
                                valProDetStatus = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_STATUS),
                                valProDetDealType = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                                valProDetTitle = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_TITLE),
                                valProDetDescription = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DESC),
                                valProDetFurnishLevel = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                                valProDetBedroomCount = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT),
                                valProDetBathroomCount = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT),
                                valProDetWholeApartment = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT),
                                valProDetCreatedDate = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));

                    }
                } catch (JSONException error) {
                    error.printStackTrace();
                    ErrorHandler.errorHandler(getActivity(), error);
                }

                // deal details
                tvPropDetDealType.setText(valProDetDealType);
                // general details
                tvPropDetTitle.setText(valProDetTitle);
                tvPropDetDesc.setText(valProDetDescription);
                // address details
                tvPropDetFloorLevel.setText("Level " + valProDetFloorLevel);
                tvPropDetStreetName.setText(valProDetStreetName);
                // house details
                if (valProDetImage.isEmpty())
                    imgvPropDetImage.setImageResource(R.drawable.ic_menu_camera);
                else
                    imgvPropDetImage.setImageBitmap(ImageHandler.decodeStringToImage(valProDetImage));
                tvPropDetFlatType.setText(valProDetFlatType);
                tvPropDetPrice.setText("SGD$" + valProDetPrice);
                tvPropDetFloorArea.setText(valProDetFloorArea + " Sq Meters");
                tvPropDetFurnishLevel.setText(valProDetFurnishLevel);
                tvPropDetBedroomCount.setText(valProDetBedroomCount + " bedroom(s)");
                tvPropDetBathroomCount.setText(valProDetBathroomCount + " bathroom(s)");
                // owner details
                tvPropDetOwnerName.setText(valProDetOwnerName);
                tvPropDetOwnerEmail.setText(valProDetOwnerEmail);
                tvPropDetOwnerContact.setText(valProDetOwnerContact);


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
