package handler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Paul K Szean on 30/9/2016.
 */

public class ImageHandler_ENCODE extends AsyncTask<Void, Void, String> {
    private static final String TAG = ImageHandler_ENCODE.class.getSimpleName();

    private Bitmap bitmap;
    private Activity activity;


    public ImageHandler_ENCODE(Activity activity, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.activity = activity;
    }

    // Decode image in background.
    @Override
    protected String doInBackground(Void... params) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        System.gc();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(String encodedImage) {

    }

}
