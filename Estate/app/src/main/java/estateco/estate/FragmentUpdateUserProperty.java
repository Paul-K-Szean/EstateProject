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
import enums.FlatType;
import enums.FurnishLevel;
import enums.PropertyStatus;
import handler.AlertDialogHandler;
import handler.AlertDialogResponse;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.FragmentHandler;
import handler.ImageHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUpdateUserProperty extends Fragment implements AlertDialogResponse {
    private static final String TAG = FragmentUpdateUserProperty.class.getSimpleName();
    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;
    private Property propertyLocalDB;
    private Sale sale;
    private Lease lease;
    private static final int SELECTED_IMAGE = 1;
    Map<String, String> paramValues = new HashMap<>();
    Bitmap bitmap;
    TextView tvLblCreatedDate, tvLblFloorArea;
    EditText etEditTitle, etEditDesc, etEditPrice, etEditAddressName, etEditPostalCode, etEditUnit, etEditFloorArea;
    Button btnEditPropertySave, btnEditPhoto, btnEditRandom;
    ImageView imgvPreview;
    Spinner spEditFurnishLevel, spEditFlatType, spEditDealType, spEditBedrooms, spEditBathrooms, spEditStatus;
    CheckBox chkbxEditWholeApartment;
    String selectedImagePath, valEditPropertyID, valEditFlatType, valEditDealType, valEditTitle, valEditDesc, valEditFurnishLevel,
            valEditPrice, valEditPostalCode, valEditUnit, valEditAddressName, valEditPhoto, valEditStatus, valEditBedrooms,
            valEditBathrooms, valEditFloorArea, valEditWholeApartment, valCreatedDate;
    Toolbar toolbar;

    public FragmentUpdateUserProperty() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_user_property, container, false);
        savedInstanceState = getArguments();
        setControls(view, savedInstanceState);

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
        }


        // get user property detail from local db
        propertyLocalDB = propertyCtrl.getUserPropertyDetails(savedInstanceState.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID), user);
        tvLblCreatedDate.setText(propertyLocalDB.getCreatedate());
        etEditTitle.setText(propertyLocalDB.getTitle());
        etEditDesc.setText(propertyLocalDB.getDescription());
        etEditPrice.setText(propertyLocalDB.getPrice());
        etEditPostalCode.setText(propertyLocalDB.getPostalcode());
        etEditUnit.setText(propertyLocalDB.getUnit());
        etEditAddressName.setText(propertyLocalDB.getAddressName());

        // spinner value
        spEditStatus.setSelection(EstateCtrl.getSpinnerItemPosition(spEditStatus, propertyLocalDB.getStatus()));
        spEditDealType.setSelection(EstateCtrl.getSpinnerItemPosition(spEditDealType, propertyLocalDB.getDealType()));
        spEditFlatType.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFlatType, propertyLocalDB.getFlatType()));
        spEditFurnishLevel.setSelection(EstateCtrl.getSpinnerItemPosition(spEditFurnishLevel, propertyLocalDB.getFurnishLevel()));
        spEditBedrooms.setSelection(EstateCtrl.getSpinnerItemPosition(spEditBedrooms, propertyLocalDB.getNoOfbedrooms()));
        spEditBathrooms.setSelection(EstateCtrl.getSpinnerItemPosition(spEditBathrooms, propertyLocalDB.getNoOfbathrooms()));

        // set view
        if (propertyLocalDB instanceof Lease) {
            tvLblFloorArea.setVisibility(View.INVISIBLE);
            etEditFloorArea.setVisibility(View.INVISIBLE);
        }
        if (propertyLocalDB instanceof Sale) {
            etEditFloorArea.setVisibility(View.VISIBLE);
            tvLblFloorArea.setVisibility(View.VISIBLE);
            etEditFloorArea.setText(((Sale) propertyLocalDB).getFloorArea());
            chkbxEditWholeApartment.setChecked(true);
            chkbxEditWholeApartment.setEnabled(false);
        }

        // check if photo is empty
        String photoData = propertyLocalDB.getPhoto();
        if (photoData.isEmpty())
            imgvPreview.setImageResource(R.drawable.ic_menu_camera);
        else
            imgvPreview.setImageBitmap(ImageHandler.getInstance().decodeStringToImage(photoData));

        return view;
    }

    private void setControls(View view, final Bundle savedInstanceState) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Update Property");
        tvLblCreatedDate = (TextView) view.findViewById(R.id.TVLblCreatedDate);
        tvLblFloorArea = (TextView) view.findViewById(R.id.TVLblFloorArea);
        etEditTitle = (EditText) view.findViewById(R.id.ETEditTitle);
        etEditDesc = (EditText) view.findViewById(R.id.ETEditDesc);
        etEditPrice = (EditText) view.findViewById(R.id.ETEditPrice);
        etEditAddressName = (EditText) view.findViewById(R.id.ETEditAddressName);
        etEditPostalCode = (EditText) view.findViewById(R.id.ETEditPostalCode);
        etEditUnit = (EditText) view.findViewById(R.id.ETEditUnit);
        etEditFloorArea = (EditText) view.findViewById(R.id.ETEditFloorArea);
        chkbxEditWholeApartment = (CheckBox) view.findViewById(R.id.CHKBXEditWholeApartment);

        imgvPreview = (ImageView) view.findViewById(R.id.IMGVPreview);

        spEditFurnishLevel = (Spinner) view.findViewById(R.id.SPEditFurnishLevel);
        spEditFurnishLevel.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FurnishLevel.values()));
        spEditFlatType = (Spinner) view.findViewById(R.id.SPEditFlatType);
        spEditFlatType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, FlatType.values()));
        spEditDealType = (Spinner) view.findViewById(R.id.SPEditDealType);
        spEditDealType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, DealType.values()));

        spEditBedrooms = (Spinner) view.findViewById(R.id.SPEditBedrooms);
        String[] arrBedRooms = {"Select Number Of Bed Room(s)", "1", "2", "3", "4", "5"};
        spEditBedrooms.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrBedRooms));
        spEditBathrooms = (Spinner) view.findViewById(R.id.SPEditBathrooms);
        String[] arrBathRooms = {"Select Number Of Bath Room(s)", "1", "2", "3"};
        spEditBathrooms.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrBathRooms));
        spEditStatus = (Spinner) view.findViewById(R.id.SPEditStatus);
        spEditStatus.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, PropertyStatus.values()));

        spEditDealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spEditDealType.getSelectedItem().toString().contains(DealType.ForSale.toString())) {
                    tvLblFloorArea.setVisibility(view.VISIBLE);
                    etEditFloorArea.setVisibility(view.VISIBLE);
                    chkbxEditWholeApartment.setEnabled(false);
                    chkbxEditWholeApartment.setChecked(true);
                }
                if (spEditDealType.getSelectedItem().toString().contains(DealType.ForLease.toString())) {
                    tvLblFloorArea.setVisibility(view.INVISIBLE);
                    etEditFloorArea.setVisibility(view.INVISIBLE);
                    chkbxEditWholeApartment.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnEditPhoto = (Button) view.findViewById(R.id.BTNEditPhoto);
        btnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ask for user permission to access gallery
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        });


        btnEditPropertySave = (Button) view.findViewById(R.id.BTNUpdateProperty);
        btnEditPropertySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // assign to valString
                valEditPropertyID = savedInstanceState.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID);
                valEditFlatType = spEditFlatType.getSelectedItem().toString();
                valEditDealType = spEditDealType.getSelectedItem().toString();
                valEditTitle = etEditTitle.getText().toString();
                valEditDesc = etEditDesc.getText().toString();
                valEditFurnishLevel = spEditFurnishLevel.getSelectedItem().toString();
                valEditPrice = etEditPrice.getText().toString();
                valEditPostalCode = etEditPostalCode.getText().toString();
                valEditUnit = etEditUnit.getText().toString();
                valEditAddressName = etEditAddressName.getText().toString();
                if (bitmap != null)
                    valEditPhoto = ImageHandler.getInstance().encodeImagetoString(bitmap);
                else {
                    imgvPreview.buildDrawingCache();
                    bitmap = imgvPreview.getDrawingCache();
                    // bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_menu_camera);
                    valEditPhoto = ImageHandler.getInstance().encodeImagetoString(bitmap);
                }
                valEditStatus = spEditStatus.getSelectedItem().toString();
                valEditBedrooms = spEditBedrooms.getSelectedItem().toString();
                valEditBathrooms = spEditBathrooms.getSelectedItem().toString();
                valEditFloorArea = (etEditFloorArea.getText().toString()).isEmpty() ? "" : etEditFloorArea.getText().toString();
                valEditWholeApartment = chkbxEditWholeApartment.isChecked() ? "wholeapartment" : "room";
                valCreatedDate = tvLblCreatedDate.getText().toString();

                if (valEditDealType.toString().equals(DealType.ForSale.toString())) {
                    sale = new Sale(
                            valEditPropertyID,
                            user,
                            valEditFlatType,
                            valEditDealType,
                            valEditTitle,
                            valEditDesc,
                            valEditFurnishLevel,
                            valEditPrice,
                            valEditPostalCode,
                            valEditUnit,
                            valEditAddressName,
                            valEditPhoto,
                            valEditStatus,
                            valEditBedrooms,
                            valEditBathrooms,
                            valEditFloorArea,
                            valCreatedDate);
                }
                if (valEditDealType.toString().equals(DealType.ForLease.toString())) {
                    lease = new Lease(
                            valEditPropertyID,
                            user,
                            valEditFlatType,
                            valEditDealType,
                            valEditTitle,
                            valEditDesc,
                            valEditFurnishLevel,
                            valEditPrice,
                            valEditPostalCode,
                            valEditUnit,
                            valEditAddressName,
                            valEditPhoto,
                            valEditStatus,
                            valEditBedrooms,
                            valEditBathrooms,
                            valEditWholeApartment,
                            valCreatedDate);
                }

                // check for empty fields
                if (spEditDealType.getSelectedItemId() != 0 &&
                        spEditFlatType.getSelectedItemId() != 0 &&
                        spEditStatus.getSelectedItemId() != 0 &&
                        spEditBedrooms.getSelectedItemId() != 0 &&
                        spEditBathrooms.getSelectedItemId() != 0 &&
                        spEditFurnishLevel.getSelectedItemId() != 0 &&
                        !valEditTitle.isEmpty() &&
                        !valEditDesc.isEmpty() &&
                        !valEditPostalCode.isEmpty() &&
                        !valEditUnit.isEmpty() &&
                        !valEditAddressName.isEmpty()
                        ) {

                    // handle if floor area is needed
                    if (valEditDealType.equals(DealType.ForSale.toString()) && valEditFloorArea.isEmpty()) {
                        // required floor area detail
                        etEditFloorArea.setError("Required field!");
                    } else {
                        // confirm action by user
                        // yes  = save to local DB first, then to server
                        AlertDialogHandler.showMyDialog(FragmentUpdateUserProperty.this);
                    }
                } else {
                    // empty fields
                    if (spEditDealType.getSelectedItemId() == 0)
                        ((TextView) spEditDealType.getSelectedView()).setError("Required field!");
                    if (spEditFlatType.getSelectedItemId() == 0)
                        ((TextView) spEditFlatType.getSelectedView()).setError("Required field!");
                    if (spEditStatus.getSelectedItemId() == 0)
                        ((TextView) spEditStatus.getSelectedView()).setError("Required field!");
                    if (spEditBedrooms.getSelectedItemId() == 0)
                        ((TextView) spEditBedrooms.getSelectedView()).setError("Required field!");
                    if (spEditBathrooms.getSelectedItemId() == 0)
                        ((TextView) spEditBathrooms.getSelectedView()).setError("Required field!");
                    if (spEditFurnishLevel.getSelectedItemId() == 0)
                        ((TextView) spEditFurnishLevel.getSelectedView()).setError("Required field!");
                    if (valEditDealType.equals(DealType.ForSale.toString()) && valEditFloorArea.isEmpty()) {
                        // required floor area detail
                        etEditFloorArea.setError("Required field!");
                    }
                    if (valEditTitle.isEmpty())
                        etEditTitle.setError("Required field!");
                    if (valEditDesc.isEmpty())
                        etEditDesc.setError("Required field!");
                    if (valEditPostalCode.isEmpty())
                        etEditPostalCode.setError("Required field!");
                    if (valEditUnit.isEmpty())
                        etEditUnit.setError("Required field!");
                    if (valEditAddressName.isEmpty())
                        etEditAddressName.setError("Required field!");

                }
            }
        });

        btnEditRandom = (Button) view.findViewById(R.id.BTNEditRandom);
        btnEditRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEditDesc.setText(Utility.generateDesc());
                etEditPrice.setText(Utility.generateNumberAsString(299999, 19999999));
                spEditBedrooms.setSelection(Utility.generateNumber(1, spEditBedrooms.getCount() - 1));
                spEditBathrooms.setSelection(Utility.generateNumber(1, spEditBathrooms.getCount() - 1));
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
                imgvPreview.setImageBitmap(BitmapFactory.decodeFile(filePath));
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }


    private static boolean alertDialogAction;

    // alert dialog response
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        alertDialogAction = true;
        // save to local DB first, then to server
        if (lease != null) {
            propertyCtrl.updateUserPropertyDetails(lease);
            paramValues.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, lease.getPropertyID());
            paramValues.put(PropertyCtrl.KEY_PROPERTY_FLATTYPE, lease.getFlatType()); // flattype
            paramValues.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, lease.getDealType());    // dealtype
            paramValues.put(PropertyCtrl.KEY_PROPERTY_TITLE, lease.getTitle()); // title
            paramValues.put(PropertyCtrl.KEY_PROPERTY_DESC, lease.getDescription());    // description
            paramValues.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, lease.getFurnishLevel()); // furnishlevel
            paramValues.put(PropertyCtrl.KEY_PROPERTY_PRICE, lease.getPrice());    // price
            paramValues.put(PropertyCtrl.KEY_PROPERTY_POSTALCODE, lease.getPostalcode()); // postalcode
            paramValues.put(PropertyCtrl.KEY_PROPERTY_UNIT, lease.getUnit());    // unit
            paramValues.put(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME, lease.getAddressName()); // addressname
            paramValues.put(PropertyCtrl.KEY_PROPERTY_PHOTO, lease.getPhoto());    // photo
            paramValues.put(PropertyCtrl.KEY_PROPERTY_STATUS, lease.getStatus()); // status
            paramValues.put(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS, lease.getNoOfbedrooms()); // noofbedrooms
            paramValues.put(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS, lease.getNoOfbathrooms()); // noofbathrooms
            paramValues.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, valEditFloorArea);    // floorarea
            paramValues.put(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT, lease.getWholeApartment());    // wholeapartment

        }
        if (sale != null) {
            propertyCtrl.updateUserPropertyDetails(sale);
            paramValues.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, sale.getPropertyID());
            paramValues.put(PropertyCtrl.KEY_PROPERTY_FLATTYPE, sale.getFlatType()); // flattype
            paramValues.put(PropertyCtrl.KEY_PROPERTY_DEALTYPE, sale.getDealType());    // dealtype
            paramValues.put(PropertyCtrl.KEY_PROPERTY_TITLE, sale.getTitle()); // title
            paramValues.put(PropertyCtrl.KEY_PROPERTY_DESC, sale.getDescription());    // description
            paramValues.put(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL, sale.getFurnishLevel()); // furnishlevel
            paramValues.put(PropertyCtrl.KEY_PROPERTY_PRICE, sale.getPrice());    // price
            paramValues.put(PropertyCtrl.KEY_PROPERTY_POSTALCODE, sale.getPostalcode()); // postalcode
            paramValues.put(PropertyCtrl.KEY_PROPERTY_UNIT, sale.getUnit());    // unit
            paramValues.put(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME, sale.getAddressName()); // addressname
            paramValues.put(PropertyCtrl.KEY_PROPERTY_PHOTO, sale.getPhoto());    // photo
            paramValues.put(PropertyCtrl.KEY_PROPERTY_STATUS, sale.getStatus()); // status
            paramValues.put(PropertyCtrl.KEY_PROPERTY_NOOFBEDROOMS, sale.getNoOfbedrooms()); // noofbedrooms
            paramValues.put(PropertyCtrl.KEY_PROPERTY_NOOFBATHROOMS, sale.getNoOfbathrooms()); // noofbathrooms
            paramValues.put(PropertyCtrl.KEY_PROPERTY_FLOORAREA, sale.getFloorArea());    // floorarea
            paramValues.put(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT, valEditWholeApartment);    // wholeapartment

        }

        // save to remote server
        if (paramValues != null) {
            new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_UPDATEUSERPROPERTY, paramValues, getActivity(), new AsyncTaskResponse() {
                @Override
                public void onAsyncTaskResponse(String response) {
                    // Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    if (!response.isEmpty())
                        FragmentHandler.getInstance().loadFragment(FragmentUpdateUserProperty.this, new FragmentUserListings());
                    else
                        Toast.makeText(getActivity(), "Unable to update property", Toast.LENGTH_LONG).show();
                }
            }).execute();
        }


    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // btnEditPropertySave.setText("Cancelled");
        Toast.makeText(getActivity(), "Update was cancelled.", Toast.LENGTH_SHORT).show();
    }
}
