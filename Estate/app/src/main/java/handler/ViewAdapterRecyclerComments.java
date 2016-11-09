package handler;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Comment;
import entities.Favourite;
import entities.Property;
import entities.User;
import estateco.estate.R;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class ViewAdapterRecyclerComments extends RecyclerView.Adapter<ViewAdapterRecyclerComments.MyViewHolder> {
    private static final String TAG = ViewAdapterRecyclerComments.class.getSimpleName();
    private final LayoutInflater inflator;
    private Fragment fragment;
    private ArrayList<Comment> commentArrayList;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;
    private User owner;
    private Property property;
    private Favourite favourite;

    public ViewAdapterRecyclerComments(Fragment fragment, ArrayList<Comment> inboxArrayList, User owner) {
        inflator = LayoutInflater.from(fragment.getContext());
        this.fragment = fragment;
        this.commentArrayList = inboxArrayList;

        userCtrl = new UserCtrl(fragment.getActivity());
        propertyCtrl = new PropertyCtrl(fragment.getActivity());
        favouriteCtrl = new FavouriteCtrl(fragment.getActivity());
        this.user = userCtrl.getUserDetails();
        this.owner = owner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.customview_comments, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Comment comment = commentArrayList.get(position);
        holder.tvCmmtUserName.setText(comment.getSender().getName());
        holder.tvCmmtMessage.setText(comment.getCommentmessage());
        holder.tvCmmtCreateddate.setText(comment.getCreateddate());

        if (owner.getUserID().equals(comment.getSender().getUserID()))
            holder.tvCmmtUserTitle.setText("Owner");
        else
            holder.tvCmmtUserTitle.setText("User");



    }


    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCmmtUserTitle, tvCmmtUserName, tvCmmtMessage, tvCmmtCreateddate, houserOwner;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCmmtUserTitle = (TextView) itemView.findViewById(R.id.TVCmmtUserTitle);
            tvCmmtUserName = (TextView) itemView.findViewById(R.id.TVCmmtUserName);
            tvCmmtMessage = (TextView) itemView.findViewById(R.id.TVCmmtMessage);
            tvCmmtCreateddate = (TextView) itemView.findViewById(R.id.TVCmmtCreatedDate);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
