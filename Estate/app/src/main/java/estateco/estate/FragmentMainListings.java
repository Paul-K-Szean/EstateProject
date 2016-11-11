package estateco.estate;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Property;
import entities.User;
import tabs.SlidingTabLayout;

import static controllers.EstateConfig.URL_LEASELISTINGS;
import static controllers.EstateConfig.URL_SALELISTINGS;
import static controllers.EstateConfig.URL_SEARCHLISTINGS;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMainListings extends Fragment implements Filterable {
    private static final String TAG = FragmentMainListings.class.getSimpleName();
    private static FragmentMainListings fragmentMainListings;
    private static Bundle args;
    private static LruCache<String, Bitmap> lruCache;
    final int maxMemorySize = (int) Runtime.getRuntime().maxMemory() / 1024;
    final int cacheSize = maxMemorySize / 10;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private User user;
    private User owner;
    private Property property;
    private ArrayList<Property> propertyArrayList;
    //    private RecyclerView recycler;
//    private ViewAdapterRecyclerProperty viewAdapter;
    private TextView tvAllListingCount, itemDataID;
    private SearchView searchView;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Toolbar toolBarTop, toolBarBottom;
    private String URL_ADDRESS;
    private CustomFilter customFilter;
    private ArrayList<Property> filterArrayList;

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

    public static Bitmap getBitmapFromCache(String key) {
        return lruCache.get(key);
    }

    public static void setBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main_listings, container, false);
        savedInstanceState = getArguments();
        URL_ADDRESS = savedInstanceState.getString("URL_SEARCHTYPE");

        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());
        propertyCtrl = new PropertyCtrl(getActivity());
        setControls(view);


//        propertyArrayList = new ArrayList<>();
//        recycler = (RecyclerView) view.findViewById(R.id.recycleView);
//        viewAdapter = new ViewAdapterRecyclerProperty(FragmentMainListings.this, propertyArrayList);
        // get all property listing from server
        propertyCtrl.serverGetAllListing(FragmentMainListings.this, URL_ADDRESS, tvAllListingCount);

        return view;
    }

    public void setControls(final View view) {
        // tool bars
        toolBarTop = (Toolbar) getActivity().findViewById(R.id.toolbar_top);
        tvAllListingCount = (TextView) view.findViewById(R.id.TVAllListingCount);

        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        setHasOptionsMenu(true);
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
                propertyCtrl.getViewAdapter().getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public Filter getFilter() {
        if (customFilter == null)
            customFilter = new CustomFilter();
        return customFilter;
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

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.i(TAG, "performFiltering" + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                // CONSTRAINT TO lower
                constraint = constraint.toString().toLowerCase();
                ArrayList<Property> filters = new ArrayList<>();
                for (Property filtered : filterArrayList) {
                    if (filtered.getOwner().getName().toLowerCase().contains(constraint) ||
                            filtered.getFlatType().toLowerCase().contains(constraint) ||
                            filtered.getStreetname().toLowerCase().contains(constraint) ||
                            filtered.getDealType().toLowerCase().contains(constraint) ||
                            filtered.getWholeapartment().toLowerCase().contains(constraint)) {
                        filters.add(filtered);
                    }
                }
                filterResults.count = filters.size();
                filterResults.values = filters;
            } else {
                filterResults.count = filterArrayList.size();
                filterResults.values = filterArrayList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.i(TAG, "publishResults");
            propertyArrayList = (ArrayList<Property>) results.values;
            TextView textView = (TextView) getView().findViewById(R.id.TVAllListingCount);

            if (propertyArrayList.size() > 1)
                textView.setText(propertyArrayList.size() + " records");
            else
                textView.setText(propertyArrayList.size() + " record");
            propertyCtrl.getViewAdapter().notifyDataSetChanged();
        }
    }

}


