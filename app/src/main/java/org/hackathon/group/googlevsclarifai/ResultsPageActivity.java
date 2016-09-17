package org.hackathon.group.googlevsclarifai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.GraphicalView;
import org.w3c.dom.Text;

/**
 * Created by jellio on 9/17/16.
 */
public class ResultsPageActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Button menuButton;
    private Button newRoundButton;
    private TextView clarifaiText, visionText, txtClarifaiTime, txtVisionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_page);

        // Handle Shared Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int clarifaiTotal = prefs.getInt("clarifaiTotal", 0);
        int visionTotal = prefs.getInt("visionTotal", 0);
        long clarifaiTime = prefs.getLong("clarifaiTime", 0);
        long visionTime = prefs.getLong("visionTime", 0);

        // Initialize Views
        txtClarifaiTime = (TextView) findViewById(R.id.txt_speed_left);
        txtVisionTime = (TextView) findViewById(R.id.txt_speed_right);
        txtClarifaiTime.setText("" + (clarifaiTime / (clarifaiTotal + visionTotal)) + " ms");
        txtVisionTime.setText("" + (visionTime / (clarifaiTotal + visionTotal)) + " ms");
        clarifaiText = (TextView) findViewById(R.id.clarifai_txt);
        clarifaiText.setText(""+clarifaiTotal+" total selections");
        visionText = (TextView) findViewById(R.id.vision_txt);
        visionText.setText(""+visionTotal+" total selections");
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);
        GraphicalView chartView = PieChartView.getNewInstance(getApplicationContext(), visionTotal, clarifaiTotal);

        chartContainer.addView(chartView);

        menuButton = (Button) findViewById(R.id.btn_results_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newRoundButton = (Button) findViewById(R.id.btn_results_new_round);
        newRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

}
