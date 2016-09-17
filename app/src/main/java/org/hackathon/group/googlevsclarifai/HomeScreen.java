package org.hackathon.group.googlevsclarifai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by jellio on 9/17/16.
 */
public class HomeScreen extends AppCompatActivity {

    private Button btnNewRound, btnFightHistory, btnAbout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        context = this;
        btnNewRound = (Button) findViewById(R.id.btn_new_round);
        btnFightHistory = (Button) findViewById(R.id.btn_fight_history);
        btnAbout = (Button) findViewById(R.id.btn_about);


        btnNewRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });

        btnFightHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ResultsPageActivity.class));
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AboutActivity.class));
            }
        });
    }

}
