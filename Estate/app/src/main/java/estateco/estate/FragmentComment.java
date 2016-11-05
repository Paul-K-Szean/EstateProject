package estateco.estate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controllers.EstateConfig;
import controllers.FavouriteCtrl;
import controllers.InboxCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Inbox;
import entities.Property;
import entities.User;
import enums.InboxType;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.JSONHandler;
import handler.Utility;
import handler.ViewAdapterRecycler;

import static controllers.InboxCtrl.KEY_INBOXSTATUS_DISPLAY;
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
    Button btnSendComment;
    EditText etComment;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;
    private User subscriber;
    private Property property;
    private Favourite favourite;
    private Inbox inbox;
    private ArrayList<Inbox> inboxArrayList;
    private InboxCtrl inboxCtrl;
    private RecyclerView recycler;
    private ViewAdapterRecycler viewAdapter;
    private String valType, valTitle, valMessage;
    private String savedPropertyID;
    private ArrayList<User> subscribers;

    public FragmentComment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        savedInstanceState = getActivity().getIntent().getExtras();
        savedPropertyID = savedInstanceState.getString(KEY_PROPERTY_PROPERTYID);
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
                    inbox = new Inbox(user, savedPropertyID, valType, valTitle, valMessage, KEY_INBOXSTATUS_DISPLAY);
                    inboxCtrl.serverNewInbox(FragmentComment.this, inbox);
                    etComment.setText("");
                }
            }
        });


        etComment = (EditText) view.findViewById(R.id.ETComment);
        Utility.showSoftKeyboard(getActivity());

        // retrieve comments
        if (savedInstanceState != null) {
            inbox = new Inbox(savedPropertyID, InboxType.PropertyComment.toString());
            inboxCtrl.serverGetInboxPropertyComment(FragmentComment.this, inbox);
        } else {
            // textView.setText("couldn't find save instance state");
        }

        return view;
    }


    public void serverGetPropertySubscriber() {
        Log.i(TAG, "serverGetPropertySubscriber");


        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_PROPERTY_PROPERTYID, property.getPropertyID());

        if (paramValues != null) {
            new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_UPDATEUSERPROPERTY, paramValues, getActivity(), new AsyncTaskResponse() {
                @Override
                public void onAsyncTaskResponse(String response) {
                    try {
                        subscribers = new ArrayList<>();
                        JSONArray jsonArray = JSONHandler.getResultAsArray(getActivity(), response);
                        if (jsonArray != null) {
                            for (int index = 0; index < jsonArray.length(); index++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(index);
                                subscriber = new User(
                                        jsonObject.getString(UserCtrl.KEY_USERID),
                                        jsonObject.getString(UserCtrl.KEY_NAME),
                                        jsonObject.getString(UserCtrl.KEY_EMAIL),
                                        jsonObject.getString(UserCtrl.KEY_CONTACT));
                                subscribers.add(subscriber);
                            }
                        } else {
                            String result = JSONHandler.getResultAsString(getActivity(), response);
                            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).execute();
        }

    }


}
