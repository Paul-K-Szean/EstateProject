package controllers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Comment;
import entities.Notification;
import entities.Property;
import entities.User;
import estateco.estate.FragmentComment;
import estateco.estate.R;
import handler.AsyncTaskHandler;
import handler.AsyncTaskResponse;
import handler.ErrorHandler;
import handler.JSONHandler;
import handler.SQLiteHandler;
import handler.SessionHandler;
import handler.ViewAdapterRecyclerComments;

import static android.view.View.VISIBLE;
import static controllers.EstateConfig.URL_GETCOMMENT;
import static controllers.UserCtrl.KEY_CONTACT;
import static controllers.UserCtrl.KEY_EMAIL;
import static controllers.UserCtrl.KEY_NAME;
import static controllers.UserCtrl.KEY_USERID;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class CommentCtrl {

    private static final String TAG = CommentCtrl.class.getSimpleName();
    // table columns names
    public static String KEY_COMMENTID = "commentID";
    public static String KEY_SENDERID = "senderID";
    public static String KEY_RECIPIENTID = "recipientID";
    public static String KEY_COMMENTTITLE = "commenttitle";
    public static String KEY_COMMENTMESSAGE = "commentmessage";
    public static String KEY_COMMENTSTATUS = "commentstatus";
    public static String KEY_CREATEDDATE = "createddate";

    public static String KEY_DEFAULT_COMMENTTITLE = "Property Comment";
    public static String KEY_STATUS_DISPLAY = "Display";
    private SessionHandler session;
    private SQLiteHandler db;


    public CommentCtrl(Context context) {
        // SQLite database handler
        db = new SQLiteHandler(context);
    }

    public CommentCtrl(Context context, SessionHandler session) {
        // SQLite database handler
        db = new SQLiteHandler(context);
        this.session = session;
    }

    // **********************************************************************
    // ********************* REMOTE WAMP SERVER ACCESS **********************
    // **********************************************************************

    // create new property
    public void serverNewComment(final Fragment fragment, final Comment comment, final Property property) {
        Log.i(TAG, "serverNewComment");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_SENDERID, comment.getSender().getUserID());
        paramValues.put(KEY_RECIPIENTID, comment.getRecipientID());
        paramValues.put(KEY_COMMENTTITLE, comment.getCommenttitle());
        paramValues.put(KEY_COMMENTMESSAGE, comment.getCommentmessage());
        paramValues.put(KEY_COMMENTSTATUS, comment.getCommentstatus());

        new AsyncTaskHandler(Request.Method.POST, EstateConfig.URL_NEWCOMMENT, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                JSONObject jsonObject = JSONHandler.getResultAsObject(fragment.getActivity(), response);
                if (jsonObject != null) {
                    // server side created comment
                    Toast.makeText(fragment.getActivity(), "You have commented!", Toast.LENGTH_LONG).show();

                    // owner made comment
                    if (property.getOwner().getUserID().equals(comment.getSender().getUserID())) {
                        // push notification
                        Notification notification = new Notification(property.getOwner(), "Comment", comment.getSender().getName() + " has commented your listing!");
                        new NotificationCtrl().serverPushNotification(notification, property);
                    }

                } else {
                    String result = JSONHandler.getResultAsString(fragment.getActivity(), response);
                    Toast.makeText(fragment.getActivity(), result, Toast.LENGTH_SHORT).show();
                }

                if (fragment.getClass().getSimpleName().equals(FragmentComment.class.getSimpleName()))
                    // refresh recycle list
                    serverGetComment(fragment, comment.getRecipientID());

            }
        }).execute();


    }


    // get comments for a listings
    public void serverGetComment(final Fragment fragment, final String recipientID) {
        Log.i(TAG, "serverGetComment");
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put(KEY_RECIPIENTID, recipientID);
        // get user favourite listings from server
        new AsyncTaskHandler(Request.Method.POST, URL_GETCOMMENT, paramValues, fragment.getActivity(), new AsyncTaskResponse() {
            @Override
            public void onAsyncTaskResponse(String response) {
                displayComments(fragment, response);
            }
        }).execute();
    }

    private void displayComments(final Fragment fragment, String response) {
        try {
            Log.i(TAG, "displayComments");
            RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.recycleView);
            ArrayList<Comment> inboxArrayList = new ArrayList<>();
            JSONArray jsonArray = JSONHandler.getResultAsArray(fragment.getActivity(), response);
            if (jsonArray != null) {
                User owner = null;
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    User sender = new User(
                            jsonObject.getString(KEY_USERID),
                            jsonObject.getString(KEY_NAME),
                            jsonObject.getString(KEY_EMAIL),
                            jsonObject.getString(KEY_CONTACT));

                    owner = new User(
                            jsonObject.getString("ownerid"),
                            jsonObject.getString("ownername"),
                            jsonObject.getString("owneremail"),
                            jsonObject.getString("ownercontact"));


                    Comment comment = new Comment(
                            jsonObject.getString(KEY_COMMENTID),
                            sender,
                            jsonObject.getString(KEY_RECIPIENTID),
                            jsonObject.getString(KEY_COMMENTTITLE),
                            jsonObject.getString(KEY_COMMENTMESSAGE),
                            jsonObject.getString(KEY_COMMENTSTATUS),
                            jsonObject.getString(KEY_CREATEDDATE));
                    inboxArrayList.add(comment);
                    Log.i(TAG, "Comment by: " + comment.getSender().getUserID() + ", " + comment.getSender().getName());
                }

                ViewAdapterRecyclerComments viewAdapter = new ViewAdapterRecyclerComments(fragment, inboxArrayList, owner);
                recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
                recyclerView.setVisibility(VISIBLE);
                recyclerView.setAdapter(viewAdapter);

            }
        } catch (JSONException error) {
            ErrorHandler.errorHandler(fragment.getActivity(), error);
        }
    }
}