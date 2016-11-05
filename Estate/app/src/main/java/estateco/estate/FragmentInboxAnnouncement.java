package estateco.estate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import controllers.UserCtrl;
import handler.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentInboxAnnouncement extends Fragment {
    private static final String TAG = FragmentInboxAnnouncement.class.getSimpleName();
    private UserCtrl userCtrl;

    public FragmentInboxAnnouncement() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentInboxAnnouncement newInstance(int sectionNumber) {
        FragmentInboxAnnouncement fragment = new FragmentInboxAnnouncement();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView()");
        Utility.hideSoftKeyboard(getActivity());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox_announcement, container, false);
        savedInstanceState = getArguments();


        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());

        setControls(view);


        return view;
    }

    private void setControls(View view) {

    }

}
