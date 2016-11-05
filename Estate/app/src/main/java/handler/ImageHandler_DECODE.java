package handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import estateco.estate.FragmentMainListings;

/**
 * Created by Paul K Szean on 30/9/2016.
 */

public class ImageHandler_DECODE extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = ImageHandler_DECODE.class.getSimpleName();
    private final static int TARGET_IMAGEVIEW_WDITH = 200;
    private final static int TARGET_IMAGEVIEW_HEIGHT = 200;
    private final WeakReference<ImageView> imageViewReference;

    public ImageHandler_DECODE(ImageView imageView) {
        imageViewReference = new WeakReference<>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        byte[] valueDecoded = Base64.decode(params[0], Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(valueDecoded, 0, valueDecoded.length);
        FragmentMainListings.setBitmapToMemoryCache(params[1], bitmap);
        return bitmap;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }


}
