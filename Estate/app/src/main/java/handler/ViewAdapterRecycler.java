package handler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Property;
import entities.User;
import estateco.estate.FragmentMainListings;
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

public class ViewAdapterRecycler extends RecyclerView.Adapter<ViewAdapterRecycler.MyViewHolder> implements Filterable {
    private static final String TAG = ViewAdapterRecycler.class.getSimpleName();
    private final LayoutInflater inflator;
    private Fragment fragment;
    private ArrayList<Property> propertyArrayList;
    private ArrayList<Favourite> favouriteArrayList;
    private ArrayList<Property> filterArrayList;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;
    private CustomFilter customFilter;
    private Property property;
    private Favourite favourite;
    private LruCache<String, Bitmap> mMemoryCache;

    public ViewAdapterRecycler(Fragment fragment, ArrayList<Property> propertyArrayList) {
        inflator = LayoutInflater.from(fragment.getContext());
        this.fragment = fragment;
        this.propertyArrayList = propertyArrayList;
        this.filterArrayList = propertyArrayList;
        userCtrl = new UserCtrl(fragment.getActivity());
        propertyCtrl = new PropertyCtrl(fragment.getActivity());
        favouriteCtrl = new FavouriteCtrl(fragment.getActivity());
        user = userCtrl.getUserDetails();


        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in bytes rather than number
                // of items.
                return bitmap.getByteCount();
            }

        };
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
        } else {
            holder.tvProDetStatus.setTextColor(Color.DKGRAY);
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

        System.gc();
        String imageData = property.getImage();
        Bitmap bitmap = FragmentMainListings.getBitmapFromCache(property.getPropertyID());
        if (imageData.isEmpty())
            holder.imgvProDetImage.setImageResource(R.drawable.ic_menu_camera);
        else {
            if (bitmap != null) {
                holder.imgvProDetImage.setImageBitmap(bitmap);
            } else {
                new ImageHandler_DECODE(holder.imgvProDetImage).execute(imageData, property.getPropertyID());
            }
        }


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

    @Override
    public Filter getFilter() {
        if (customFilter == null)
            customFilter = new CustomFilter();
        return customFilter;
    }

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.i(TAG, "performFiltering" + constraint);
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                // CONSTRAINT TO lower
                constraint = constraint.toString().toLowerCase();
                ArrayList<Property> filters = new ArrayList<>();
                for (Property filtered : filterArrayList) {
                    if (filtered.getOwner().getName().toLowerCase().contains(constraint) ||
                            filtered.getFlatType().toLowerCase().contains(constraint) ||
                            filtered.getStreetname().toLowerCase().contains(constraint) ||
                            filtered.getDealType().toLowerCase().contains(constraint) ||
                            filtered.getWholeapartment().toLowerCase().contains(constraint)) {
                        filters.add(filtered);
                    }
                }
                filterResults.count = filters.size();
                filterResults.values = filters;
            } else {
                filterResults.count = filterArrayList.size();
                filterResults.values = filterArrayList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.i(TAG, "publishResults");
            propertyArrayList = (ArrayList<Property>) results.values;
            TextView textView = (TextView) fragment.getView().findViewById(R.id.TVAllListingCount);

            if (getItemCount() > 1)
                textView.setText(getItemCount() + " records");
            else
                textView.setText(getItemCount() + " record");
            notifyDataSetChanged();
        }
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
