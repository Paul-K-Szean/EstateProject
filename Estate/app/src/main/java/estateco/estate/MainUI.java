package estateco.estate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Property;
import entities.User;
import handler.SQLiteHandler;
import handler.SessionHandler;
import tabs.SlidingTabLayout;

import static android.view.View.GONE;

// import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

public class MainUI extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainUI.class.getSimpleName();

    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;

    private TextView tvHderName, tvHderEmail;
    private NavigationView navigationView;
    private Toolbar toolBarTop, toolBarBottom;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_action_searchQuery).setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.menu_action_inbox) {
            startActivity(new Intent(MainUI.this, InboxUI.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragObj = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_allistings) {
            startActivity(new Intent(MainUI.this, MainUI.class));
            finish();
        } else if (id == R.id.nav_userlistings) {
            fragObj = new FragmentUserListings();
        } else if (id == R.id.nav_userfavourites) {
            viewPager.setVisibility(GONE);
            fragObj = new FragmentUserFavouriteListings();

        } else if (id == R.id.nav_logout) {
            userCtrl.deleteUserTable();
            propertyCtrl.deletePropertyTable();
            favouriteCtrl.deleteFavouritePropertyTable();
            session.setLogin(false);
            startActivity(new Intent(MainUI.this, LoginUI.class));
            finish();
        }

        if (fragObj != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragObj);
            fragmentTransaction.addToBackStack(fragObj.getTag());  // add to stack for back button
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setup ctrl objects
        session = new SessionHandler(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        userCtrl = new UserCtrl(getApplicationContext());
        propertyCtrl = new PropertyCtrl(getApplicationContext());
        favouriteCtrl = new FavouriteCtrl(getApplicationContext());
        user = userCtrl.getUserDetails();

        Log.i(TAG, "User at main: " + user.getName() + ", Current session: " + session.isLoggedIn());
        // check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
        } else {
            // remove any existing data in local db.
            userCtrl.deleteUserTable();
            propertyCtrl.deletePropertyTable();
            favouriteCtrl.deleteFavouritePropertyTable();
            session.setLogin(false);
        }

        // tool bars
        toolBarTop = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolBarTop);
        toolBarTop.setSubtitle("All Listings");
//        toolBarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
//        toolBarBottom.inflateMenu(R.menu.property_details_actionbar);

        // sliding tabs
        viewPager = (ViewPager) findViewById(R.id.ViewPagerMain);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.TabLayoutMain);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorAccent);
            }
        });
        viewPager.setOffscreenPageLimit(0);
        slidingTabLayout.setViewPager(viewPager);

        // navigation
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setTitle("My listings (" + propertyCtrl.getUserPropertyCountDetails() + ")");
        navigationView.getMenu().getItem(2).setTitle("Favourite Listings (" + favouriteCtrl.getUserFarvouriteCountDetails() + ")");

        // drawer header
        View header = navigationView.getHeaderView(0);
        // sets the header use user information
        tvHderName = (TextView) header.findViewById(R.id.TVHderName);
        tvHderEmail = (TextView) header.findViewById(R.id.TVHderEmail);
        tvHderName.setText(user.getName());
        tvHderEmail.setText(user.getEmail());

        // drawers items
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolBarTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements Filterable {

        String[] tabTitle = {"All", "For Lease", "For Sale"};
        private ArrayList<Property> propertyArrayList;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "getItem " + position);
            return FragmentMainListings.getInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return tabTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
//            switch (position) {
//                case 0:
//                    return "SECTION 1";
//                case 1:
//                    return "SECTION 2";
//                case 2:
//                    return "SECTION 3";
//            }
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }


}
