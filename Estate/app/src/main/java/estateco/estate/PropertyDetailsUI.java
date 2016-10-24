package estateco.estate;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.User;
import handler.SQLiteHandler;
import handler.SessionHandler;

public class PropertyDetailsUI extends AppCompatActivity {
    private static final String TAG = PropertyDetailsUI.class.getSimpleName();

    private SessionHandler session;
    private SQLiteHandler db;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;
    Toolbar toolBarTop, toolBarBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        savedInstanceState = getIntent().getExtras();
        // setup ctrl objects
        session = new SessionHandler(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        userCtrl = new UserCtrl(getApplicationContext(), session);
        propertyCtrl = new PropertyCtrl(getApplicationContext());
        favouriteCtrl = new FavouriteCtrl(getApplicationContext());
        user = userCtrl.getUserDetails();
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
        // sets the initial fragment to load for this activity
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new FragmentPropertyDetails());
        fragmentTransaction.commit();
        // tool bars
        toolBarTop = (Toolbar) findViewById(R.id.toolbar_top);
        toolBarTop.setTitle("Property Details");
        setSupportActionBar(toolBarTop);


        if (savedInstanceState.get("previousfragment").toString().equals(FragmentUserFavouriteListings.class.getSimpleName())) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolBarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolBarBottom.inflateMenu(R.menu.property_details_actionbar);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_action_searchQuery).setVisible(false);
        menu.findItem(R.id.menu_action_inbox).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
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
