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
import android.support.v4.app.Fragment;
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
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.ImageHandler;
import handler.JSONHandler;
import handler.Utility;

import static controllers.EstateConfig.URL_GOVDATA_RESALEFLATPRICES;
import static controllers.PropertyCtrl.KEY_PROPERTY_ROOM;
import static controllers.PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNewProperty extends Fragment {
    private static final String TAG = FragmentNewProperty.class.getSimpleName();

    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private User user;
    private Property property;
    Toolbar toolBarTop;

    public FragmentNewProperty() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_property, container, false);


        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());
        propertyCtrl = new PropertyCtrl(getActivity());
        user = userCtrl.getUserDetails();

        setControls(view);
        return view;
    }


    private static final int SELECTED_IMAGE = 1;
    Bitmap bitmap;
    Button btnCreateProperty, btnNewRandom, btnNewImage;
    CheckBox chkbxNewWholeApartment;
    EditText etNewTitle, etNewDesc, etNewStreetName, etNewPrice, etNewFloorArea;
    ImageView imgvNewImage;
    Spinner spNewDealType, spNewFloorLevel, spNewFlatType, spNewFurnishLevel, spNewBedroomCount, spNewBathroomCount;
    String valNewFlatType, valNewBlock, valNewStreetName, valNewFloorLevel, valNewFloorArea, valNewPrice, valNewImage, valNewStatus,
            valNewDealType, valNewTitle, valNewDesc, valNewFurnishLevel, valNewBedroomCount, valNewBathroomCount, valNewFavouriteCount, valNewViewCount, valNewWholeApartment, selectedImagePath;
    TextView tvNewBedroomCount;


    public void setControls(View view) {
        toolBarTop = (Toolbar) getActivity().findViewById(R.id.toolbar_top);
        toolBarTop.setTitle("My Listings");
        toolBarTop.getMenu().findItem(R.id.menu_action_searchQuery).setVisible(false);


        // check box
        chkbxNewWholeApartment = (CheckBox) view.findViewById(R.id.CHKBXNewWholeApartment);
        chkbxNewWholeApartment.setChecked(false);
        chkbxNewWholeApartment.setEnabled(false);
        // edit texts
        etNewTitle = (EditText) view.findViewById(R.id.ETNewTitle);
        etNewDesc = (EditText) view.findViewById(R.id.ETNewDesc);
        etNewStreetName = (EditText) view.findViewById(R.id.ETNewStreetName);
        etNewPrice = (EditText) view.findViewById(R.id.ETNewPrice);
        etNewFloorArea = (EditText) view.findViewById(R.id.ETNewFloorArea);
        // text view
        tvNewBedroomCount = (TextView) view.findViewById(R.id.TVNewBedroomCount);
        // image view
        imgvNewImage = (ImageView) view.findViewById(R.id.IMGVNewImage);
        imgvNewImage.setImageResource(R.drawable.ic_menu_camera);

        //spinners
        spNewDealType = (Spinner) view.findViewById(R.id.SPNewDealType);
        spNewDealType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, DealType.values()));
        spNewDealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spNewDealType.getSelectedItem().equals(DealType.ForLease)) {
                    chkbxNewWholeApartment.setEnabled(true);
                }
                if (spNewDealType.getSelectedItem().equals(DealType.ForSale)) {
                    chkbxNewWholeApartment.setChecked(true);
                    chkbxNewWholeApartment.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chkbxNewWholeApartment.setChecked(false);
                chkbxNewWholeApartment.setEnabled(false);
            }
        });
        spNewFloorLevel = (Spinner) view.findViewById(R.id.SPNewFloorLevel);
        spNewFloorLevel.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FloorLevel.values()));
        spNewFlatType = (Spinner) view.findViewById(R.id.SPNewFlatType);
        spNewFlatType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FlatType.values()));
        spNewFurnishLevel = (Spinner) view.findViewById(R.id.SPNewFurnishLevel);
        spNewFurnishLevel.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FurnishLevel.values()));
        spNewBedroomCount = (Spinner) view.findViewById(R.id.SPNewBedroomCount);
        String[] arrBedRooms = {"Select Number Of Bed Room(s)", "1", "2", "3", "4", "5"};
        spNewBedroomCount.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrBedRooms));
        spNewBathroomCount = (Spinner) view.findViewById(R.id.SPNewBathroomCount);
        String[] arrBathRooms = {"Select Number Of Bath Room(s)", "1", "2", "3"};
        spNewBathroomCount.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrBathRooms));

        // buttons
        btnNewImage = (Button) view.findViewById(R.id.BTNNewImage);
        btnNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        });

        btnNewRandom = (Button) view.findViewById(R.id.BTNNewRandom);
        btnNewRandom.setOnClickListener(new View.OnClickListener() {
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
                                                                    int randomIndex = Utility.generateNumber(0, resultArrSize - 1);
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
                                                                    spNewDealType.setSelection(Utility.generateNumber(1, spNewDealType.getCount() - 1));
                                                                    // for sale
                                                                    if (spNewDealType.getSelectedItemPosition() == 1) {
                                                                        chkbxNewWholeApartment.setText("Selling whole apartment");
                                                                        chkbxNewWholeApartment.setChecked(true);
                                                                    }
                                                                    // for lease
                                                                    if (spNewDealType.getSelectedItemPosition() == 2) {
                                                                        chkbxNewWholeApartment.setText("Leasing whole apartment");
                                                                        chkbxNewWholeApartment.setChecked(Utility.generateBool());
                                                                    }
                                                                    // general details
                                                                    etNewTitle.setText(Utility.generateTitle());
                                                                    etNewDesc.setText(Utility.generateDesc());
                                                                    // address details
                                                                    valNewBlock = resaleFlatPrice.getBlock();
                                                                    spNewFloorLevel.setSelection(EstateCtrl.getSpinnerItemPosition(spNewFloorLevel, resaleFlatPrice.getStorey_range()));
                                                                    etNewStreetName.setText(resaleFlatPrice.getStreet_name());
                                                                    // house details
                                                                    spNewFlatType.setSelection(EstateCtrl.getSpinnerItemPosition(spNewFlatType, resaleFlatPrice.getFlat_type()));
                                                                    etNewFloorArea.setText(resaleFlatPrice.getFloor_area_sqm());
                                                                    etNewPrice.setText(resaleFlatPrice.getResale_price());
                                                                    spNewFurnishLevel.setSelection(Utility.generateNumber(1, spNewFurnishLevel.getCount() - 1));
                                                                    spNewBedroomCount.setSelection(Utility.generateNumber(1, spNewBedroomCount.getCount() - 1));
                                                                    spNewBathroomCount.setSelection(Utility.generateNumber(1, spNewBathroomCount.getCount() - 1));
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

        btnCreateProperty = (Button) view.findViewById(R.id.BTNCreateProperty);
        btnCreateProperty.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override
                                                 public void onClick(View v) {
                                                     // deal details
                                                     valNewDealType = spNewDealType.getSelectedItem().toString();
                                                     valNewWholeApartment = chkbxNewWholeApartment.isChecked() ? KEY_PROPERTY_WHOLEAPARTMENT : KEY_PROPERTY_ROOM;
                                                     // general details
                                                     valNewTitle = etNewTitle.getText().toString();
                                                     valNewDesc = etNewDesc.getText().toString();
                                                     // address details
                                                     valNewFloorLevel = spNewFloorLevel.getSelectedItem().toString();
                                                     valNewStreetName = etNewStreetName.getText().toString();
                                                     // house details
                                                     valNewFlatType = spNewFlatType.getSelectedItem().toString();
                                                     valNewPrice = etNewPrice.getText().toString();
                                                     valNewFloorArea = etNewFloorArea.getText().toString();
                                                     valNewFurnishLevel = spNewFurnishLevel.getSelectedItem().toString();
                                                     valNewBedroomCount = spNewBedroomCount.getSelectedItem().toString();
                                                     valNewBathroomCount = spNewBathroomCount.getSelectedItem().toString();
                                                     // image details
                                                     if (bitmap != null)
                                                         valNewImage = ImageHandler.encodeImagetoString(bitmap);
                                                     else {
                                                         imgvNewImage.buildDrawingCache();
                                                         bitmap = imgvNewImage.getDrawingCache();
                                                         // bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_menu_camera);
                                                         valNewImage = ImageHandler.encodeImagetoString(bitmap);
                                                     }

                                                     if (valNewDealType.isEmpty() ||
                                                             valNewWholeApartment.isEmpty() ||
                                                             valNewTitle.isEmpty() ||
                                                             valNewDesc.isEmpty() ||
                                                             valNewBlock.isEmpty() ||
                                                             valNewFloorLevel.isEmpty() ||
                                                             valNewStreetName.isEmpty() ||
                                                             valNewFlatType.isEmpty() ||
                                                             valNewPrice.isEmpty() ||
                                                             valNewFloorArea.isEmpty() ||
                                                             valNewFurnishLevel.isEmpty() ||
                                                             valNewBedroomCount.isEmpty() ||
                                                             valNewBathroomCount.isEmpty()
                                                             ) {
                                                         // empty fields
                                                         if (spNewDealType.getSelectedItemId() == 0)
                                                             ((TextView) spNewDealType.getSelectedView()).setError("Required field!");
                                                         if (spNewFloorLevel.getSelectedItemId() == 0)
                                                             ((TextView) spNewFloorLevel.getSelectedView()).setError("Required field!");
                                                         if (spNewFlatType.getSelectedItemId() == 0)
                                                             ((TextView) spNewFlatType.getSelectedView()).setError("Required field!");
                                                         if (spNewFurnishLevel.getSelectedItemId() == 0)
                                                             ((TextView) spNewFurnishLevel.getSelectedView()).setError("Required field!");
                                                         if (spNewBedroomCount.getSelectedItemId() == 0)
                                                             ((TextView) spNewBedroomCount.getSelectedView()).setError("Required field!");
                                                         if (spNewBathroomCount.getSelectedItemId() == 0)
                                                             ((TextView) spNewBathroomCount.getSelectedView()).setError("Required field!");

                                                         if (etNewTitle.getText().toString().isEmpty())
                                                             etNewTitle.setError("Required field!");
                                                         if (etNewDesc.getText().toString().isEmpty())
                                                             etNewDesc.setError("Required field!");
                                                         if (etNewStreetName.getText().toString().isEmpty())
                                                             etNewStreetName.setError("Required field!");
                                                         if (etNewPrice.getText().toString().isEmpty())
                                                             etNewPrice.setError("Required field!");
                                                         if (etNewFloorArea.getText().toString().isEmpty())
                                                             etNewFloorArea.setError("Required field!");
                                                     } else {
                                                         valNewStatus = "open";
                                                         valNewFavouriteCount = valNewViewCount = "0";
                                                         if (valNewDealType.equals(DealType.ForLease.toString()) && valNewWholeApartment.equals(KEY_PROPERTY_ROOM))
                                                             valNewPrice = String.valueOf((Double.valueOf(valNewPrice)) / 1000);

                                                         // create property to server
                                                         property = new Property(
                                                                 user,
                                                                 valNewFlatType,
                                                                 valNewBlock,
                                                                 valNewStreetName,
                                                                 valNewFloorLevel,
                                                                 valNewFloorArea,
                                                                 valNewPrice,
                                                                 valNewImage,
                                                                 valNewStatus,
                                                                 valNewDealType,
                                                                 valNewTitle,
                                                                 valNewDesc,
                                                                 valNewFurnishLevel,
                                                                 valNewBedroomCount,
                                                                 valNewBathroomCount,
                                                                 valNewFavouriteCount,
                                                                 valNewViewCount,
                                                                 valNewWholeApartment);

                                                         propertyCtrl.serverNewProperty(FragmentNewProperty.this, property, user);
                                                     }
                                                 }
                                             }

        );

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
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
                imgvNewImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
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
