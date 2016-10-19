package estateco.estate;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Property;
import entities.User;
import handler.FragmentHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.ViewAdapterAllProperties;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUserListings extends Fragment {
    private static final String TAG = FragmentUserListings.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;
    private ArrayList<Property> userProperties;

    Button btnNewListing;
    GridView gvUserListings;
    TextView tvUserMsg, itemDataID;
    Toolbar toolbar;

    public FragmentUserListings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_listings, container, false);

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

        setControls(view);


        return view;
    }

    public void setControls(View view) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("My Lisings");
        // error msg
        tvUserMsg = (TextView) view.findViewById(R.id.TVUserMsg);

        gvUserListings = (GridView) view.findViewById(R.id.GVUserListings);

        // new property listing
        btnNewListing = (Button) view.findViewById(R.id.BTNNewListing);
        btnNewListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to new listing step 1
                FragmentHandler.loadFragment(FragmentUserListings.this, new FragmentNewProperty());
            }
        });


        // retrieves data from local db
        userProperties = propertyCtrl.getUserProperties(user);
        // displays into grid view
        gvUserListings.setAdapter(new ViewAdapterAllProperties(getActivity(), userProperties));

        if (userProperties.size() > 1)
            tvUserMsg.setText("You have a total of " + userProperties.size() + " properties");
        else
            tvUserMsg.setText("You have a total of " + userProperties.size() + " property");

        // item selected behaviour
        gvUserListings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // retrieve property id
                itemDataID = (TextView) view.findViewById(R.id.TVLblPropertyID);
                Bundle bundlePropertyDetails = new Bundle();
                bundlePropertyDetails.putString(PropertyCtrl.KEY_PROPERTY_PROPERTYID, itemDataID.getText().toString());
                FragmentHandler.loadFragment(FragmentUserListings.this, new FragmentUpdateUserProperty(), bundlePropertyDetails);
            }
        });
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
