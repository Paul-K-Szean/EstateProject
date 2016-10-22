package estateco.estate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controllers.UserCtrl;
import entities.Property;
import entities.User;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.JSONHandler;
import handler.ViewAdapter;
import tabs.SlidingTabLayout;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static controllers.EstateConfig.URL_SEARCHLISTINGS;
import static controllers.PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BLOCK;
import static controllers.PropertyCtrl.KEY_PROPERTY_CREATEDDATE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DEALTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DESC;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLATTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORAREA;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_IMAGE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PRICE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;
import static controllers.PropertyCtrl.KEY_PROPERTY_SEARCH;
import static controllers.PropertyCtrl.KEY_PROPERTY_STATUS;
import static controllers.PropertyCtrl.KEY_PROPERTY_STREETNAME;
import static controllers.PropertyCtrl.KEY_PROPERTY_TITLE;
import static controllers.PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT;
import static controllers.UserCtrl.KEY_CONTACT;
import static controllers.UserCtrl.KEY_EMAIL;
import static controllers.UserCtrl.KEY_NAME;
import static controllers.UserCtrl.KEY_USERID;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMainListings extends Fragment {
    private static final String TAG = FragmentMainListings.class.getSimpleName();
    private UserCtrl userCtrl;
    private User user;
    private User owner;
    private Property property;


    //ArrayList for property info
    private RecyclerView recycler;
    private ViewAdapter viewAdapter;
    private ArrayList<Property> propertyArrayList;
    private Map<String, String> paramValues;


    private TextView tvAllListingCount, itemDataID;

    private SearchView searchView;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Toolbar toolBarTop, toolBarBottom;
    private String URL_ADDRESS = URL_SEARCHLISTINGS;

    public FragmentMainListings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main_listings, container, false);
        savedInstanceState = getArguments();
        URL_ADDRESS = savedInstanceState.getString("URL_SEARCHTYPE");

        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());

        setControls(view);

        // tool bars
        toolBarTop = (Toolbar) getActivity().findViewById(R.id.toolbar_top);
        toolBarBottom = (Toolbar) getActivity().findViewById(R.id.toolbar_bottom);
        toolBarBottom.inflateMenu(R.menu.property_details_actionbar);


        // get all property listing from server
        new AsyncTaskHandler(Request.Method.GET, URL_ADDRESS, null, getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                getAllListings(response);
            }
        }).execute();

        return view;
    }

    public void setControls(final View view) {
        toolBarTop = (Toolbar) getActivity().findViewById(R.id.toolbar_top);
        tvAllListingCount = (TextView) view.findViewById(R.id.TVAllListingCount);
        setHasOptionsMenu(true);
    }


    private void getAllListings(String response) {
        try {
            propertyArrayList = new ArrayList<>();
            JSONArray jsonArray = JSONHandler.getResultAsArray(getActivity(), response);

            if (jsonArray != null) {
                Log.i(TAG, "Results: " + jsonArray.length());
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonRecordObject = jsonArray.getJSONObject(index);
                    owner = new User(
                            jsonRecordObject.getString(KEY_USERID),
                            jsonRecordObject.getString(KEY_NAME),
                            jsonRecordObject.getString(KEY_EMAIL),
                            jsonRecordObject.getString(KEY_CONTACT));

                    property = new Property(
                            jsonRecordObject.getString(KEY_PROPERTY_PROPERTYID),
                            owner,
                            jsonRecordObject.getString(KEY_PROPERTY_FLATTYPE),
                            jsonRecordObject.getString(KEY_PROPERTY_BLOCK),
                            jsonRecordObject.getString(KEY_PROPERTY_STREETNAME),
                            jsonRecordObject.getString(KEY_PROPERTY_FLOORLEVEL),
                            jsonRecordObject.getString(KEY_PROPERTY_FLOORAREA),
                            jsonRecordObject.getString(KEY_PROPERTY_PRICE),
                            jsonRecordObject.getString(KEY_PROPERTY_IMAGE),
                            jsonRecordObject.getString(KEY_PROPERTY_STATUS),
                            jsonRecordObject.getString(KEY_PROPERTY_DEALTYPE),
                            jsonRecordObject.getString(KEY_PROPERTY_TITLE),
                            jsonRecordObject.getString(KEY_PROPERTY_DESC),
                            jsonRecordObject.getString(KEY_PROPERTY_FURNISHLEVEL),
                            jsonRecordObject.getString(KEY_PROPERTY_BEDROOMCOUNT),
                            jsonRecordObject.getString(KEY_PROPERTY_BATHROOMCOUNT),
                            jsonRecordObject.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                            jsonRecordObject.getString(KEY_PROPERTY_CREATEDDATE));
                    propertyArrayList.add(property);
                }

                recycler = (RecyclerView) getView().findViewById(R.id.recycleView);
                viewAdapter = new ViewAdapter(FragmentMainListings.this, propertyArrayList);
                recycler.setAdapter(viewAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                recycler.setVisibility(VISIBLE);
                if (propertyArrayList.size() > 1)
                    tvAllListingCount.setText(propertyArrayList.size() + " records");
                else
                    tvAllListingCount.setText(propertyArrayList.size() + " record");

            } else {
                recycler.setVisibility(GONE);
                String result = JSONHandler.getResultAsString(getActivity(), response);
                tvAllListingCount.setText(result);
            }

        } catch (JSONException error) {

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_action_searchQuery).setVisible(true);
        // search behaviour
        searchView = (SearchView) menu.findItem(R.id.menu_action_searchQuery).getActionView();
        searchView.setSubmitButtonEnabled(true);
        Log.i(TAG, "Query: " + searchView.getQuery().toString());
        if (!searchView.getQuery().toString().isEmpty()) {
            searchView.setIconifiedByDefault(true);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Map<String, String> paramValues = new HashMap<>();
                paramValues.put(KEY_PROPERTY_SEARCH, newText);
                new AsyncTaskHandler(Request.Method.POST, URL_ADDRESS, paramValues, getActivity(), new AsyncTaskResponse() {
                    @Override
                    public void onAsyncTaskResponse(String response) {
                        getAllListings(response);
                    }
                }).execute();

                return false;
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
        // fetching user details from sqlite
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


