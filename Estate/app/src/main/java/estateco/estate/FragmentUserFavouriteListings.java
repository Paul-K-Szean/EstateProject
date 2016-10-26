package estateco.estate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Property;
import entities.User;
import handler.ViewAdapterRecycler;
import tabs.SlidingTabLayout;

import static android.view.View.GONE;
import static controllers.FavouriteCtrl.KEY_FAVOURITE_OWNERID;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUserFavouriteListings extends Fragment {
    private static final String TAG = FragmentUserFavouriteListings.class.getSimpleName();


    private UserCtrl userCtrl;
    private User user, owner;
    private Property property;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private RecyclerView recycler;
    private ViewAdapterRecycler viewAdapter;
    private ArrayList<Property> propertyArrayList;

    private TextView tvUserFavouriteListingsCount;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    public FragmentUserFavouriteListings() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_favourite_listings, container, false);

        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());
        favouriteCtrl = new FavouriteCtrl(getActivity());
        user = userCtrl.getUserDetails();

        setControls(view, savedInstanceState);


        // get user favourite listings from server
        favouriteCtrl.serverGetUserFavouriteListings(FragmentUserFavouriteListings.this, user);


        return view;
    }

    private void setControls(View view, Bundle savedInstanceState) {
        Toolbar toolBarTop = (Toolbar) getActivity().findViewById(R.id.toolbar_top);
        toolBarTop.setTitle("My favourites");
        toolBarTop.getMenu().findItem(R.id.menu_action_searchQuery).setVisible(false);
        tvUserFavouriteListingsCount = (TextView) view.findViewById(R.id.TVUserFavouriteListingsCount);
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
