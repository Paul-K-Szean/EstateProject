package estateco.estate;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controllers.EstateConfig;
import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Property;
import entities.User;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.ImageHandler;
import handler.JSONHandler;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPropertyDetails extends Fragment implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = FragmentPropertyDetails.class.getSimpleName();
    private static final String TAG_FARVOURITE = "favourite";
    private static final String TAG_PHONECALL = "phonecall";
    private static final String TAG_PHONEMESSAGE = "phonemessage";

    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;

    private User user;
    private User owner;
    private static Property property;
    private static Favourite favourite;
    ArrayList<Favourite> favouriteArrayList;
    static Boolean isFavourited = false;

    TextView tvPropDetTitle, tvPropDetDesc, tvPropDetFlatType, tvPropDetDealType, tvPropDetFurnishLevel, tvPropDetPrice, tvPropDetBedroomCount,
            tvPropDetBathroomCount, tvPropDetFloorArea, tvPropDetStreetName, tvPropDetFloorLevel, tvPropDetBlock, tvPropDetWholeApartment,
            tvPropDetOwnerName, tvPropDetOwnerEmail, tvPropDetOwnerContact;
    ImageView imgvPropDetImage;
    String valProDetPropertyID, valProDetOwnerID, valProDetOwnerName, valProDetOwnerEmail, valProDetOwnerContact, valProDetFlatType,
            valProDetDealType, valProDetTitle, valProDetDescription, valProDetFurnishLevel, valProDetPrice,
            valProDetBlock, valProDetStreetName, valProDetImage, valProDetStatus, valProDetBedroomCount, valProDetBathroomCount,
            valProDetFloorLevel, valProDetFloorArea, valProDetWholeApartment, valProDetCreatedDate;
    Toolbar toolBarTop, toolBarBottom;
    MenuItem menuItemFavourite, menuItemFavouriteCount;


    public FragmentPropertyDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);
        savedInstanceState = getActivity().getIntent().getExtras();


        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());
        propertyCtrl = new PropertyCtrl(getActivity());
        favouriteCtrl = new FavouriteCtrl(getActivity());
        user = userCtrl.getUserDetails();

        // set controls to user property
        setPropertyDetails(view, savedInstanceState);
        setControls(view, savedInstanceState);
        return view;
    }

    private void setControls(View view, Bundle savedInstanceState) {
        toolBarTop = (Toolbar) getActivity().findViewById(R.id.toolbar_top);
        toolBarTop.setTitle("Property Details");

        toolBarBottom = (Toolbar) getActivity().findViewById(R.id.toolbar_bottom);
        toolBarBottom.setVisibility(View.VISIBLE);
        toolBarBottom.setOnMenuItemClickListener(this);
        if ((menuItemFavourite = toolBarBottom.getMenu().findItem(R.id.action_favourite)) != null) {
            menuItemFavourite.setIcon(R.drawable.ic_action_favourite_outline);
        } else {
            Log.i(TAG, "Unable to find favourite icon.");
        }
        menuItemFavouriteCount = toolBarBottom.getMenu().findItem(R.id.action_propertyfavouritecount);


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


    }

    private void setPropertyDetails(View view, final Bundle savedInstanceState) {
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

                        property = new Property(
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

                    } else {
                        String result = JSONHandler.getResultAsString(getActivity(), response);
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
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

                // set favourite icon
                setFavouritedIcon(property);
            }
        }).execute();

    }

    private void setFavouritedIcon(Property property) {
        // set isFavourited initially to false as default display.;
        isFavourited = false;
        favouriteArrayList = favouriteCtrl.getUserFavouriteProperties(user);
        for (Favourite favourite : favouriteArrayList) {
            Log.i(TAG, "propertyID(" + property.getPropertyID().toString() +
                    ") == favourite.getPropertyID(" +
                    favourite.getPropertyID().toString() + ") == " +
                    (property.getPropertyID().toString().equals(favourite.getPropertyID().toString())));
            // display favourite or un-favourite
            if (property.getPropertyID().toString().equals(favourite.getPropertyID().toString())) {
                isFavourited = true;
                this.favourite = favourite;
                menuItemFavourite.setIcon(R.drawable.ic_action_favourite);
                break;
            }
        }
        // display favourite count
        favouriteCtrl.serverGetPropertyFavouriteCount(FragmentPropertyDetails.this, new Favourite(user.getUserID(), property.getPropertyID()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getActivity().startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + property.getOwner().getContact())));
        } else {
            return;
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favourite) {
            Log.i(TAG, TAG_FARVOURITE + " action clicked. isFavourite value was = " + isFavourited);
            if (isFavourited) {
                isFavourited = false;
                Log.i(TAG, "Trying to un-favourite this property. isFavourite value is = " + isFavourited);
                item.setIcon(R.drawable.ic_action_favourite_outline);
                // un-favourite property
                favouriteCtrl.serverDeleteFavouriteProperty(FragmentPropertyDetails.this, favourite);
            } else {
                isFavourited = true;
                Log.i(TAG, "Trying to favourite this property. isFavourite value is = " + isFavourited);
                item.setIcon(R.drawable.ic_action_favourite);
                favourite = new Favourite(user.getUserID(), property.getPropertyID());
                // favourite property
                favouriteCtrl.serverNewFavouriteProperty(FragmentPropertyDetails.this, favourite);
            }
        }
        if (id == R.id.action_phone_call) {
            Toast.makeText(getActivity(), TAG_PHONECALL, LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            Log.i(TAG, TAG_PHONECALL + " action clicked");
        }
        if (id == R.id.action_phone_message) {
            Toast.makeText(getActivity(), TAG_PHONEMESSAGE, LENGTH_SHORT).show();
            Log.i(TAG, TAG_PHONEMESSAGE + " action clicked");
        }
        return false;
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
    public void onDestroyView() {
        Log.w(TAG, "onDestroyView");
        super.onDestroyView();
        toolBarBottom.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }
}
