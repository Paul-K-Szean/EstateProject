package handler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import controllers.FavouriteCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Property;
import entities.User;
import estateco.estate.FragmentUpdateUserProperty;
import estateco.estate.FragmentUserListings;
import estateco.estate.PropertyDetailsUI;
import estateco.estate.R;

import static android.view.View.GONE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.MyViewHolder> {
    private static final String TAG = ViewAdapter.class.getSimpleName();

    private Fragment fragment;
    private ArrayList<Property> propertyArrayList;
    private final LayoutInflater inflator;
    private FavouriteCtrl favouriteCtrl;
    private UserCtrl userCtrl;
    private User user;
    private Favourite favourite;
    boolean isFavourited = false;
    ArrayList<Favourite> favouriteArrayList;

    public ViewAdapter(Fragment fragment, ArrayList<Property> property) {
        this.fragment = fragment;
        inflator = LayoutInflater.from(fragment.getContext());
        propertyArrayList = property;
        favouriteCtrl = new FavouriteCtrl(fragment.getActivity());
        userCtrl = new UserCtrl(fragment.getActivity());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.viewstyle_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        user = userCtrl.getUserDetails();
        final Property property = propertyArrayList.get(position);
        User owner = new User(property.getOwner().getUserID(), property.getOwner().getName(), property.getOwner().getEmail(), property.getOwner().getContact());
        // deal details
        holder.tvProDetDealType.setText(property.getDealType());
        holder.tvProDetWholeApartment.setText(property.getWholeapartment());
        // general details
        holder.tvProDetPropertyID.setText(property.getPropertyID());
        holder.tvProDetStatus.setText(property.getStatus());
        // address details
        holder.tvHeaderAddress.setVisibility(GONE);
        holder.tvProDetFloorLevel.setVisibility(GONE);
        holder.tvProDetStreetName.setVisibility(GONE);
        holder.tvProDetFloorLevel.setText("Storey " + property.getFloorlevel() + " @");
        holder.tvProDetStreetName.setText(property.getStreetname());
        // house details
        String imageData = property.getImage();
        if (imageData.isEmpty())
            holder.imgvProDetImage.setImageResource(R.drawable.ic_menu_camera);
        else
            holder.imgvProDetImage.setImageBitmap(ImageHandler.decodeStringToImage(imageData));
        holder.tvProDetFlatType.setText(property.getFlatType());
        holder.tvProDetPrice.setText("$" + Utility.formatStringNumber(property.getPrice()));
        holder.tvProDetFloorArea.setText(property.getFloorarea() + "sqm");
        // owner details
        holder.tvProDetOwnerName.setText(owner.getName());

//        favouriteArrayList = favouriteCtrl.getUserFavouriteProperties(user);
//        for (Favourite favourite : favouriteArrayList) {
//            Log.i(TAG, "propertyID(" + property.getPropertyID().toString() +
//                    ") == favourite.getPropertyID(" +
//                    favourite.getPropertyID().toString() + ") == " +
//                    (property.getPropertyID().toString().equals(favourite.getPropertyID().toString())));
//            // display favourite or un-favourite
//            if (property.getPropertyID().toString().equals(favourite.getPropertyID().toString())) {
//                isFavourited = true;
//                this.favourite = favourite;
//                holder.imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite);
//                break;
//            }
//        }

        holder.imgvLblFavouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavourited) {
                    isFavourited = false;
                    Log.i(TAG, "Trying to un-favourite this property. isFavourite value is = " + isFavourited);
                    holder.imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite_outline);
                    // un-favourite property
                    // favouriteCtrl.serverDeleteFavouriteProperty(fragment, favourite);
                } else {
                    isFavourited = true;
                    Log.i(TAG, "Trying to favourite this property. isFavourite value is = " + isFavourited);
                    holder.imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite);
                    favourite = new Favourite(user.getUserID(), property.getPropertyID());
                    // favourite property
                    // favouriteCtrl.serverNewFavouriteProperty(fragment, favourite);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return propertyArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgvProDetImage, imgvLblFavouriteIcon;
        TextView tvProDetPropertyID, tvProDetTitle, tvProDetDesc, tvProDetFlatType, tvProDetDealType, tvProDetFurnishLevel, tvProDetPrice,
                tvProDetBedroomCount, tvProDetBathroomCount, tvProDetFloorArea, tvProDetStreetName, tvProDetFloorLevel, tvProDetBlock,
                tvProDetStatus, tvProDetWholeApartment, tvProDetOwnerName, tvProDetOwnerEmail, tvProDetOwnerContact, tvHeaderAddress,
                tvLblPropertyFavouriteCount;

        public MyViewHolder(View itemView) {
            super(itemView);

            // deal details
            tvProDetDealType = (TextView) itemView.findViewById(R.id.TVLblDealType);
            tvProDetWholeApartment = (TextView) itemView.findViewById(R.id.TVLblWholeApartment);
            // general details
            tvProDetPropertyID = (TextView) itemView.findViewById(R.id.TVLblPropertyID);
            tvProDetStatus = (TextView) itemView.findViewById(R.id.TVLblStatus);
            tvLblPropertyFavouriteCount = (TextView) itemView.findViewById(R.id.TVLblPropertyFavouriteCount);
            imgvLblFavouriteIcon = (ImageView) itemView.findViewById(R.id.IMGVLblFavouriteIcon);

            // address details
            tvHeaderAddress = (TextView) itemView.findViewById(R.id.TVHderAddress);
            tvProDetFloorLevel = (TextView) itemView.findViewById(R.id.TVLblFloorLevel);
            tvProDetStreetName = (TextView) itemView.findViewById(R.id.TVLblStreetName);
            // house details
            imgvProDetImage = (ImageView) itemView.findViewById(R.id.IMGVLblImage);
            tvProDetFlatType = (TextView) itemView.findViewById(R.id.TVLblFlatType);
            tvProDetPrice = (TextView) itemView.findViewById(R.id.TVLblPrice);
            tvProDetFloorArea = (TextView) itemView.findViewById(R.id.TVLblFloorArea);
            // owner details
            tvProDetOwnerName = (TextView) itemView.findViewById(R.id.TVLblOwnerName);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            String itemID = ((TextView) v.findViewById(R.id.TVLblPropertyID)).getText().toString();
            Bundle args = new Bundle();
            args.putString(KEY_PROPERTY_PROPERTYID, itemID);
            Log.i(TAG, fragment.getClass().getSimpleName() + " " + FragmentUpdateUserProperty.class.getName());

            // check for action
            if (fragment.getClass().getSimpleName().contains(FragmentUserListings.class.getSimpleName()))
                FragmentHandler.loadFragment(fragment, new FragmentUpdateUserProperty(), args);
            else {
                args.putString("previousfragment", fragment.getClass().getSimpleName());
                fragment.startActivity(new Intent(fragment.getActivity(), PropertyDetailsUI.class).putExtras(args));
            }

        }
    }
}
