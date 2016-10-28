package handler;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Inbox;
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
    private ArrayList<Inbox> inboxArrayList;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;
    private Property property;
    private Favourite favourite;

    public ViewAdapterRecyclerComments(Fragment fragment, ArrayList<Inbox> inboxArrayList) {
        inflator = LayoutInflater.from(fragment.getContext());
        this.fragment = fragment;
        this.inboxArrayList = inboxArrayList;

        userCtrl = new UserCtrl(fragment.getActivity());
        propertyCtrl = new PropertyCtrl(fragment.getActivity());
        favouriteCtrl = new FavouriteCtrl(fragment.getActivity());
        user = userCtrl.getUserDetails();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.customview_comments, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Inbox inbox = inboxArrayList.get(position);
        holder.tvCmmtUserName.setText(inbox.getSender().getName());
        holder.tvCmmtMessage.setText(inbox.getInboxmessage());
        holder.tvCmmtCreateddate.setText(inbox.getCreateddate());
    }


    @Override
    public int getItemCount() {
        return inboxArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCmmtUserName, tvCmmtMessage, tvCmmtCreateddate;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCmmtUserName = (TextView) itemView.findViewById(R.id.TVCmmtUserName);
            tvCmmtMessage = (TextView) itemView.findViewById(R.id.TVCmmtMessage);
            tvCmmtCreateddate = (TextView) itemView.findViewById(R.id.TVCmmtCreatedDate);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
