package estateco.estate;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import controllers.PhoneCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Property;
import entities.User;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.ImageHandler_DECODE;
import handler.JSONHandler;
import handler.Utility;

import static android.graphics.Color.RED;
import static controllers.PropertyCtrl.KEY_ACTION_DECREASEFAVOURITE;
import static controllers.PropertyCtrl.KEY_ACTION_INCREASEFAVOURITE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPropertyDetails extends Fragment implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = FragmentPropertyDetails.class.getSimpleName();
    private static final String TAG_FARVOURITE = "favourite";
    private static final String TAG_VIEW = "view";
    private static final String TAG_PHONECALL = "phonecall";
    private static final String TAG_PHONEMESSAGE = "phonemessage";
    static Boolean isFavourited = false;
    ArrayList<Favourite> favouriteArrayList;
    TextView tvPropDetTitle, tvPropDetDesc, tvPropDetFlatType, tvPropDetDealType, tvPropDetFurnishLevel, tvPropDetPrice, tvPropDetBedroomCount,
            tvPropDetBathroomCount, tvPropDetFloorArea, tvPropDetStreetName, tvPropDetFloorLevel, tvPropDetBlock, tvPropDetWholeApartment,
            tvPropDetOwnerName, tvPropDetOwnerEmail, tvPropDetOwnerContact;
    ImageView imgvPropDetImage;
    String valProDetPropertyID, valProDetOwnerID, valProDetOwnerName, valProDetOwnerEmail, valProDetOwnerContact, valProDetFlatType,
            valProDetDealType, valProDetTitle, valProDetDescription, valProDetFurnishLevel, valProDetPrice,
            valProDetBlock, valProDetStreetName, valProDetImage, valProDetStatus, valProDetBedroomCount, valProDetFavouriteCount, valProDetViewCount, valProDetBathroomCount,
            valProDetFloorLevel, valProDetFloorArea, valProDetWholeApartment, valProDetCreatedDate;
    Toolbar toolBarTop, toolBarBottom;
    MenuItem menuItemFavouriteIcon, menuItemFavouriteCount, menuItemViewIcon, menuItemViewCount, menuItemPhoneCall, menuItemPhoneMessage;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;
    private User owner;
    private Property property;
    private Favourite favourite;


    public FragmentPropertyDetails() {
        // Required empty public constructor
        // increase view count

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView()");
        Utility.hideSoftKeyboard(getActivity());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);
        savedInstanceState = getActivity().getIntent().getExtras();

        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());
        propertyCtrl = new PropertyCtrl(getActivity());
        favouriteCtrl = new FavouriteCtrl(getActivity());
        user = userCtrl.getUserDetails();

        // set controls to user property
        if (savedInstanceState != null) {
            setPropertyDetails(view, savedInstanceState);
            setControls(view, savedInstanceState);
        }

        return view;
    }

    private void setControls(View view, Bundle savedInstanceState) {
        toolBarTop = (Toolbar) getActivity().findViewById(R.id.toolBarTopPropertyDetails);
        toolBarTop.setTitle("Estate");
        toolBarTop.setSubtitle("Property Details");

        toolBarBottom = (Toolbar) getActivity().findViewById(R.id.toolBarBottomPropertyDetails);
        toolBarBottom.setOnMenuItemClickListener(this);
        if ((menuItemFavouriteIcon = toolBarBottom.getMenu().findItem(R.id.action_favouriteicon)) != null) {
            menuItemFavouriteIcon.setIcon(R.drawable.ic_action_favourite_outline);
        } else {
            Log.i(TAG, "Unable to find favourite icon.");
        }
        menuItemFavouriteCount = toolBarBottom.getMenu().findItem(R.id.action_favouritecount);

        if ((menuItemViewIcon = toolBarBottom.getMenu().findItem(R.id.action_viewicon)) != null) {
            menuItemViewIcon.setIcon(R.drawable.ic_action_remove_red_eye);
        } else {
            Log.i(TAG, "Unable to find view icon.");
        }
        menuItemViewCount = toolBarBottom.getMenu().findItem(R.id.action_viewcount);
        menuItemPhoneCall = toolBarBottom.getMenu().findItem(R.id.action_phone_call);
        menuItemPhoneMessage = toolBarBottom.getMenu().findItem(R.id.action_phonemessage);
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
        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_GETPROPERTYDETAILS, paramValues, getActivity(), new AsyncTaskResponse() {
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
                                valProDetFavouriteCount = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FAVOURITECOUNT),
                                valProDetViewCount = propertyObj.getString(PropertyCtrl.KEY_PROPERTY_VIEWCOUNT),
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
                menuItemFavouriteCount.setTitle(valProDetFavouriteCount);
                menuItemViewCount.setTitle(valProDetViewCount);

                // address details
                tvPropDetFloorLevel.setText("Level " + valProDetFloorLevel);
                tvPropDetStreetName.setText(valProDetStreetName);
                // house details
                System.gc();
                String imageData = valProDetImage;
                Bitmap bitmap = FragmentMainListings.getBitmapFromCache(valProDetPropertyID);
                if (imageData.isEmpty())
                    imgvPropDetImage.setImageResource(R.drawable.ic_menu_camera);
                else {
                    if (bitmap != null) {
                        imgvPropDetImage.setImageBitmap(bitmap);
                    } else {
                        new ImageHandler_DECODE(imgvPropDetImage).execute(imageData, property.getPropertyID());
                    }
                }

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


                // increase view count
                favouriteCtrl.serverUpdateViewCount(FragmentPropertyDetails.this,
                        property, menuItemViewCount);

                //
                if (valProDetStatus.equals("closed")) {
                    menuItemPhoneCall.setEnabled(false);
                    menuItemPhoneMessage.setEnabled(false);
                    tvPropDetDealType.setText(valProDetDealType + " - " + valProDetStatus);
                    tvPropDetOwnerContact.setText(valProDetStatus);
                    tvPropDetOwnerEmail.setText(valProDetStatus);
                    tvPropDetDealType.setTextColor(RED);
                    tvPropDetOwnerContact.setTextColor(RED);
                    tvPropDetOwnerEmail.setTextColor(RED);
                }

                if (valProDetOwnerID.equals(user.getUserID())) {
                    menuItemPhoneMessage.setEnabled(false);
                    menuItemPhoneMessage.setEnabled(false);
                }
            }
        }).execute();

    }

    private void setFavouritedIcon(Property property) {
        // set isFavourited initially to false as default display;
        isFavourited = false;
        Favourite favourite = favouriteCtrl.getUserFavouritePropertyDetails(user, property);
        // check favourite list from local db and show icon accordingly
        if (favourite != null) {
            Log.i(TAG, "This property " + favourite.getProperty().getPropertyID() + " is favourite by user " + favourite.getOwner().getUserID());
            isFavourited = true;
            menuItemFavouriteIcon.setIcon(R.drawable.ic_action_favourite);
        } else {
            Log.i(TAG, "This property " + property.getPropertyID() + " is not favourite by user " + user.getUserID());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        // loop through all permission requested
//        for (String permission : permissions) {
//            // android.permission.CALL_PHONE
//            if (permission.equalsIgnoreCase("android.permission.CALL_PHONE")) {
//                // if permission granted
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                }
//            }
//        }

        switch (requestCode) {
            case 1: {
                // android.permission.CALL_PHONE
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // start calling
                    new PhoneCtrl().makeCall(FragmentPropertyDetails.this, property.getOwner().getContact());
                }
                return;
            }
            case 2: {
                // android.permission.SEND_SMS
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // send message
                    new PhoneCtrl().sendMessage(FragmentPropertyDetails.this, property.getOwner().getContact(), property.getTitle());
                }
                return;
            }
            default: {
                return;
            }
        }


    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favouriteicon) {
            Log.i(TAG, TAG_FARVOURITE + " action clicked. isFavourite value was = " + isFavourited);
            if (isFavourited) {
                isFavourited = false;
                Log.i(TAG, "Trying to un-favourite this property. isFavourite value is = " + isFavourited);
                item.setIcon(R.drawable.ic_action_favourite_outline);
                // decrease favourite count of this property
                favouriteCtrl.serverUpdateFavouriteCount(FragmentPropertyDetails.this, property, KEY_ACTION_DECREASEFAVOURITE,
                        menuItemFavouriteCount);

            } else {
                isFavourited = true;
                Log.i(TAG, "Trying to favourite this property. isFavourite value is = " + isFavourited);
                item.setIcon(R.drawable.ic_action_favourite);
                // increase favourite count of this property
                favouriteCtrl.serverUpdateFavouriteCount(FragmentPropertyDetails.this, property, KEY_ACTION_INCREASEFAVOURITE,
                        menuItemFavouriteCount);
            }
        }
        if (id == R.id.action_phone_call) {
            Log.i(TAG, TAG_PHONECALL + " action clicked");
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        if (id == R.id.action_phonemessage) {
            Log.i(TAG, TAG_PHONEMESSAGE + " action clicked");
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 2);
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

    }

    @Override
    public void onPause() {
        Log.w(TAG, "onPause");
        super.onPause();

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
        if (toolBarBottom != null)
            toolBarBottom.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }
}
