package org.hackathon.group.googlevsclarifai;

import org.hackathon.group.googlevsclarifai.ClarifaiAPI.ClarifaiManager;
import org.hackathon.group.googlevsclarifai.VisionAPI.GoogleVisionManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private Uri imageUri;
    private Bitmap imageBitmap;

    private Button btnVoteLeft, btnVoteRight;
    private TextView txtTagsLeft, txtTagsRight, txtVisionLoading, txtClarifaiLoading;
    private TextViewManager txtManager;
    private ImageView imgPictureTaken;
    private LinearLayout llPrePicture, llPostPicture;

    private int clarifaiTotal, visionTotal;
    private long[] clarifaiEndTime, visionEndTime;
    private long startTime, clarifaiTime, visionTime; // running totals

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private MediaPlayer rumble;
    private MediaPlayer bell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        /** Media player **/
        rumble = MediaPlayer.create(getApplicationContext(), R.raw.rumble);
        bell = MediaPlayer.create(getApplicationContext(), R.raw.bell);
        bell.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent i = new Intent(getApplicationContext(), HomeScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

            }
        });

        /** App Permissions **/
        // Request camera permission if the user hasn't already granted it.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
        // Request write permission if the user hasn't already granted it.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        /** Enabled Shared Preferences and Relevant Data **/
        editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        clarifaiTotal = prefs.getInt("clarifaiTotal", 0);
        visionTotal = prefs.getInt("visionTotal", 0);
        clarifaiTime = prefs.getLong("clarifaiTime", 0);
        visionTime = prefs.getLong("visionTime", 0);
        clarifaiEndTime = new long[1];
        visionEndTime = new long[1];

        /** Initialize Views **/
        btnVoteLeft = (Button) findViewById(R.id.btn_vote_left);
        btnVoteRight = (Button) findViewById(R.id.btn_vote_right);
        txtTagsLeft = (TextView) findViewById(R.id.txt_tags_left);
        txtTagsRight = (TextView) findViewById(R.id.txt_tags_right);
        txtVisionLoading = (TextView) findViewById(R.id.txt_vision_loading);
        txtClarifaiLoading = (TextView) findViewById(R.id.txt_clarifai_loading);
        imgPictureTaken = (ImageView) findViewById(R.id.img_picture_taken);
        llPrePicture = (LinearLayout) findViewById(R.id.linear_layout_pre_picture);
        llPostPicture = (LinearLayout) findViewById(R.id.linear_layout_post_picture);

        /** FIIIIIIGHT!!! **/
        dispatchTakePictureIntent();
        rumble.start();

    }

    /**
     * Called when the user finished getting a picture from the camera intent.
     * The logic in place here is for the post-picture section.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    try {
                        // Get the bitmap
                        imageBitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        // Set relevant image views
                        imgPictureTaken.setImageBitmap(imageBitmap);
                        // Move to the next stage
                        llPrePicture.setVisibility(View.VISIBLE);
                        llPostPicture.setVisibility(View.INVISIBLE);
                        unleashTheAPIs();
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
                }
            }
        }
    }

    /**
     * Launches the Clarifai and Vision APIs.
     */
    private void unleashTheAPIs() {
        // Randomly decide where the text views are assigned
        Random rand = new Random();
        int num = rand.nextInt(2);

        // Assign Clarifai to left or Right
        TextView clarifaiTextView = num == 0 ? txtTagsLeft : txtTagsRight;
        final Button clarifaiButton = num == 0 ? btnVoteLeft : btnVoteRight;

        // Assign Vision to Left or Right
        TextView visionTextView = num == 0 ? txtTagsRight : txtTagsLeft;
        final Button visionButton = num == 0 ? btnVoteRight : btnVoteLeft;

        clarifaiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add to the clarifai running total
                clarifaiTotal++;
                editor.putInt("clarifaiTotal", clarifaiTotal);
                editor.commit();
                Toast.makeText(getApplicationContext(), "You picked Clarifai!", Toast.LENGTH_SHORT).show();
                endState(clarifaiButton, visionButton);
            }
        });
        visionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add to the vision running total
                visionTotal++;
                editor.putInt("visionTotal", visionTotal);
                editor.commit();
                Toast.makeText(getApplicationContext(), "You picked Google Vision!", Toast.LENGTH_SHORT).show();
                endState(clarifaiButton, visionButton);
            }
        });

        startTime = System.currentTimeMillis();
        txtManager = new TextViewManager(txtClarifaiLoading, txtVisionLoading,
                clarifaiTextView, visionTextView,
                llPrePicture, llPostPicture);

        // Start the Google Cloud Vision API
        GoogleVisionManager googleVisionManager = new GoogleVisionManager(imageBitmap, txtManager, visionEndTime);
        googleVisionManager.runVision();

        // Start the Clarifai API
        ClarifaiManager clarifaiManager = new ClarifaiManager(imageBitmap, txtManager, clarifaiEndTime);
        clarifaiManager.runClarifai();
    }

    /**
     * Handles the app termination.
     * @param clarifaiButton The button clarifai is using.
     * @param visionButton The button vision is using.
     */
    private void endState(Button clarifaiButton, Button visionButton) {
        // Handle media players
        rumble.stop();
        bell.start();
        // Handle button visibility (stop user from messing with things)
        clarifaiButton.setVisibility(View.INVISIBLE);
        visionButton.setVisibility(View.INVISIBLE);
        // Handle shared preference
        long visionRuntime = visionEndTime[0] - startTime;
        visionTime += visionRuntime;
        long clarifaiRuntime = clarifaiEndTime[0] - startTime;
        clarifaiTime += clarifaiRuntime;
        editor.putLong("clarifaiTime", clarifaiTime);
        editor.putLong("visionTime", visionTime);
        editor.commit();
    }

    /**
     * Launches the camera intent so the user can take a picture.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Method called when the user grants/denies permission use.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted.
                } else {
                    // Permission denied.
                }
            }
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted.
                } else {
                    // Permission denied.
                }
            }
        }
    }


}
