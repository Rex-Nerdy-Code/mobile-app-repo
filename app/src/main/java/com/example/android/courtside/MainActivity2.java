package com.example.android.courtside;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MainActivity2 extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "HEY YO", EXTRA_MESSAGE2 = "HEY HEY";

    EditText teamANameCollector, teamBNameCollector;

    String TeamAName = "TEAM A", TeamBName = "TEAM B";
   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_name);


        teamANameCollector = findViewById(R.id.team_a_name);
        teamBNameCollector = findViewById(R.id.team_b_name);



    }

    public void goToCountPage(View view) {

        TeamAName = teamANameCollector.getText().toString();
        TeamBName = teamBNameCollector.getText().toString();
        //send via intent to new activity
       Intent intent = new Intent(getApplicationContext(), MainActivity.class);
       intent.putExtra(EXTRA_MESSAGE, TeamAName);
       intent.putExtra(EXTRA_MESSAGE2, TeamBName);
       MainActivity2.this.startActivity(intent);
    }
}