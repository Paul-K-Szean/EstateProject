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

import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Lease;
import entities.Property;
import entities.Sale;
import entities.User;
import enums.FlatType;
import enums.FurnishLevel;
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
    private Property propertyLocalDB;
    private Sale sale;
    private Lease lease;
    private static final int SELECTED_IMAGE = 1;
    Bitmap bitmap;
    TextView tvLblCreatedDate;
    EditText etEditTitle, etEditDesc, etEditPrice, etEditNoOfBedrooms, etEditNoOfBathrooms, etEditFloorArea,
            etEditAddressName, etEditPostalCode, etEditUnit;
    Button btnEditPropertySave, btnEditPhoto;
    ImageView imgvPreview;
    Spinner spEditFurnishLevel, spEditFlatType;
    CheckBox chkbxEditWholeApartment;
    String selectedImagePath;


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
            // getActivity().finish();
        }


        // get user property detail from local db
        savedInstanceState = getArguments();
        propertyLocalDB = propertyCtrl.getUserPropertyDetails(savedInstanceState.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID), user);
        tvLblCreatedDate.setText(propertyLocalDB.getCreatedate());
        etEditTitle.setText(propertyLocalDB.getTitle());
        etEditDesc.setText(propertyLocalDB.getDescription());
        spEditFurnishLevel.setSelection(1);
        etEditPrice.setText("SGD$" + propertyLocalDB.getPrice());
        etEditPostalCode.setText("S'pore(" + propertyLocalDB.getPostalcode() + ")");
        etEditUnit.setText("#" + propertyLocalDB.getUnit());
        etEditAddressName.setText(propertyLocalDB.getAddressName());
        etEditNoOfBedrooms.setText(propertyLocalDB.getNoOfbedrooms());
        etEditNoOfBathrooms.setText(propertyLocalDB.getNoOfbathrooms());


        // TODO DEAL TYPE
        if (propertyLocalDB instanceof Lease) {
            //TODO DISPLAY WHOLE APARTMENT
        }
        if (propertyLocalDB instanceof Sale) {
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

    private void setControls(View view) {
        tvLblCreatedDate = (TextView) view.findViewById(R.id.TVLblCreatedDate);
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
                etEditTitle.setText("TESTING");
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

}
