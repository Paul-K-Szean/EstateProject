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
import java.util.Map;

import controllers.UserCtrl;
import entities.Property;
import entities.User;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.JSONHandler;
import handler.ViewAdapterListView;
import handler.ViewAdapterRecycler;
import tabs.SlidingTabLayout;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static controllers.EstateConfig.URL_LEASELISTINGS;
import static controllers.EstateConfig.URL_SALELISTINGS;
import static controllers.EstateConfig.URL_SEARCHLISTINGS;
import static controllers.PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_BLOCK;
import static controllers.PropertyCtrl.KEY_PROPERTY_CREATEDDATE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DEALTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_DESC;
import static controllers.PropertyCtrl.KEY_PROPERTY_FAVOURITECOUNT;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLATTYPE;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORAREA;
import static controllers.PropertyCtrl.KEY_PROPERTY_FLOORLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL;
import static controllers.PropertyCtrl.KEY_PROPERTY_IMAGE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PRICE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;
import static controllers.PropertyCtrl.KEY_PROPERTY_STATUS;
import static controllers.PropertyCtrl.KEY_PROPERTY_STREETNAME;
import static controllers.PropertyCtrl.KEY_PROPERTY_TITLE;
import static controllers.PropertyCtrl.KEY_PROPERTY_VIEWCOUNT;
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
    private static FragmentMainListings fragmentMainListings;
    private static Bundle args;
    private UserCtrl userCtrl;
    private User user;
    private User owner;
    private Property property;
    private ArrayList<Property> propertyArrayList;
    private RecyclerView recycler;
    private ViewAdapterRecycler viewAdapter;
    private ViewAdapterListView viewAdapterListView;
    private Map<String, String> paramValues;
    private TextView tvAllListingCount, itemDataID;
    private SearchView searchView;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Toolbar toolBarTop, toolBarBottom;
    private String URL_ADDRESS;

    public FragmentMainListings() {
        // Required empty public constructor
    }

    public static FragmentMainListings getInstance(int position) {
        fragmentMainListings = new FragmentMainListings();
        args = new Bundle();

        switch (position) {
            case 0:
                args.putString("URL_SEARCHTYPE", URL_SEARCHLISTINGS);
                break;
            case 1:
                args.putString("URL_SEARCHTYPE", URL_LEASELISTINGS);
                break;
            case 2:
                args.putString("URL_SEARCHTYPE", URL_SALELISTINGS);
                break;
        }
        fragmentMainListings.setArguments(args);
        return fragmentMainListings;
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
        //        toolBarBottom = (Toolbar) getActivity().findViewById(R.id.toolbar_bottom);
        //        toolBarBottom.inflateMenu(R.menu.property_details_actionbar);

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
            recycler = (RecyclerView) getView().findViewById(R.id.recycleView);
            JSONArray jsonArray = JSONHandler.getResultAsArray(getActivity(), response);
            if (jsonArray != null) {
                Log.i(TAG, "Results: " + jsonArray.length());
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    owner = new User(
                            jsonObject.getString(KEY_USERID),
                            jsonObject.getString(KEY_NAME),
                            jsonObject.getString(KEY_EMAIL),
                            jsonObject.getString(KEY_CONTACT));

                    property = new Property(
                            jsonObject.getString(KEY_PROPERTY_PROPERTYID),
                            owner,
                            jsonObject.getString(KEY_PROPERTY_FLATTYPE),
                            jsonObject.getString(KEY_PROPERTY_BLOCK),
                            jsonObject.getString(KEY_PROPERTY_STREETNAME),
                            jsonObject.getString(KEY_PROPERTY_FLOORLEVEL),
                            jsonObject.getString(KEY_PROPERTY_FLOORAREA),
                            jsonObject.getString(KEY_PROPERTY_PRICE),
                            jsonObject.getString(KEY_PROPERTY_IMAGE),
                            jsonObject.getString(KEY_PROPERTY_STATUS),
                            jsonObject.getString(KEY_PROPERTY_DEALTYPE),
                            jsonObject.getString(KEY_PROPERTY_TITLE),
                            jsonObject.getString(KEY_PROPERTY_DESC),
                            jsonObject.getString(KEY_PROPERTY_FURNISHLEVEL),
                            jsonObject.getString(KEY_PROPERTY_BEDROOMCOUNT),
                            jsonObject.getString(KEY_PROPERTY_BATHROOMCOUNT),
                            jsonObject.getString(KEY_PROPERTY_FAVOURITECOUNT),
                            jsonObject.getString(KEY_PROPERTY_VIEWCOUNT),
                            jsonObject.getString(KEY_PROPERTY_WHOLEAPARTMENT),
                            jsonObject.getString(KEY_PROPERTY_CREATEDDATE));
                    propertyArrayList.add(property);
                }


                viewAdapter = new ViewAdapterRecycler(FragmentMainListings.this, propertyArrayList);
                recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                recycler.setVisibility(VISIBLE);
                recycler.setAdapter(viewAdapter);

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
        Log.i(TAG, "onCreateOptionsMenu");
        // super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_action_searchQuery).setVisible(true);
        // search behaviour
        searchView = (SearchView) menu.findItem(R.id.menu_action_searchQuery).getActionView();
        searchView.setSubmitButtonEnabled(true);

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
                viewAdapter.getFilter().filter(newText);
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


