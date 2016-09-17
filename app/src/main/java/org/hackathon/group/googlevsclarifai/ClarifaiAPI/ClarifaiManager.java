package org.hackathon.group.googlevsclarifai.ClarifaiAPI;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;

import org.hackathon.group.googlevsclarifai.Credentials;
import org.hackathon.group.googlevsclarifai.MainActivity;
import org.hackathon.group.googlevsclarifai.TextViewManager;

import java.io.ByteArrayOutputStream;

/**
 * Created by jellio on 9/16/16.
 */
public class ClarifaiManager {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final ClarifaiClient client = new ClarifaiClient(Credentials.CLIENT_ID, Credentials.CLIENT_SECRET);

    private Bitmap bitmap;
    private TextViewManager txtManager;
    private long[] endTime;

    public ClarifaiManager(Bitmap bitmap, TextViewManager txtManager, long[] endTime) {
        this.bitmap = bitmap;
        this.txtManager = txtManager;
        this.endTime = endTime;
    }

    public void runClarifai() {
        // Run recognition on a background thread since it makes a network call.
        new AsyncTask<Bitmap, Void, RecognitionResult>() {
            @Override protected RecognitionResult doInBackground(Bitmap... bitmaps) {
                return recognizeBitmap(bitmaps[0]);
            }
            @Override protected void onPostExecute(RecognitionResult result) {
                generateReturnString(result);
            }
        }.execute(bitmap);
    }

    /** Sends the given bitmap to Clarifai for recognition and returns the result. */
    private RecognitionResult recognizeBitmap(Bitmap bitmap) {
        try {
            // Scale down the image. This step is optional. However, sending large images over the
            // network is slow and  does not significantly improve recognition performance.
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 320,
                    320 * bitmap.getHeight() / bitmap.getWidth(), true);

            // Compress the image as a JPEG.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
            byte[] jpeg = out.toByteArray();

            // Send the JPEG to Clarifai and return the result.
            return client.recognize(new RecognitionRequest(jpeg)).get(0);
        } catch (ClarifaiException e) {
            Log.e(TAG, "Clarifai error", e);
            return null;
        }
    }

    /** Updates the UI by displaying tags for the given result. */
    private void generateReturnString(RecognitionResult result) {
        if (result != null) {
            if (result.getStatusCode() == RecognitionResult.StatusCode.OK) {
                // Display the list of tags in the UI.
                StringBuilder b = new StringBuilder();
                int counter = 0;
                for (Tag tag : result.getTags()) {
                    if (tag.getName().equals("no person")) {
                        continue;
                    }
                    b.append(b.length() > 0 ? ", " : "").append(tag.getName());
                    counter++;
                    if (counter == 5) {
                        break;
                    }
                }
                txtManager.updateToDone("clarifai", b.toString());
                endTime[0] = System.currentTimeMillis();
            } else {
                // Error recognizing image
            }
        } else {
            // Error
        }
    }

}
