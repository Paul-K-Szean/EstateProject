package handler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import estateco.estate.R;

/**
 * Created by Paul K Szean on 30/9/2016.
 */

public class FragmentHandler {



    public static void loadFragment(Fragment currentFragment, Fragment nextFragment) {
        FragmentTransaction fragmentTransaction = currentFragment.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.addToBackStack(currentFragment.getTag());  // add to stack for back button
        fragmentTransaction.commit();
    }

    public static void loadFragment(Fragment currentFragment, Fragment nextFragment, Bundle savedInstanceState) {

        FragmentTransaction fragmentTransaction = currentFragment.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.addToBackStack(currentFragment.getTag());  // add to stack for back button
        nextFragment.setArguments(savedInstanceState);  // pass data to nextFragment
        fragmentTransaction.commit();
    }
}
