package estateco.estate;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.HashMap;

import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.User;
import handler.FragmentHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNewPropertyStep1 extends Fragment {
    private static final String TAG = FragmentNewPropertyStep1.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;

    public FragmentNewPropertyStep1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_listing_step1, container, false);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // setup ctrl objects
        db = new SQLiteHandler(getActivity());
        userCtrl = new UserCtrl(getActivity());
        session = new SessionHandler(getActivity());

        // check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
        } else {
            db.deleteUsers();
            session.setLogin(false);
            // getActivity().finish();
        }

        setControls(view);

        return view;
    }

    Button btnNewListNext, btnRandomise;
    CheckBox chkbxForSale, chkbxForLease;
    EditText etTitle, etDesc, etPostalCode, etUnit, etAddressName;
    String valTitle, valDesc, valPostalCode, valUnit, valAddressName;

    public void setControls(View view) {
        // set controls
        etTitle = (EditText) view.findViewById(R.id.ETTitle);
        etDesc = (EditText) view.findViewById(R.id.ETDesc);

        etPostalCode = (EditText) view.findViewById(R.id.ETPostalCode);
        etUnit = (EditText) view.findViewById(R.id.ETUnit);
        etAddressName = (EditText) view.findViewById(R.id.ETAddressName);


        // random btn
        btnRandomise = (Button) view.findViewById(R.id.BTNRandomise);
        btnRandomise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTitle.setText(Utility.generateTitle());
                etDesc.setText(Utility.generateDesc());
                etPostalCode.setText(Utility.generateNumberAsString(6));
                etUnit.setText(Utility.generateUnit());
                etAddressName.setText("Humes");
                if (Utility.generateNumber(0, 1) == 1) {
                    chkbxForSale.setChecked(true);
                    chkbxForLease.setChecked(false);
                } else {
                    chkbxForSale.setChecked(false);
                    chkbxForLease.setChecked(true);
                }
            }
        });

        // checkbox controls
        chkbxForSale = (CheckBox) view.findViewById(R.id.CHKBXForSale);
        chkbxForLease = (CheckBox) view.findViewById(R.id.CHKBXForLease);

        chkbxForSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkbxForSale.setChecked(true);
                chkbxForLease.setChecked(false);
            }
        });
        chkbxForLease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkbxForSale.setChecked(false);
                chkbxForLease.setChecked(true);
            }
        });


        // next btn
        btnNewListNext = (Button) view.findViewById(R.id.BTNNewListingNext);
        btnNewListNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Next button clicked.");
                // check user input
                valTitle = etTitle.getText().toString().trim();
                valDesc = etDesc.getText().toString().trim();
                valPostalCode = etPostalCode.getText().toString().trim();
                valUnit = etUnit.getText().toString().trim();
                valAddressName = etAddressName.getText().toString().trim();

                // no empty field
                if (!valTitle.isEmpty() &&
                        !valDesc.isEmpty() &&
                        !valPostalCode.isEmpty() &&
                        !valUnit.isEmpty() &&
                        !valAddressName.isEmpty() && (!chkbxForSale.isChecked() || !chkbxForLease.isChecked())) {

                    // change to step 2 fragment
                    Bundle fragmentStep1Details = new Bundle();
                    if (chkbxForSale.isChecked())
                        fragmentStep1Details.putString(PropertyCtrl.KEY_PROPERTY_DEALTYPE, "For Sale");
                    if (chkbxForLease.isChecked())
                        fragmentStep1Details.putString(PropertyCtrl.KEY_PROPERTY_DEALTYPE, "For Lease");
                    fragmentStep1Details.putString(PropertyCtrl.KEY_PROPERTY_TITLE, valTitle);
                    fragmentStep1Details.putString(PropertyCtrl.KEY_PROPERTY_DESC, valDesc);
                    fragmentStep1Details.putString(PropertyCtrl.KEY_PROPERTY_POSTALCODE, valPostalCode);
                    fragmentStep1Details.putString(PropertyCtrl.KEY_PROPERTY_UNIT, valUnit);
                    fragmentStep1Details.putString(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME, valAddressName);

                    FragmentHandler.getInstance().loadFragment(FragmentNewPropertyStep1.this, new FragmentNewPropertyStep2(), fragmentStep1Details);
                } else {
                    // empty field detected
                    if (!chkbxForSale.isChecked() && !chkbxForLease.isChecked()) {
                        chkbxForSale.setError("Required field!");
                        chkbxForLease.setError("Required field!");
                    }
                    if (valTitle.isEmpty())
                        etTitle.setError("Required field!");
                    if (valDesc.isEmpty())
                        etDesc.setError("Required field!");
                    if (valPostalCode.isEmpty())
                        etPostalCode.setError("Required field!");
                    if (valUnit.isEmpty())
                        etUnit.setError("Required field!");
                    if (valAddressName.isEmpty())
                        etAddressName.setError("Required field!");

                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.w(TAG, "onSaveInstanceState");
        // save the fragment's state here

        outState.putString(PropertyCtrl.KEY_PROPERTY_TITLE, valTitle);
        outState.putString(PropertyCtrl.KEY_PROPERTY_DESC, valDesc);
        outState.putString(PropertyCtrl.KEY_PROPERTY_POSTALCODE, valPostalCode);
        outState.putString(PropertyCtrl.KEY_PROPERTY_UNIT, valUnit);
        outState.putString(PropertyCtrl.KEY_PROPERTY_ADDRESSNAME, valAddressName);
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
        HashMap<String, String> savedUser = db.getUserDetails();

        if (savedUser != null) {
            user = new User(
                    savedUser.get("userID"),
                    savedUser.get("name"),
                    savedUser.get("email"),
                    savedUser.get("password"),
                    savedUser.get("contact")
            );
        } else
            Log.e(TAG, "No user data from local database.");
    }

    @Override
    public void onPause() {
        Log.w(TAG, "onPause");
        super.onPause();
        if (user != null)
            db.updateUser(user);
        else
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
