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


    TextView PropertyID, Title, Desc, Price, OwnerName, AddressName;
    ImageView Photo;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.viewstyle_row, parent, false);
        row.setPadding(22, 0, 12, 22);

        PropertyID = (TextView) row.findViewById(R.id.TVPropertyID);
        Title = (TextView) row.findViewById(R.id.TVLblUpdateTitle);
        Desc = (TextView) row.findViewById(R.id.TVDesc);
        Price = (TextView) row.findViewById(R.id.TVPrice);
        OwnerName = (TextView) row.findViewById(R.id.TVOwnerName);
        AddressName = (TextView) row.findViewById(R.id.TVAddressName);
        Photo = (ImageView) row.findViewById(R.id.IMGVPhoto);

        PropertyID.setText(properties.get(position).getPropertyID());
        Title.setText(" - " + properties.get(position).getTitle());
        Desc.setText(properties.get(position).getDescription());
        Price.setText("Price : SGD$" + properties.get(position).getPrice());
        AddressName.setText("Address : " + properties.get(position).getAddressName());
        OwnerName.setText("Owner : " + properties.get(position).getOwner().getName());

        // check if photo is empty
        String photoData = properties.get(position).getPhoto();
        if (photoData.isEmpty())
            Photo.setImageResource(R.drawable.ic_menu_camera);
        else
            Photo.setImageBitmap(ImageHandler.getInstance().decodeStringToImage(photoData));


        return row;
    }
}
