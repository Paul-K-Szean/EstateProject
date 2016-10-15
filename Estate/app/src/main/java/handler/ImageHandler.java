package handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Paul K Szean on 30/9/2016.
 */

public class ImageHandler {
    private static ImageHandler imageHandler;

    public static ImageHandler getInstance() {
        if (imageHandler == null)
            imageHandler = new ImageHandler();
        return imageHandler;
    }

    public String encodeImagetoString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public Bitmap decodeStringToImage(String imageBytes) {
        // Decode data on other side, by processing encoded data
        byte[] valueDecoded = Base64.decode(imageBytes, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(valueDecoded, 0, valueDecoded.length);
        return bitmap;
    }





}
