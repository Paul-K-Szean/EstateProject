package handler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import entities.Property;
import estateco.estate.R;

/**
 * Created by Paul K Szean on 26/9/2016.
 */

public class ViewAdapterAllProperties extends BaseAdapter {
    private static final String TAG = ViewAdapterAllProperties.class.getSimpleName();
    Context context;
    ArrayList<Property> properties;
    Property property;

    public ViewAdapterAllProperties(Context context, ArrayList<Property> properties) {
        Log.i(TAG, "ViewAdapterAllProperties()");
        this.context = context;
        this.properties = properties;

    }

    @Override
    public int getCount() {
        // Log.i(TAG, "getCount() " + properties.size());
        return properties.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(TAG, "getItem()");
        return properties.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Title, Desc,
    TextView PropertyID, FlatType, DealType, Price, OwnerName, FloorLevel, StreetName, Status, FloorArea;
    ImageView Photo;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.viewstyle_row, parent, false);
        row.setPadding(22, 0, 12, 22);

        PropertyID = (TextView) row.findViewById(R.id.TVLblPropertyID);
        FlatType = (TextView) row.findViewById(R.id.TVLblFlatType);
        DealType = (TextView) row.findViewById(R.id.TVLblDealType);
//        Title = (TextView) row.findViewById(R.id.TVLblTitle);
//        Desc = (TextView) row.findViewById(R.id.TVLblDesc);
        Price = (TextView) row.findViewById(R.id.TVLblPrice);
        OwnerName = (TextView) row.findViewById(R.id.TVLblOwnerName);
        FloorLevel = (TextView) row.findViewById(R.id.TVLblFloorLevel);
        StreetName = (TextView) row.findViewById(R.id.TVLblStreetName);
        Photo = (ImageView) row.findViewById(R.id.IMGVLblImage);
        Status = (TextView) row.findViewById(R.id.TVLblStatus);
        FloorArea = (TextView) row.findViewById(R.id.TVLblFloorArea);

        property = properties.get(position);
        PropertyID.setText(property.getPropertyID());

        // check if photo is empty
        String photoData = property.getImage();
        // Log.i(TAG, "photoDAta : " + photoData);

        // deal details
        DealType.setText(" - " + properties.get(position).getDealType()
                + " - "
                + properties.get(position).getWholeapartment());

        // general details
        PropertyID.setText(property.getPropertyID());
        if (property.getStatus().toString().equals("open"))
            Status.setText("");
        else
            Status.setText(property.getStatus());
//        Title.setText(property.getTitle());
//        Desc.setText(property.getDescription());

        // address details
        FloorLevel.setText("Storey : " + property.getFloorlevel());
        StreetName.setText("Address : " + property.getStreetname());

        // house details
        if (photoData.isEmpty())
            Photo.setImageResource(R.drawable.ic_menu_camera);
        else
            Photo.setImageBitmap(ImageHandler.decodeStringToImage(photoData));

        FlatType.setText("Flat Type : " + property.getFlatType());
        FloorArea.setText("Area: " + property.getFloorarea() + "sqm");
        Price.setText("Price : SGD$" + Utility.formatStringNumber(property.getPrice()));

        // owner details
        OwnerName.setText("Owner : " + property.getOwner().getName());

        return row;
    }
}
