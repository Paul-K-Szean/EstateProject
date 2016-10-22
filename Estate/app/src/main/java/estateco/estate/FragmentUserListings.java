package estateco.estate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Property;
import entities.User;
import handler.FragmentHandler;
import handler.ViewAdapter;
import tabs.SlidingTabLayout;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUserListings extends Fragment {
    private static final String TAG = FragmentUserListings.class.getSimpleName();

    private UserCtrl userCtrl;
    private User user;
    private PropertyCtrl propertyCtrl;

    private RecyclerView recycler;
    private ViewAdapter viewAdapter;
    private ArrayList<Property> propertyArrayList;
    Button btnNewListing;
    TextView tvUserMsg, itemDataID;
    Toolbar toolBarTop;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

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
        userCtrl = new UserCtrl(getActivity());
        user = userCtrl.getUserDetails();
        propertyCtrl = new PropertyCtrl(getActivity());

        viewPager = (ViewPager) getActivity().findViewById(R.id.ViewPagerMain);
        viewPager.setVisibility(GONE);
        slidingTabLayout = (SlidingTabLayout) getActivity().findViewById(R.id.TabLayoutMain);
        slidingTabLayout.setVisibility(GONE);


        setControls(view);
        return view;
    }

    public void setControls(View view) {
        toolBarTop = (Toolbar) getActivity().findViewById(R.id.toolbar_top);
        toolBarTop.setTitle("My Listings");
        toolBarTop.getMenu().findItem(R.id.menu_action_searchQuery).setVisible(false);
        // error msg
        tvUserMsg = (TextView) view.findViewById(R.id.TVUserListingsCount);

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
        propertyArrayList = propertyCtrl.getUserProperties(user);
        // displays into recycler view
        recycler = (RecyclerView) view.findViewById(R.id.recycleView);
        viewAdapter = new ViewAdapter(FragmentUserListings.this, propertyArrayList);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(viewAdapter);


        if (propertyArrayList.size() > 1)
            tvUserMsg.setText("You have a total of " + propertyArrayList.size() + " properties");
        else
            tvUserMsg.setText("You have a total of " + propertyArrayList.size() + " property");


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

    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }


}
