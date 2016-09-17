package org.hackathon.group.googlevsclarifai;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jellio on 9/17/16.
 */
public class TextViewManager {

    private final String done = "DONE!";

    private TextView txtClarifaiLoading, txtVisionLoading;
    private TextView txtClarifaiResults, txtVisionResults;
    private LinearLayout llPrePicture, llPostPicture;

    public TextViewManager(TextView txtClarifaiLoading, TextView txtVisionLoading,
                           TextView txtClarifaiResults, TextView txtVisionResults,
                           LinearLayout llPrePicture, LinearLayout llPostPicture) {
        this.txtClarifaiLoading = txtClarifaiLoading;
        this.txtClarifaiResults = txtClarifaiResults;
        this.txtVisionLoading = txtVisionLoading;
        this.txtVisionResults = txtVisionResults;
        this.llPrePicture = llPrePicture;
        this.llPostPicture = llPostPicture;
    }

    public void updateToDone(String tag, String results) {
        if (tag.equals("clarifai")) {
            txtClarifaiLoading.setText(done);
            txtClarifaiResults.setText(results);
        } else if (tag.equals("vision")) {
            txtVisionLoading.setText(done);
            txtVisionResults.setText(results);
        }
        if (txtClarifaiLoading.getText().toString().equals(done) &&
                txtVisionLoading.getText().toString().equals(done)) {
            llPrePicture.setVisibility(View.INVISIBLE);
            llPostPicture.setVisibility(View.VISIBLE);
        }
    }
}
