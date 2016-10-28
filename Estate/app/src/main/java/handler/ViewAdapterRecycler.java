package handler;

import android.content.Intent;
import android.graphics.Color;
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
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Property;
import entities.User;
import estateco.estate.FragmentUpdateUserProperty;
import estateco.estate.FragmentUserListings;
import estateco.estate.PropertyDetailsUI;
import estateco.estate.R;

import static android.view.View.GONE;
import static controllers.PropertyCtrl.KEY_ACTION_DECREASEFAVOURITE;
import static controllers.PropertyCtrl.KEY_ACTION_INCREASEFAVOURITE;
import static controllers.PropertyCtrl.KEY_PROPERTY_PROPERTYID;

/**
 * Created by Paul K Szean on 21/10/2016.
 */

public class ViewAdapterRecycler extends RecyclerView.Adapter<ViewAdapterRecycler.MyViewHolder> {
    private static final String TAG = ViewAdapterRecycler.class.getSimpleName();
    private final LayoutInflater inflator;
    private Fragment fragment;
    private ArrayList<Property> propertyArrayList;
    private ArrayList<Favourite> favouriteArrayList;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;
    private Property property;
    private Favourite favourite;


    public ViewAdapterRecycler(Fragment fragment, ArrayList<Property> propertyArrayList) {
        inflator = LayoutInflater.from(fragment.getContext());
        this.fragment = fragment;
        this.propertyArrayList = propertyArrayList;

        userCtrl = new UserCtrl(fragment.getActivity());
        propertyCtrl = new PropertyCtrl(fragment.getActivity());
        favouriteCtrl = new FavouriteCtrl(fragment.getActivity());
        user = userCtrl.getUserDetails();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.customview_propertydetails, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Property property = propertyArrayList.get(position);

        user = userCtrl.getUserDetails();
        User owner = new User(property.getOwner().getUserID(), property.getOwner().getName(), property.getOwner().getEmail(), property.getOwner().getContact());
        // deal details
        holder.tvProDetDealType.setText(property.getDealType());
        holder.tvProDetWholeApartment.setText(property.getWholeapartment());
        // general details
        holder.tvProDetPropertyID.setText(property.getPropertyID());
        holder.tvProDetStatus.setText(property.getStatus());
        String valStatus = property.getStatus();
        if (valStatus.equals("closed")) {
            holder.tvProDetStatus.setTextColor(Color.RED);
        }

        holder.tvProDetFavouriteCount.setText(property.getFavouritecount());
        holder.tvProDetViewCount.setText(property.getViewcount());
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

        Log.i(TAG, "Loading property: " + property.getPropertyID());

        // favourite icon
        favourite = favouriteCtrl.getUserFavouritePropertyDetails(user, property);

        if (favourite != null) {
            holder.imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite);
        } else {
            holder.imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite_outline);
        }

        holder.imgvLblFavouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Favourite clicked propertyID: " + property.getPropertyID());
                if ((favourite = favouriteCtrl.getUserFavouritePropertyDetails(user, property)) != null) {
                    Log.i(TAG, "Trying to un-favourite this property. ");
                    holder.imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite_outline);
                    // decrease favourite count of this property
                    favouriteCtrl.serverUpdatePropertyCount(fragment, property, KEY_ACTION_DECREASEFAVOURITE, holder.tvProDetFavouriteCount);
                } else {
                    Log.i(TAG, "Trying to favourite this property. ");
                    holder.imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite);
                    // increase favourite count of this property
                    favouriteCtrl.serverUpdatePropertyCount(fragment, property, KEY_ACTION_INCREASEFAVOURITE, holder.tvProDetFavouriteCount);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return propertyArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgvProDetImage, imgvLblFavouriteIcon, imgvLblViewIcon;
        TextView tvProDetPropertyID, tvProDetFlatType, tvProDetBlock, tvProDetStreetName, tvProDetFloorLevel, tvProDetFloorArea,
                tvProDetPrice, tvProDetStatus, tvProDetDealType, tvProDetTitle, tvProDetDesc, tvProDetFurnishLevel,
                tvProDetBedroomCount, tvProDetBathroomCount, tvProDetFavouriteCount, tvProDetViewCount, tvProDetWholeApartment,
                tvProDetOwnerName, tvProDetOwnerEmail, tvProDetOwnerContact, tvHeaderAddress;


        public MyViewHolder(View itemView) {
            super(itemView);

            // deal details
            tvProDetDealType = (TextView) itemView.findViewById(R.id.TVLblDealType);
            tvProDetWholeApartment = (TextView) itemView.findViewById(R.id.TVLblWholeApartment);
            // general details
            tvProDetPropertyID = (TextView) itemView.findViewById(R.id.TVLblPropertyID);
            tvProDetStatus = (TextView) itemView.findViewById(R.id.TVLblStatus);
            tvProDetFavouriteCount = (TextView) itemView.findViewById(R.id.TVLblFavouriteCount);
            tvProDetViewCount = (TextView) itemView.findViewById(R.id.TVLblViewCount);

            imgvLblFavouriteIcon = (ImageView) itemView.findViewById(R.id.IMGVLblFavouriteIcon);
            imgvLblViewIcon = (ImageView) itemView.findViewById(R.id.IMGVLblViewIcon);
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
