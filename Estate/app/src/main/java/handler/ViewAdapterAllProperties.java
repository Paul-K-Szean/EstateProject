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

import controllers.PropertyCtrl;
import entities.Lease;
import entities.Property;
import entities.Sale;
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


    TextView PropertyID, FlatType, DealType, Title, Desc, Price, OwnerName, AddressName, Status;
    ImageView Photo;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.viewstyle_row, parent, false);
        row.setPadding(22, 0, 12, 22);

        PropertyID = (TextView) row.findViewById(R.id.TVLblPropertyID);
        FlatType = (TextView) row.findViewById(R.id.TVLblFlatType);
        DealType = (TextView) row.findViewById(R.id.TVLblDealType);
        Title = (TextView) row.findViewById(R.id.TVLblTitle);
        Desc = (TextView) row.findViewById(R.id.TVLblDesc);
        Price = (TextView) row.findViewById(R.id.TVLblPrice);
        OwnerName = (TextView) row.findViewById(R.id.TVLblOwnerName);
        AddressName = (TextView) row.findViewById(R.id.TVLblAddressName);
        Photo = (ImageView) row.findViewById(R.id.IMGVLblImage);
        Status = (TextView) row.findViewById(R.id.TVLblStatus);


        if (properties.get(position).getStatus().toString().equals("open"))
            Status.setText("");
        else
            Status.setText(properties.get(position).getStatus());

        PropertyID.setText(properties.get(position).getPropertyID());
        FlatType.setText("Flat Type : " + properties.get(position).getFlatType());
        int roomCount = Integer.valueOf(properties.get(position).getNoOfbedrooms().trim());

        if (properties.get(position) instanceof Lease) {
            // if leasing only rooms
            if (((Lease) properties.get(position)).getWholeApartment().toString().equals("room")) {
                if (roomCount <= 1)
                    DealType.setText(" - " + properties.get(position).getDealType()
                            + " - "
                            + roomCount
                            + " "
                            + ((Lease) properties.get(position)).getWholeApartment());
                else
                    DealType.setText(" - " + properties.get(position).getDealType()
                            + " - "
                            + roomCount
                            + " "
                            + ((Lease) properties.get(position)).getWholeApartment() + "s");
            }
            // if leasing whole apartment
            if (((Lease) properties.get(position)).getWholeApartment().toString().equals(PropertyCtrl.KEY_PROPERTY_WHOLEAPARTMENT.toString())) {
                DealType.setText(" - " + properties.get(position).getDealType()
                        + " - "
                        + ((Lease) properties.get(position)).getWholeApartment());
            }
        }
        if (properties.get(position) instanceof Sale) {
            DealType.setText(" - " + properties.get(position).getDealType());
        }


        Title.setText(properties.get(position).getTitle());
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
