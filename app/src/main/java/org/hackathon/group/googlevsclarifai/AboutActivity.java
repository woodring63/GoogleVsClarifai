package org.hackathon.group.googlevsclarifai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by jellio on 9/17/16.
 */
public class AboutActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textView = (TextView) findViewById(R.id.txt_about_heading);
        textView.setText("ABOUT MLH SMACKDOWN- RAW:\n" + "\n"+
                "MLH Smackdown Raw is a mobile app we created to test the Image Recognition API of Google Cloud Vision and Clarifai. \n" +
                "\n" +
                "For every \"fight\", you take a picture from your phone's camera, which is then simultaneously uploaded to both Google and Clarifai's services. They return a list of tags that they have determined to describe the image most accurately. \n" +
                "To make it difficult to identify which results are from which service, each list is limited to 5 results, and the locations are randomly switched. It is then up to you to determine which list of tags best relates to each image.\n" +
                "After you vote, the round is over and a winner is declared!\n" +
                "\n" +
                "\n" +
                "Team Members:\n" +
                "Evan Woodring\n" +
                "Joseph Elliot\n" +
                "Chris Judge\n"+ "\n"+"\n"+"Important Links:\n" + "Clarifai: https://www.clarifai.com/\n" + "Google Cloud Vision: https://cloud.google.com/vision/\n" + "Major Leauge Hacking: https://mlh.io/\n");
    }

}
