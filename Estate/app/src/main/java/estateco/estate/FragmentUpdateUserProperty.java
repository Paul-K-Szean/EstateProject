package estateco.estate;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import controllers.EstateCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Property;
import entities.ResaleFlatPrice;
import entities.User;
import enums.DealType;
import enums.FlatType;
import enums.FloorLevel;
import enums.FurnishLevel;
import enums.PropertyStatus;
import handler.AlertDialogHandler;
import handler.AlertDialogResponse;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.ImageHandler;
import handler.JSONHandler;
import handler.Utility;
import tabs.SlidingTabLayout;

import static android.view.View.GONE;
import static controllers.EstateConfig.URL_GOVDATA_RESALEFLATPRICES;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUpdateUserProperty extends Fragment implements AlertDialogResponse {
    private static final String TAG = FragmentUpdateUserProperty.class.getSimpleName();

    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;
    private Property propertyLocalDB;
    private Property property;
    private static final int SELECTED_IMAGE = 1;
    Bitmap bitmap;
    Button btnEditPropertySave, btnEditPhoto, btnEditRandom;
    CheckBox chkbxEditWholeApartment;
    EditText etEditTitle, etEditDesc, etEditStreetName, etEditPrice, etEditFloorArea;
    ImageView imgvEditImage;
    Map<String, String> paramValues = new HashMap<>();
    Spinner spEditDealType, spEditFloorLevel, spEditFlatType, spEditFurnishLevel, spEditBedroomCount, spEditBathroomCount, spEditStatus;
    String selectedImagePath;

    String valEditPropertyID, valEditFlatType, valEditBlock = "000", valEditStreetName, valEditFloorLevel,
            valEditFloorArea, valEditPrice, valEditImage, valEditStatus,
            valEditDealType, valEditTitle, valEditDesc, valEditFurnishLevel, valEditBedroomCount,
            valEditBathroomCount, valEditFavouriteCount, valEditViewCount, valEditWholeApartment, valEditCreatedDate;
    TextView tvLblCreatedDate, tvLblFloorArea;
    Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    public FragmentUpdateUserProperty() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_user_property, container, false);
        savedInstanceState = getArguments();

        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());
        propertyCtrl = new PropertyCtrl(getActivity());
        user = userCtrl.getUserDetails();

        viewPager = (ViewPager) getActivity().findViewById(R.id.ViewPagerMain);
        viewPager.setVisibility(GONE);
        slidingTabLayout = (SlidingTabLayout) getActivity().findViewById(R.id.TabLayoutMain);
        slidingTabLayout.setVisibility(GONE);

        setControls(view, savedInstanceState);
        // set controls to user property
        setUserData(view, savedInstanceState);
        return view;
    }

    private void setControls(View view, final Bundle savedInstanceState) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_top);
        toolbar.setTitle("Update Property");

        // check box
        chkbxEditWholeApartment = (CheckBox) view.findViewById(R.id.CHKBXEditWholeApartment);
        chkbxEditWholeApartment.setChecked(false);
        chkbxEditWholeApartment.setEnabled(false);
        // edit texts
        etEditTitle = (EditText) view.findViewById(R.id.ETEditTitle);
        etEditDesc = (EditText) view.findViewById(R.id.ETEditDesc);
        etEditStreetName = (EditText) view.findViewById(R.id.ETEditStreetName);
        etEditPrice = (EditText) view.findViewById(R.id.ETEditPrice);
        etEditFloorArea = (EditText) view.findViewById(R.id.ETEditFloorArea);
        // text view
        tvLblCreatedDate = (TextView) view.findViewById(R.id.TVLblCreatedDate);
        tvLblFloorArea = (TextView) view.findViewById(R.id.TVLblFloorArea);
        // image view
        imgvEditImage = (ImageView) view.findViewById(R.id.IMGVEditImage);

        // spinners
        spEditDealType = (Spinner) view.findViewById(R.id.SPEditDealType);
        spEditDealType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, DealType.values()));
        spEditDealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spEditDealType.getSelectedItem().equals(DealType.ForLease)) {
                    chkbxEditWholeApartment.setEnabled(true);
                }
                if (spEditDealType.getSelectedItem().equals(DealType.ForSale)) {
                    chkbxEditWholeApartment.setChecked(true);
                    chkbxEditWholeApartment.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chkbxEditWholeApartment.setChecked(false);
                chkbxEditWholeApartment.setEnabled(false);
            }
        });
        spEditFloorLevel = (Spinner) view.findViewById(R.id.SPEditFloorLevel);
        spEditFloorLevel.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FloorLevel.values()));
        spEditFlatType = (Spinner) view.findViewById(R.id.SPEditFlatType);
        spEditFlatType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FlatType.values()));
        spEditFurnishLevel = (Spinner) view.findViewById(R.id.SPEditFurnishLevel);
        spEditFurnishLevel.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FurnishLevel.values()));
        spEditBedroomCount = (Spinner) view.findViewById(R.id.SPEditBedroomCount);
        String[] arrBedRooms = {"Select Number Of Bed Room(s)", "1", "2", "3", "4", "5"};
        spEditBedroomCount.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrBedRooms));
        spEditBathroomCount = (Spinner) view.findViewById(R.id.SPEditBathroomCount);
        String[] arrBathRooms = {"Select Number Of Bath Room(s)", "1", "2", "3"};
        spEditBathroomCount.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrBathRooms));
        spEditStatus = (Spinner) view.findViewById(R.id.SPEditStatus);
        spEditStatus.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, PropertyStatus.values()));

        // buttons
        btnEditPhoto = (Button) view.findViewById(R.id.BTNEditPhoto);
        btnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ask for user permission to access gallery
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        });

        btnEditPropertySave = (Button) view.findViewById(R.id.BTNEditPropertySave);
        btnEditPropertySave.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       // assign to valString
                                                       valEditPropertyID = savedInstanceState.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID);
                                                       valEditFlatType = spEditFlatType.getSelectedItem().toString();
                                                       valEditPrice = etEditPrice.getText().toString();
                                                       valEditStreetName = etEditStreetName.getText().toString();
                                                       valEditFloorLevel = spEditFloorLevel.getSelectedItem().toString();
                                                       valEditFloorArea = (etEditFloorArea.getText().toString()).isEmpty() ? "" : etEditFloorArea.getText().toString();
                                                       valEditPrice = etEditPrice.getText().toString();
                                                       if (bitmap != null)
                                                           valEditImage = ImageHandler.encodeImagetoString(bitmap);
                                                       else {
                                                           imgvEditImage.buildDrawingCache();
                                                           bitmap = imgvEditImage.getDrawingCache();
                                                           // bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_menu_camera);
                                                           valEditImage = ImageHandler.encodeImagetoString(bitmap);
                                                       }
                                                       valEditStatus = spEditStatus.getSelectedItem().toString();
                                                       valEditDealType = spEditDealType.getSelectedItem().toString();
                                                       valEditTitle = etEditTitle.getText().toString();
                                                       valEditDesc = etEditDesc.getText().toString();
                                                       valEditFurnishLevel = spEditFurnishLevel.getSelectedItem().toString();
                                                       valEditBedroomCount = spEditBedroomCount.getSelectedItem().toString();
                                                       valEditBathroomCount = spEditBathroomCount.getSelectedItem().toString();
                                                       valEditFavouriteCount =
                                                               valEditWholeApartment = chkbxEditWholeApartment.isChecked() ? PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT : PropertyCtrl.KEY_PROPERTY_ROOM;
                                                       valEditCreatedDate = tvLblCreatedDate.getText().toString();


                                                       // check for empty fields
                                                       if (spEditFlatType.getSelectedItemId() != 0 &&
                                                               spEditFloorLevel.getSelectedItemId() != 0 &&
                                                               spEditStatus.getSelectedItemId() != 0 &&
                                                               spEditDealType.getSelectedItemId() != 0 &&
                                                               spEditFurnishLevel.getSelectedItemId() != 0 &&
                                                               spEditBedroomCount.getSelectedItemId() != 0 &&
                                                               spEditBathroomCount.getSelectedItemId() != 0 &&
                                                               !valEditBlock.isEmpty() &&
                                                               !valEditStreetName.isEmpty() &&
                                                               !valEditFloorArea.isEmpty() &&
                                                               !valEditPrice.isEmpty() &&
                                                               !valEditTitle.isEmpty() &&
                                                               !valEditDesc.isEmpty()) {


                                                           property = new Property(
                                                                   valEditPropertyID,
                                                                   user,
                                                                   valEditFlatType,
                                                                   valEditBlock,
                                                                   valEditStreetName,
                                                                   valEditFloorLevel,
                                                                   valEditFloorArea,
                                                                   valEditPrice,
                                                                   valEditImage,
                                                                   valEditStatus,
                                                                   valEditDealType,
                                                                   valEditTitle,
                                                                   valEditDesc,
                                                                   valEditFurnishLevel,
                                                                   valEditBedroomCount,
                                                                   valEditBathroomCount,
                                                                   valEditWholeApartment);


                                                           Log.i(TAG, valEditFloorLevel);
                                                           // confirm action by user
                                                           // yes  = save to local DB first, then to server
                                                           AlertDialogHandler.showMyDialog(FragmentUpdateUserProperty.this);

                                                       } else {
                                                           Toast.makeText(getActivity(), "Please fill in all required fields!", Toast.LENGTH_LONG);
                                                           // empty fields
                                                           if (spEditFlatType.getSelectedItemId() == 0)
                                                               ((TextView) spEditFlatType.getSelectedView()).setError("Required field!");
                                                           if (spEditFloorLevel.getSelectedItemId() == 0)
                                                               ((TextView) spEditFloorLevel.getSelectedView()).setError("Required field!");
                                                           if (spEditStatus.getSelectedItemId() == 0)
                                                               ((TextView) spEditStatus.getSelectedView()).setError("Required field!");
                                                           if (spEditDealType.getSelectedItemId() == 0)
                                                               ((TextView) spEditDealType.getSelectedView()).setError("Required field!");
                                                           if (spEditFurnishLevel.getSelectedItemId() == 0)
                                                               ((TextView) spEditFurnishLevel.getSelectedView()).setError("Required field!");
                                                           if (spEditBedroomCount.getSelectedItemId() == 0)
                                                               ((TextView) spEditBedroomCount.getSelectedView()).setError("Required field!");
                                                           if (spEditBathroomCount.getSelectedItemId() == 0)
                                                               ((TextView) spEditBathroomCount.getSelectedView()).setError("Required field!");
                                                           if (valEditStreetName.isEmpty())
                                                               etEditStreetName.setError("Required field!");
                                                           if (valEditTitle.isEmpty())
                                                               etEditTitle.setError("Required field!");
                                                           if (valEditFloorArea.isEmpty())
                                                               etEditFloorArea.setError("Required field!");
                                                           if (valEditPrice.isEmpty())
                                                               etEditPrice.setError("Required field!");
                                                           if (valEditDesc.isEmpty())
                                                               etEditDesc.setError("Required field!");
                                                       }
                                                   }
                                               }

        );

        btnEditRandom = (Button) view.findViewById(R.id.BTNEditRandom);
        btnEditRandom.setOnClickListener(new View.OnClickListener() {

                                             @Override
                                             public void onClick(View v) {
                                                 final String location = new Utility().generateLocation();
                                                 new AsyncTaskHandler(Request.Method.GET, URL_GOVDATA_RESALEFLATPRICES + "&q=" + location, null, getActivity(), new AsyncTaskResponse() {
                                                     @Override
                                                     public void onAsyncTaskResponse(String response) {

                                                         try {
                                                             JSONArray jsonArray = JSONHandler.getRecordsAsArray(getActivity(), response);
                                                             if (jsonArray != null && jsonArray.length() > 0) {
                                                                 if (jsonArray instanceof JSONArray) {
                                                                     int resultArrSize = jsonArray.length();
                                                                     int randomIndex = Utility.generateNumber(0, resultArrSize);
                                                                     JSONObject jsonRandomObject = (JSONObject) jsonArray.get(randomIndex);
                                                                     Log.i(TAG, jsonArray.get(randomIndex).toString());
                                                                     Log.i(TAG, jsonRandomObject.getString("flat_type").toString());

                                                                     ResaleFlatPrice resaleFlatPrice = new ResaleFlatPrice(
                                                                             jsonRandomObject.getString("_id"),
                                                                             jsonRandomObject.getString("town"),
                                                                             jsonRandomObject.getString("flat_type"),
                                                                             jsonRandomObject.getString("block"),
                                                                             jsonRandomObject.getString("month"),
                                                                             jsonRandomObject.getString("street_name"),
                                                                             jsonRandomObject.getString("storey_range"),
                                                                             jsonRandomObject.getString("floor_area_sqm"),
                                                                             jsonRandomObject.getString("flat_model"),
                                                                             jsonRandomObject.getString("lease_commence_date"),
                                                                             jsonRandomObject.getString("resale_price"));

                                                                     // deal details
                                                                     spEditDealType.setSelection(Utility.generateNumber(1, spEditDealType.getCount() - 1));
                                                                     // for sale
                                                                     if (spEditDealType.getSelectedItemPosition() == 1) {
                                                                         chkbxEditWholeApartment.setText("Selling whole apartment");
                                                                         chkbxEditWholeApartment.setChecked(true);
                                                                     }
                                                                     // for lease
                                                                     if (spEditDealType.getSelectedItemPosition() == 2) {
                                                                         chkbxEditWholeApartment.setText("Leasing whole apartment");
                                                                         chkbxEditWholeApartment.setChecked(Utility.generateBool());
                                                                     }
                                                                     // general details
                                                                     etEditTitle.setText(Utility.generateTitle());
                                                                     etEditDesc.setText(Utility.generateDesc());
                                                                     // address details
                                                                     valEditBlock = resaleFlatPrice.getBlock();
                                                                     spEditFloorLevel.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFloorLevel, resaleFlatPrice.getStorey_range()));
                                                                     etEditStreetName.setText(resaleFlatPrice.getStreet_name());
                                                                     // house details
                                                                     spEditFlatType.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFlatType, resaleFlatPrice.getFlat_type()));
                                                                     etEditFloorArea.setText(resaleFlatPrice.getFloor_area_sqm());
                                                                     etEditPrice.setText(resaleFlatPrice.getResale_price());
                                                                     spEditFurnishLevel.setSelection(Utility.generateNumber(1, spEditFurnishLevel.getCount() - 1));
                                                                     spEditBedroomCount.setSelection(Utility.generateNumber(1, spEditBedroomCount.getCount() - 1));
                                                                     spEditBathroomCount.setSelection(Utility.generateNumber(1, spEditBathroomCount.getCount() - 1));
                                                                 }
                                                             } else {
                                                                 Toast.makeText(getActivity(), "No data for (" + location + ") from gov data.", Toast.LENGTH_SHORT).show();
                                                             }
                                                         } catch (JSONException error) {
                                                             // JSON error
                                                             ErrorHandler.errorHandler(getActivity(), error);
                                                         }
                                                     }
                                                 }).execute();
                                             }
                                         }

        );
    }

    private void setUserData(View view, final Bundle savedInstanceState) {

        // get user property details from local db
        propertyLocalDB = propertyCtrl.getUserPropertyDetails(savedInstanceState.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID), user);

        // deal type details
        spEditDealType.setSelection(EstateCtrl.getSpinnerItemPosition(spEditDealType, propertyLocalDB.getDealType()));
        chkbxEditWholeApartment.setChecked(propertyLocalDB.getWholeapartment().toString().equals(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT.toString()));

        // general details
        tvLblCreatedDate.setText(propertyLocalDB.getCreateddate());
        spEditStatus.setSelection(EstateCtrl.getSpinnerItemPosition(spEditStatus, propertyLocalDB.getStatus()));
        etEditTitle.setText(propertyLocalDB.getTitle());
        etEditDesc.setText(propertyLocalDB.getDescription());

        // address details

        Log.i(TAG, "Floor Level: " + propertyLocalDB.getFloorlevel());
        spEditFloorLevel.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFloorLevel, propertyLocalDB.getFloorlevel()));
        etEditStreetName.setText(propertyLocalDB.getStreetname());

        // house details
        spEditFlatType.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFlatType, propertyLocalDB.getFlatType()));
        etEditPrice.setText(propertyLocalDB.getPrice());
        etEditFloorArea.setText(propertyLocalDB.getFloorarea().toString());
        spEditFurnishLevel.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFurnishLevel, propertyLocalDB.getFurnishLevel()));
        spEditBedroomCount.setSelection(EstateCtrl.getSpinnerItemPosition(spEditBedroomCount, propertyLocalDB.getBedroomcount()));
        spEditBathroomCount.setSelection(EstateCtrl.getSpinnerItemPosition(spEditBathroomCount, propertyLocalDB.getBathroomcount()));

        // check if photo is empty
        String photoData = propertyLocalDB.getImage();
        if (photoData.isEmpty())
            imgvEditImage.setImageResource(R.drawable.ic_menu_camera);
        else
            imgvEditImage.setImageBitmap(ImageHandler.decodeStringToImage(photoData));

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
        } else {
            return;
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
                imgvEditImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }


    // alert dialog response
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        propertyCtrl.serverUpdateUserProperty(FragmentUpdateUserProperty.this, property, user);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // btnEditPropertySave.setText("Cancelled");
        Toast.makeText(getActivity(), "Update was cancelled.", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }
}
