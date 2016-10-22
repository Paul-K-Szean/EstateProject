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

import entities.Property;
import entities.User;
import estateco.estate.FragmentUpdateUserProperty;
import estateco.estate.FragmentUserListings;
import estateco.estate.R;
import estateco.estate.PropertyDetailsUI;

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

    public ViewAdapter(Fragment fragment, ArrayList<Property> property) {
        this.fragment = fragment;
        inflator = LayoutInflater.from(fragment.getContext());
        propertyArrayList = property;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.viewstyle_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Property property = propertyArrayList.get(position);
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

    }


    @Override
    public int getItemCount() {
        return propertyArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgvProDetImage;
        TextView tvProDetPropertyID, tvProDetTitle, tvProDetDesc, tvProDetFlatType, tvProDetDealType, tvProDetFurnishLevel, tvProDetPrice,
                tvProDetBedroomCount, tvProDetBathroomCount, tvProDetFloorArea, tvProDetStreetName, tvProDetFloorLevel, tvProDetBlock,
                tvProDetStatus, tvProDetWholeApartment, tvProDetOwnerName, tvProDetOwnerEmail, tvProDetOwnerContact, tvHeaderAddress;

        public MyViewHolder(View itemView) {
            super(itemView);

            // deal details
            tvProDetDealType = (TextView) itemView.findViewById(R.id.TVLblDealType);
            tvProDetWholeApartment = (TextView) itemView.findViewById(R.id.TVLblWholeApartment);
            // general details
            tvProDetPropertyID = (TextView) itemView.findViewById(R.id.TVLblPropertyID);
            tvProDetStatus = (TextView) itemView.findViewById(R.id.TVLblStatus);
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
            else
                fragment.startActivity(new Intent(fragment.getActivity(), PropertyDetailsUI.class).putExtras(args));

        }
    }
}
