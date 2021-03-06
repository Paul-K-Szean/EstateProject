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
import android.text.InputFilter;
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
import java.util.concurrent.ExecutionException;

import controllers.EstateCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Entity_GovData_ResaleFlat;
import entities.Property;
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
import handler.ImageHandler_DECODE;
import handler.ImageHandler_ENCODE;
import handler.JSONHandler;
import handler.Utility;
import tabs.SlidingTabLayout;

import static android.view.View.GONE;
import static controllers.EstateConfig.URL_GOVDATA_RESALEFLATPRICES;
import static controllers.PropertyCtrl.KEY_PROPERTY_ROOM;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUpdateUserProperty extends Fragment implements AlertDialogResponse {
    private static final String TAG = FragmentUpdateUserProperty.class.getSimpleName();
    private static final int SELECTED_IMAGE = 1;
    Bitmap bitmap;
    Button btnEditPropertySave, btnEditPhoto, btnEditRandom;
    CheckBox chkbxEditWholeApartment;
    EditText etEditTitle, etEditDesc, etEditBlock, etEditStreetName, etEditPrice, etEditFloorArea;
    ImageView imgvEditImage;
    Map<String, String> paramValues = new HashMap<>();
    Spinner spEditDealType, spEditFloorLevel, spEditFlatType, spEditFurnishLevel, spEditBedroomCount, spEditBathroomCount, spEditStatus;
    String selectedImagePath;
    String valEditPropertyID, valEditFlatType, valEditBlock, valEditStreetName, valEditFloorLevel,
            valEditFloorArea, valEditPrice, valEditImage, valEditStatus,
            valEditDealType, valEditTitle, valEditDesc, valEditFurnishLevel, valEditBedroomCount,
            valEditBathroomCount, valEditFavouriteCount, valEditViewCount, valEditWholeApartment, valEditCreatedDate;
    TextView tvLblCreatedDate, tvLblFloorArea;
    Toolbar toolbar;
    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;
    private Property propertyLocalDB;
    private Property property;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    public FragmentUpdateUserProperty() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView()");
        Utility.hideSoftKeyboard(getActivity());
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
        toolbar.setSubtitle("Update Property");

        // check box
        chkbxEditWholeApartment = (CheckBox) view.findViewById(R.id.CHKBXEditWholeApartment);
        chkbxEditWholeApartment.setChecked(false);
        chkbxEditWholeApartment.setEnabled(false);

        // edit texts
        etEditTitle = (EditText) view.findViewById(R.id.ETEditTitle);
        etEditDesc = (EditText) view.findViewById(R.id.ETEditDesc);
        etEditBlock = (EditText) view.findViewById(R.id.ETEditBlock);
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
                    chkbxEditWholeApartment.setText("Leasing Wholeapartment?");
                }
                if (spEditDealType.getSelectedItem().equals(DealType.ForSale)) {
                    chkbxEditWholeApartment.setChecked(true);
                    chkbxEditWholeApartment.setEnabled(false);
                    chkbxEditWholeApartment.setText("Selling wholeapartment?");
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
                                                       valEditFlatType = spEditFlatType.getSelectedItem().toString().trim();
                                                       valEditPrice = etEditPrice.getText().toString().trim();
                                                       valEditBlock = etEditBlock.getText().toString().trim();
                                                       valEditStreetName = etEditStreetName.getText().toString().trim();
                                                       valEditFloorLevel = spEditFloorLevel.getSelectedItem().toString().trim();
                                                       valEditFloorArea = (etEditFloorArea.getText().toString()).isEmpty() ? "" : etEditFloorArea.getText().toString();
                                                       valEditPrice = etEditPrice.getText().toString().trim();

                                                       System.gc();
                                                       // image details
                                                       try {

                                                           if (bitmap != null) {
                                                               valEditImage = new ImageHandler_ENCODE(getActivity(), bitmap).execute().get();
                                                           } else {
                                                               imgvEditImage.buildDrawingCache();
                                                               bitmap = imgvEditImage.getDrawingCache();
                                                               valEditImage = new ImageHandler_ENCODE(getActivity(), bitmap).execute().get();
                                                           }

//                                                           System.gc();// clear garbage
//                                                           if (bitmap != null)
//                                                               valEditImage = ImageHandler_ENCODE.encodeImagetoString(bitmap);
//                                                           else {
//                                                               imgvEditImage.buildDrawingCache();
//                                                               bitmap = imgvEditImage.getDrawingCache();
//                                                               valEditImage = ImageHandler_ENCODE.encodeImagetoString(bitmap);
//                                                           }

                                                       } catch (InterruptedException e) {
                                                           e.printStackTrace();
                                                       } catch (ExecutionException e) {
                                                           e.printStackTrace();
                                                       }


                                                       valEditStatus = spEditStatus.getSelectedItem().toString().trim();
                                                       valEditDealType = spEditDealType.getSelectedItem().toString().trim();
                                                       valEditTitle = etEditTitle.getText().toString().trim();
                                                       valEditDesc = etEditDesc.getText().toString().trim();
                                                       valEditFurnishLevel = spEditFurnishLevel.getSelectedItem().toString().trim();
                                                       valEditBedroomCount = spEditBedroomCount.getSelectedItem().toString().trim();
                                                       valEditBathroomCount = spEditBathroomCount.getSelectedItem().toString().trim();
                                                       valEditFavouriteCount =
                                                               valEditWholeApartment = chkbxEditWholeApartment.isChecked() ? PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT : KEY_PROPERTY_ROOM;
                                                       valEditCreatedDate = tvLblCreatedDate.getText().toString().trim();


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
                                                                     Toast.makeText(getActivity(), "Location at (" + location + ").", Toast.LENGTH_SHORT).show();
                                                                     int resultArrSize = jsonArray.length();
                                                                     int randomIndex = Utility.generateNumber(0, resultArrSize);
                                                                     JSONObject jsonRandomObject = (JSONObject) jsonArray.get(randomIndex);
                                                                     Log.i(TAG, jsonArray.get(randomIndex).toString());
                                                                     Log.i(TAG, jsonRandomObject.getString("flat_type").toString());

                                                                     Entity_GovData_ResaleFlat entityGovDataResaleFlat = new Entity_GovData_ResaleFlat(
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
                                                                     etEditBlock.setText(entityGovDataResaleFlat.getBlock());
                                                                     spEditFloorLevel.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFloorLevel, entityGovDataResaleFlat.getStorey_range()));
                                                                     etEditStreetName.setText(entityGovDataResaleFlat.getStreet_name());
                                                                     // house details
                                                                     spEditFlatType.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFlatType, entityGovDataResaleFlat.getFlat_type()));
                                                                     etEditFloorArea.setText(entityGovDataResaleFlat.getFloor_area_sqm());
                                                                     etEditPrice.setText(propertyCtrl.calculatePropertyPrice(valEditDealType, valEditWholeApartment, valEditPrice));
                                                                     // etEditPrice.setText(entityGovDataResaleFlat.getResale_price());
                                                                     spEditFurnishLevel.setSelection(Utility.generateNumber(1, spEditFurnishLevel.getCount() - 1));
                                                                     spEditBedroomCount.setSelection(Utility.generateNumber(1, spEditBedroomCount.getCount() - 1));
                                                                     spEditBathroomCount.setSelection(Utility.generateNumber(1, spEditBathroomCount.getCount() - 1));

                                                                 }
                                                                 btnEditRandom.setText("Location : " + location);
                                                             } else {
                                                                 btnEditRandom.setText("No data for (" + location + ") from gov data.");
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
        etEditBlock.setText(propertyLocalDB.getBlock());
        spEditFloorLevel.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFloorLevel, propertyLocalDB.getFloorlevel()));
        etEditStreetName.setText(propertyLocalDB.getStreetname());
        etEditStreetName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        // house details
        spEditFlatType.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFlatType, propertyLocalDB.getFlatType()));
        etEditPrice.setText(propertyLocalDB.getPrice());
        etEditFloorArea.setText(propertyLocalDB.getFloorarea().toString());
        spEditFurnishLevel.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFurnishLevel, propertyLocalDB.getFurnishLevel()));
        spEditBedroomCount.setSelection(EstateCtrl.getSpinnerItemPosition(spEditBedroomCount, propertyLocalDB.getBedroomcount()));
        spEditBathroomCount.setSelection(EstateCtrl.getSpinnerItemPosition(spEditBathroomCount, propertyLocalDB.getBathroomcount()));

        // check if photo is empty
        String imageData = propertyLocalDB.getImage();
        Bitmap bitmap = FragmentMainListings.getBitmapFromCache(propertyLocalDB.getPropertyID());
        if (imageData.isEmpty())
            imgvEditImage.setImageResource(R.drawable.ic_menu_camera);
        else {
            if (bitmap != null) {
                imgvEditImage.setImageBitmap(bitmap);
            } else {
                new ImageHandler_DECODE(imgvEditImage).execute(imageData, propertyLocalDB.getPropertyID());
            }
        }


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
                bitmap = null;
                System.gc();
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }


    // alert dialog response
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        propertyCtrl.serverUpdateUserProperty(FragmentUpdateUserProperty.this, property);

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
