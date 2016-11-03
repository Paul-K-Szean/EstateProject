package handler;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import controllers.FavouriteCtrl;
import controllers.PropertyCtrl;
import controllers.UserCtrl;
import entities.Favourite;
import entities.Property;
import entities.User;
import estateco.estate.R;

import static android.view.View.GONE;

/**
 * Created by Paul K Szean on 26/9/2016.
 */

public class ViewAdapterListView extends BaseAdapter {
    private static final String TAG = ViewAdapterListView.class.getSimpleName();
    //Title, Desc,
    ImageView imgvProDetImage, imgvLblFavouriteIcon;
    TextView tvProDetPropertyID, tvProDetTitle, tvProDetDesc, tvProDetFlatType, tvProDetDealType, tvProDetFurnishLevel, tvProDetPrice,
            tvProDetBedroomCount, tvProDetBathroomCount, tvProDetFloorArea, tvProDetStreetName, tvProDetFloorLevel, tvProDetBlock,
            tvProDetStatus, tvProDetWholeApartment, tvProDetOwnerName, tvProDetOwnerEmail, tvProDetOwnerContact, tvHeaderAddress,
            tvLblPropertyFavouriteCount;
    private Fragment fragment;
    private ArrayList<Property> propertyArrayList;
    private ArrayList<Favourite> favouriteArrayList;
    private Map<String, String> propertyFavouriteCountList;
    private UserCtrl userCtrl;
    private PropertyCtrl propertyCtrl;
    private FavouriteCtrl favouriteCtrl;
    private User user;
    private Property property;
    private Favourite favourite;

    public ViewAdapterListView(Fragment fragment, ArrayList<Property> propertyArrayList, Map<String, String> propertyFavouriteCountList) {
        Log.i(TAG, "ViewAdapterListView()");
        this.fragment = fragment;
        this.propertyArrayList = propertyArrayList;
        this.propertyFavouriteCountList = propertyFavouriteCountList;
        userCtrl = new UserCtrl(fragment.getActivity());
        favouriteCtrl = new FavouriteCtrl(fragment.getActivity());
        user = userCtrl.getUserDetails();

    }

    @Override
    public int getCount() {
        // Log.i(TAG, "getCount() " + properties.size());
        return propertyArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return propertyArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.customview_propertydetails, parent, false);

        // deal details
        tvProDetDealType = (TextView) itemView.findViewById(R.id.TVLblDealType);
        tvProDetWholeApartment = (TextView) itemView.findViewById(R.id.TVLblWholeApartment);
        // general details
        tvProDetPropertyID = (TextView) itemView.findViewById(R.id.TVLblPropertyID);
        tvProDetStatus = (TextView) itemView.findViewById(R.id.TVLblStatus);
        tvLblPropertyFavouriteCount = (TextView) itemView.findViewById(R.id.TVLblFavouriteCount);
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

        property = (Property) getItem(position);
        User owner = new User(property.getOwner().getUserID(), property.getOwner().getName(), property.getOwner().getEmail(), property.getOwner().getContact());
        Log.i(TAG, "Loading property: " + property.getPropertyID());
        // deal details
        tvProDetDealType.setText(property.getDealType());
        tvProDetWholeApartment.setText(property.getWholeapartment());
        // general details
        tvProDetPropertyID.setText(property.getPropertyID());
        tvProDetStatus.setText(property.getStatus());
        // address details
        tvHeaderAddress.setVisibility(GONE);
        tvProDetFloorLevel.setVisibility(GONE);
        tvProDetStreetName.setVisibility(GONE);
        tvProDetFloorLevel.setText("Storey " + property.getFloorlevel() + " @");
        tvProDetStreetName.setText(property.getStreetname());
        // house details
        String imageData = property.getImage();
        if (imageData.isEmpty())
            imgvProDetImage.setImageResource(R.drawable.ic_menu_camera);
        else {
            // imgvPropDetImage.setImageBitmap(ImageHandler_ENCODE.decodeStringToImage(valProDetImage));
            new ImageHandler_DECODE(imgvProDetImage).execute(imageData);
        }
        tvProDetFlatType.setText(property.getFlatType());
        tvProDetPrice.setText("$" + Utility.formatStringNumber(property.getPrice()));
        tvProDetFloorArea.setText(property.getFloorarea() + "sqm");
        // owner details
        tvProDetOwnerName.setText(owner.getName());

        // favourite icon
        favourite = favouriteCtrl.getUserFavouritePropertyDetails(user, property);

        if (favourite != null) {
            imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite);
        } else {
            imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite_outline);
        }

        // favourite count
        if (propertyFavouriteCountList.get(property.getPropertyID()) != null)
            tvLblPropertyFavouriteCount.setText(propertyFavouriteCountList.get(property.getPropertyID()));
        else
            tvLblPropertyFavouriteCount.setText("0");

        imgvLblFavouriteIcon.setOnClickListener(new View.OnClickListener() {

            int value;

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Favourite clicked propertyID: " + property.getPropertyID());
                if ((favourite = favouriteCtrl.getUserFavouritePropertyDetails(user, property)) != null) {
                    Log.i(TAG, "Trying to un-favourite this property. ");
                    imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite_outline);
                    // un-favourite property
                    favouriteCtrl.serverDeleteFavouriteProperty(fragment, user, property);

                } else {
                    Log.i(TAG, "Trying to favourite this property. ");
                    imgvLblFavouriteIcon.setImageResource(R.drawable.ic_action_favourite);
                    // favourite property
                    favouriteCtrl.serverNewFavouriteProperty(fragment, user, property);

                }


            }
        });


        return itemView;
    }
}
