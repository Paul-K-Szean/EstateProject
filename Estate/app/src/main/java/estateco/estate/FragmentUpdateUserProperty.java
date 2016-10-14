package estateco.estate;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

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
import enums.FlatType;
import enums.FurnishLevel;
import handler.AsyncTaskResponse;
import handler.BackgroundTaskHandler;
import handler.ErrorHandler;
import handler.ImageHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUpdateUserProperty extends Fragment {
    private static final String TAG = FragmentUserListings.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;
    private Property propertyOld;
    private Sale sale;
    private Lease lease;

    EditText etEditTitle, etEditDesc, etEditPrice, etEditNoOfBedrooms, etEditNoOfBathrooms, etEditFloorArea,
            etEditAddressName, etEditPostalCode, etEditUnit;
    Button btnSave;
    ImageView imgvPreview;
    Spinner spEditFurnishLevel, spEditFlatType;
    CheckBox chkbxEditWholeApartment;

    public FragmentUpdateUserProperty() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_update_user_property, container, false);
        setControls(view);
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
            // TODO REDIRECT USER BACK TO LOGIN SCREEN FOR ALL ACTIVITY/FRAGMENT
            // getActivity().finish();
        }


        // get user propertyOld detail
        savedInstanceState = getArguments();
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, savedInstanceState.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID));
        new BackgroundTaskHandler(Request.Method.POST, EstateConfig.URL_PROPERTYDETAILS, paramValues, getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONObject propertyObj = jsonObj.getJSONObject("property");
                    propertyOld = new Property(
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

                    spEditFlatType.setSelection(2);
                    // et.setText(property.getDealType());

                    etEditTitle.setText(propertyOld.getTitle());
                    etEditDesc.setText(propertyOld.getDescription());

                    spEditFurnishLevel.setSelection(3);
                    etEditPrice.setText("SGD$" + propertyOld.getPrice());
                    etEditPostalCode.setText("S'pore(" + propertyOld.getPostalcode() + ")");
                    etEditUnit.setText("#" + propertyOld.getUnit());
                    etEditAddressName.setText(propertyOld.getAddressName());
                    etEditNoOfBedrooms.setText(propertyOld.getNoOfbedrooms() + " bedroom(s)");
                    etEditNoOfBathrooms.setText(propertyOld.getNoOfbathrooms() + " bathroom(s)");

                    // TODO WHOLEAPARTMENT, DEAL TYPE
                    if (propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE).equals(DealType.ForSale)) {
                        lease = new Lease(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT));
                    } else {
                        sale = new Sale(propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA));
                        etEditFloorArea.setText(sale.getFloorArea() + "Sq Meters");
                        chkbxEditWholeApartment.setChecked(true);
                        chkbxEditWholeApartment.setEnabled(false);
                    }


                    // check if photo is empty
                    String photoData = propertyOld.getPhoto();
                    if (photoData.isEmpty())
                        imgvPreview.setImageResource(R.drawable.ic_menu_camera);
                    else
                        imgvPreview.setImageBitmap(ImageHandler.getInstance().decodeStringToImage(photoData));


                } catch (JSONException error) {
                    ErrorHandler.errorHandler(getActivity(), error);
                }
            }
        }).execute();

        return view;
    }

    private void setControls(View view) {

        etEditTitle = (EditText) view.findViewById(R.id.ETEditTitle);
        etEditDesc = (EditText) view.findViewById(R.id.ETEditDesc);
        etEditPrice = (EditText) view.findViewById(R.id.ETEditPrice);
        etEditAddressName = (EditText) view.findViewById(R.id.ETEditAddressName);
        etEditPostalCode = (EditText) view.findViewById(R.id.ETEditPostalCode);
        etEditUnit = (EditText) view.findViewById(R.id.ETEditUnit);
        etEditNoOfBedrooms = (EditText) view.findViewById(R.id.ETEditBedRooms);
        etEditNoOfBathrooms = (EditText) view.findViewById(R.id.ETEditBathrooms);
        etEditFloorArea = (EditText) view.findViewById(R.id.ETEditFloorArea);
        chkbxEditWholeApartment = (CheckBox) view.findViewById(R.id.CHKBXEditWholeApartment);

        imgvPreview = (ImageView) view.findViewById(R.id.IMGVPreview);

        spEditFurnishLevel = (Spinner) view.findViewById(R.id.SPEditFurnishLevel);
        spEditFurnishLevel.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FurnishLevel.values()));
        spEditFlatType = (Spinner) view.findViewById(R.id.SPEditFlatType);
        spEditFlatType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FlatType.values()));

        btnSave = (Button) view.findViewById(R.id.BTNUpdateProperty);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEditTitle.setText("TESTING");
            }
        });


    }
}
