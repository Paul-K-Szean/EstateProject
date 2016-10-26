package estateco.estate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import controllers.FavouriteCtrl;
import controllers.InboxCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Inbox;
import entities.Property;
import entities.User;
import enums.InboxType;
import handler.Utility;
import handler.ViewAdapterRecycler;

import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentComment extends Fragment {
    private static final String TAG = FragmentPropertyDetails.class.getSimpleName();
    private static final String TAG_FARVOURITE = "favourite";
    private static final String TAG_VIEW = "view";
    private static final String TAG_PHONECALL = "phonecall";
    private static final String TAG_COMMENT = "phonemessage";

    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;

    private User user;
    private User owner;
    private Property property;
    private Favourite favourite;
    private Inbox inbox;
    private ArrayList<Inbox> inboxArrayList;
    private InboxCtrl inboxCtrl;

    public FragmentComment() {
        // Required empty public constructor
    }

    Button btnSendComment;
    EditText etComment;
    private RecyclerView recycler;
    private ViewAdapterRecycler viewAdapter;
    private String valType, valTitle, valMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        savedInstanceState = getActivity().getIntent().getExtras();
        final String savedPropertyID = savedInstanceState.getString(KEY_PROPERTY_PROPERTYID);
        // setup ctrl objects
        userCtrl = new UserCtrl(getActivity());
        propertyCtrl = new PropertyCtrl(getActivity());
        favouriteCtrl = new FavouriteCtrl(getActivity());
        inboxCtrl = new InboxCtrl(getActivity());
        user = userCtrl.getUserDetails();

        // set controls
        btnSendComment = (Button) view.findViewById(R.id.BTNSendComment);
        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideSoftKeyboard(getActivity());
                valType = InboxType.PropertyComment.toString();
                valTitle = InboxType.PropertyComment.toString();
                valMessage = etComment.getText().toString();
                if (valMessage.isEmpty()) {
                    Toast.makeText(getActivity(), "Nothing to post.", Toast.LENGTH_SHORT).show();
                } else {
                    inbox = new Inbox(user.getUserID(), savedPropertyID, valType, valTitle, valMessage);
                    inboxCtrl.serverNewInbox(FragmentComment.this, inbox);
                    etComment.setText("");
                }
            }
        });


        etComment = (EditText) view.findViewById(R.id.ETComment);
        Utility.showSoftKeyboard(getActivity());

        if (savedInstanceState != null) {
            //Restore the fragment's state here
            //etComment.setText(savedInstanceState.getString(KEY_PROPERTY_PROPERTYID));
            inbox = new Inbox(savedPropertyID, InboxType.PropertyComment.toString());
            inboxCtrl.serverGetInboxPropertyComment(FragmentComment.this, inbox);
        } else {
            // textView.setText("couldn't find save instance state");
        }


        // display comments
//        recycler = (RecyclerView) getActivity().findViewById(R.id.recycleView);
//      viewAdapter = new ViewAdapterRecycler(getActivity(), propertyArrayList);
//      recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
//      recycler.setVisibility(VISIBLE);
//      recycler.setAdapter(viewAdapter);


        return view;
    }


}
