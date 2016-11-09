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

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controllers.CommentCtrl;
import controllers.EstateConfig;
import controllers.FavouriteCtrl;
import controllers.NotificationCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Comment;
import entities.Favourite;
import entities.Property;
import entities.User;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.JSONHandler;
import handler.Utility;
import handler.ViewAdapterRecyclerProperty;

import static controllers.CommentCtrl.KEY_DEFAULT_COMMENTTITLE;
import static controllers.CommentCtrl.KEY_STATUS_DISPLAY;
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
    private Comment comment;
    private CommentCtrl commentCtrl;
    private NotificationCtrl notificatiinCtrl;
    private RecyclerView recycler;
    private ViewAdapterRecyclerProperty viewAdapter;
    private String valTitle, valMessage;
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
        commentCtrl = new CommentCtrl(getActivity());
        notificatiinCtrl = new NotificationCtrl();
        user = userCtrl.getUserDetails();

        // set controls
        btnSendComment = (Button) view.findViewById(R.id.BTNSendComment);
        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideSoftKeyboard(getActivity());

                valTitle = KEY_DEFAULT_COMMENTTITLE;
                valMessage = etComment.getText().toString();
                if (valMessage.isEmpty()) {
                    Toast.makeText(getActivity(), "Nothing to post.", Toast.LENGTH_SHORT).show();
                } else {
                    // get property details from server
                    Map<String, String> paramValues = new HashMap<>();
                    paramValues.put(PropertyCtrl.KEY_PROPERTY_PROPERTYID, savedPropertyID);
                    new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_GETPROPERTYDETAILS, paramValues, getActivity(), new AsyncTaskResponse() {
                        @Override
                        public void onAsyncTaskResponse(String response) {
                            try {
                                JSONObject propertyObj = JSONHandler.getResultAsObject(getActivity(), response);
                                // check for error in json
                                if (propertyObj != null) {
                                    User owner = new User(
                                            propertyObj.getString(UserCtrl.KEY_USERID),
                                            propertyObj.getString(UserCtrl.KEY_NAME),
                                            propertyObj.getString(UserCtrl.KEY_EMAIL),
                                            propertyObj.getString(UserCtrl.KEY_CONTACT));

                                    property = new Property(
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PROPERTYID),
                                            owner,
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLATTYPE),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_BLOCK),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_STREETNAME),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORLEVEL),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FLOORAREA),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_PRICE),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_IMAGE),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_STATUS),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DEALTYPE),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_TITLE),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_DESC),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FURNISHLEVEL),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_BEDROOMCOUNT),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_BATHROOMCOUNT),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_FAVOURITECOUNT),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_VIEWCOUNT),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT),
                                            propertyObj.getString(PropertyCtrl.KEY_PROPERTY_CREATEDDATE));

                                    comment = new Comment(user, savedPropertyID, valTitle, valMessage, KEY_STATUS_DISPLAY);
                                    commentCtrl.serverNewComment(FragmentComment.this, comment, property);
                                    etComment.setText("");
                                } else {
                                    String result = JSONHandler.getResultAsString(getActivity(), response);
                                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException error) {
                                error.printStackTrace();
                                ErrorHandler.errorHandler(getActivity(), error);
                            }

                        }
                    }).execute();
                }
            }
        });


        etComment = (EditText) view.findViewById(R.id.ETComment);
        Utility.showSoftKeyboard(getActivity());

        // retrieve comments
        if (savedInstanceState != null) {
            commentCtrl.serverGetComment(FragmentComment.this, savedPropertyID);
        }

        return view;
    }
}
