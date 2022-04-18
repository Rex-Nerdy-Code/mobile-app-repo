package com.example.android.courtside;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

/**
 * This activity keeps track of the basketball score for 2 teams.
 */
public class MainActivity extends AppCompatActivity {

    // Tracks the score for Team A
    int scoreTeamA = 0, foulTeamA = 0, timeOutTeamA = 0;

    // Tracks the score for Team B
    int scoreTeamB = 0, foulTeamB = 0, timeOutTeamB = 0;

    MenuItem darkModeButton;

    String teamAname = "TEAM A", teamBname = "TEAM B ";
    TextView teamANameFinal, teamBNameFinal, shot_clock_timer, game_time, quarter_time, teamAFouls,
            teamBFouls , teamATimeout, teamBtimeout;
    Button startStopButton;
    private CountDownTimer countDownTimer;
    private CountDownTimer shotClockCountDownTimer;
    private long gameTimeLeftInMilliseconds = 720000;  //12mins
    private long shotClockTimeleftInmilliseconds = 24000; //24sec
    private boolean isGameTimerRunning;
    private boolean isShotClockTimerRunning;
    MediaPlayer mediaPlayer;
    int quarter_time_value;

    Dialog dialog;
    TextView t_dialog_msg, teamA_dialogName, teamA_dialogScore, teamB_dialogName, teamB_dialogScore;
    LinearLayout dialog_background;
    private AdView mAdView;

    // Saving state of our app
    // using SharedPreferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isDarkModeOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quarter_time = findViewById(R.id.quarter_text);
        shot_clock_timer = findViewById(R.id.shotclocktimer);
        startStopButton = findViewById(R.id.start_button);
        mediaPlayer = MediaPlayer.create(this, R.raw.buzzer);
        darkModeButton = findViewById(R.id.dark_mode);

        sharedPreferences
            = getSharedPreferences(
                "sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor
                = sharedPreferences.edit();

        isDarkModeOn
                = sharedPreferences
                .getBoolean(
                        "isDarkModeOn", false);



        MobileAds.initialize(this, initializationStatus -> {
        });

        Intent intent = getIntent();
        teamAname = intent.getStringExtra(MainActivity2.EXTRA_MESSAGE);
        teamBname = intent.getStringExtra(MainActivity2.EXTRA_MESSAGE2);

        teamANameFinal = findViewById(R.id.teamANameFinal);
        teamBNameFinal = findViewById(R.id.teamBName_final);
        game_time = findViewById(R.id.game_time);

        if(!teamAname.isEmpty()){
            teamANameFinal.setText(teamAname);
        }
        if (!teamBname.isEmpty()){
            teamBNameFinal.setText(teamBname);
        }
        quarter_time_value = 1;
        initViews();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        teamAFouls = findViewById(R.id.team_a_fouls);
        teamBFouls = findViewById(R.id.team_b_fouls);
        teamATimeout = findViewById(R.id.team_a_timeout);
        teamBtimeout = findViewById(R.id.team_b_timeout);


//        darkModeButton.setOnMenuItemClickListener(
//                item -> {
//                    if (isDarkModeOn) {
//
//                        // if dark mode is on it
//                        // will turn it off
//                        AppCompatDelegate
//                                .setDefaultNightMode(
//                                        AppCompatDelegate
//                                                .MODE_NIGHT_NO);
//                        // it will set isDarkModeOn
//                        // boolean to false
//                        editor.putBoolean(
//                                "isDarkModeOn", false);
//                        editor.apply();
//
//                        // change text of Button
//                        darkModeButton.setTitle(
//                                "Dark Mode");
//                    }
//                    else {
//
//                        // if dark mode is off
//                        // it will turn it on
//                        AppCompatDelegate
//                                .setDefaultNightMode(
//                                        AppCompatDelegate
//                                                .MODE_NIGHT_YES);
//
//                        // it will set isDarkModeOn
//                        // boolean to true
//                        editor.putBoolean(
//                                "isDarkModeOn", true);
//                        editor.apply();
//
//                        // change text of Button
//                        darkModeButton.setTitle(
//                                "Light Mode");
//                    }
//                    return false;
//                }
//        );

    }

    void initViews(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_popup);
        t_dialog_msg = dialog.findViewById(R.id.dialog_message);
        teamA_dialogName = dialog.findViewById(R.id.teamANameFinal);
        teamA_dialogScore = dialog.findViewById(R.id.team_a_score);
        teamB_dialogName = dialog.findViewById(R.id.teamBName_final);
        teamB_dialogScore = dialog.findViewById(R.id.team_b_score);
        dialog_background = dialog.findViewById(R.id.dialog_background);
    }

    //initialize/link menu XML to App
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    //Handle menu clicks
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
         switch (id){
             case R.id.action_end_game:
                 //code to end game and return to start activity
                 endGame();
                 return true;
             case R.id.action_settings:
                 //goto App settings page
                 return true;
             case R.id.action_exit:
                 //code to close App
                 exitApp();
                 return true;
             case R.id.dark_mode:
                 //darkMode
                 // When user reopens the app
                 // after applying dark/light mode
//                 if (isDarkModeOn) {
//                     AppCompatDelegate
//                             .setDefaultNightMode(
//                                     AppCompatDelegate
//                                             .MODE_NIGHT_YES);
//                     darkModeButton.setTitle(
//                             "Light Mode");
//                 }
//                 else {
//                     AppCompatDelegate
//                             .setDefaultNightMode(
//                                     AppCompatDelegate
//                                             .MODE_NIGHT_NO);
//                     darkModeButton
//                             .setTitle(
//                                     "Dark Mode");
//                 }
                 turnOnDarkMode();
                 return true;
             case R.id.action_about:
                 //goto about page
                 goToABoutPage();
                 return true;
             default:
                 return super.onOptionsItemSelected(item);
         }

    }

    private void turnOnDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // When user taps the enable/disable
        // dark mode button
        if (isDarkModeOn) {

            // if dark mode is on it
            // will turn it off
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
            // it will set isDarkModeOn
            // boolean to false
            editor.putBoolean(
                    "isDarkModeOn", false);
            editor.apply();

            // change text of Button
            darkModeButton.setTitle(
                    "Dark Mode");
        }
        else {

            // if dark mode is off
            // it will turn it on
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);

            // it will set isDarkModeOn
            // boolean to true
            editor.putBoolean(
                    "isDarkModeOn", true);
            editor.apply();

            // change text of Button
            darkModeButton.setTitle(
                    "Light Mode");
        }
    }

    /**
     * Increase the score for Team A by 1 point.
     */
    public void addOneForTeamA(View v) {
        scoreTeamA = scoreTeamA + 1;
        displayForTeamA(scoreTeamA);
        if (isShotClockTimerRunning){
            stopTimer();
            shotClockTimeleftInmilliseconds = 24000;
        }
    }

    /**
     * Increase the score for Team A by 2 points.
     */
    public void addTwoForTeamA(View v) {
        scoreTeamA = scoreTeamA + 2;
        displayForTeamA(scoreTeamA);
        if (isShotClockTimerRunning){
            stopTimer();
            shotClockTimeleftInmilliseconds = 24000;
        }
    }

    /**
     * Increase the score for Team A by 3 points.
     */
    public void addThreeForTeamA(View v) {
        scoreTeamA = scoreTeamA + 3;
        displayForTeamA(scoreTeamA);
        if (isShotClockTimerRunning){
            stopTimer();
            shotClockTimeleftInmilliseconds = 24000;
        }
    }

    /**
     * Increase the score for Team B by 1 point.
     */
    public void addOneForTeamB(View v) {
        scoreTeamB = scoreTeamB + 1;
        displayForTeamB(scoreTeamB);
        if (isShotClockTimerRunning){
            stopTimer();
            shotClockTimeleftInmilliseconds = 24000;
        }
    }

    /**
     * Increase the score for Team B by 2 points.
     */
    public void addTwoForTeamB(View v) {
        scoreTeamB = scoreTeamB + 2;
        displayForTeamB(scoreTeamB);
        if (isShotClockTimerRunning){
            stopTimer();
            shotClockTimeleftInmilliseconds = 24000;
        }
    }

    /**
     * Increase the score for Team B by 3 points.
     */
    public void addThreeForTeamB(View v) {
        scoreTeamB = scoreTeamB + 3;
        displayForTeamB(scoreTeamB);
        if (isShotClockTimerRunning){
            stopTimer();
            shotClockTimeleftInmilliseconds = 24000;
        }
    }

    /**
     * Displays the given score for Team A.
     */
    public void displayForTeamA(int score) {
        TextView scoreView =  findViewById(R.id.team_a_score);
        scoreView.setText(String.valueOf(score));
    }

    /**
     * Displays the given score for Team B.
     */
    public void displayForTeamB(int score) {
        TextView scoreView = findViewById(R.id.team_b_score);
        scoreView.setText(String.valueOf(score));
    }

    /**
     * Start game, resetscore starts the game, am just too lazy to refactor.
     */
    public void resetScore(View v) {
        startStop();
    }

    private void startStop() {
        if (isGameTimerRunning & isShotClockTimerRunning){
            stopTimer();
        }else {
            startTimer();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(gameTimeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameTimeLeftInMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onFinish() {
                stopTimer();
                gameTimeLeftInMilliseconds = 720000;  //12 min
                shotClockTimeleftInmilliseconds = 24000; //24sec
                quarter_time_value += 1;
                if (quarter_time_value == 2){
                    quarter_time.setText(quarter_time_value + "ND");
                    t_dialog_msg.setText("End of 1st Quarter");
                    if (!teamAname.isEmpty()) teamA_dialogName.setText(teamAname);
                    if (!teamBname.isEmpty()) teamB_dialogName.setText(teamBname);
                    teamA_dialogScore.setText(String.valueOf(scoreTeamA));
                    teamB_dialogScore.setText(String.valueOf(scoreTeamB));
                    dialog.create();
                    dialog.show();
                }else if (quarter_time_value == 3){
                    quarter_time.setText(quarter_time_value + "RD");
                    t_dialog_msg.setText("End of 2nd Quarter");
                    if (!teamAname.isEmpty()) teamA_dialogName.setText(teamAname);
                    if (!teamBname.isEmpty()) teamB_dialogName.setText(teamBname);
                    teamA_dialogScore.setText(String.valueOf(scoreTeamA));
                    teamB_dialogScore.setText(String.valueOf(scoreTeamB));
                    dialog.create();
                    dialog.show();
                }else if (quarter_time_value == 4){
                    quarter_time.setText(quarter_time_value + "TH");
                    t_dialog_msg.setText("End of 3rd Quarter");
                    if (!teamAname.isEmpty()) teamA_dialogName.setText(teamAname);
                    if (!teamBname.isEmpty()) teamB_dialogName.setText(teamBname);
                    teamA_dialogScore.setText(String.valueOf(scoreTeamA));
                    teamB_dialogScore.setText(String.valueOf(scoreTeamB));
                    dialog.create();
                    dialog.show();
                }
            }
        }.start();

        shotClockCountDownTimer = new CountDownTimer(shotClockTimeleftInmilliseconds, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
               shotClockTimeleftInmilliseconds = millisUntilFinished;
               updateShotClockTimer();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onFinish() {
                stopTimer();
                shotClockTimeleftInmilliseconds = 24000;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500); // Vibrate for 500 milliseconds
                v.vibrate(500);
                mediaPlayer.start(); // play buzzer sound

            }
        }.start();

        startStopButton.setText(R.string.pause);
        isGameTimerRunning = true;
        isShotClockTimerRunning = true;
    }

    private void stopTimer() {
        countDownTimer.cancel();
        shotClockCountDownTimer.cancel();
        startStopButton.setText("RESUME");
        isGameTimerRunning = false;
        isShotClockTimerRunning = false;
    }

    private void updateTimer() {
        int minutes = (int) gameTimeLeftInMilliseconds / 60000;
        int seconds = (int) gameTimeLeftInMilliseconds % 60000 / 1000;
        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 0) timeLeftText += "0";
        timeLeftText += seconds;

        game_time.setText(timeLeftText);
    }

    private void updateShotClockTimer() {
        int seconds = (int) shotClockTimeleftInmilliseconds / 1000;
        String timeLeftText;
        timeLeftText = "" + seconds;
        shot_clock_timer.setText(timeLeftText);
    }

    public void resetShotClock(View view) {
        shotClockTimeleftInmilliseconds = 24000;
        shot_clock_timer.setText("24");

    }

    public void startNextQuarter(View view) {
        dialog.cancel();
    }

    public void resetFouls(View view) {
     foulTeamA += 1;
     teamAFouls.setText(String.valueOf(foulTeamA));
    }

    public void resetFoulsB(View view) {
        foulTeamB += 1;
        teamBFouls.setText(String.valueOf(foulTeamB));
    }

    public void resetTimeOutB(View view) {
        timeOutTeamB += 1;
        teamBtimeout.setText(String.valueOf(timeOutTeamB));
    }

    public void resetTimeutA(View view) {
        timeOutTeamA += 1;
        teamATimeout.setText(String.valueOf(timeOutTeamA));
    }

    /**
     * end game and return to mainActivity2
     */
    public void endGame() {
        scoreTeamA = 0;
        scoreTeamB = 0;
        displayForTeamA(scoreTeamA);
        displayForTeamB(scoreTeamB);

        finish();
    }

    public void exitApp() {
        finishAffinity();
        System.exit(0);
    }

    public void goToABoutPage() {
        Intent intent = new Intent(getApplicationContext(), AboutPageActivity.class);
        MainActivity.this.startActivity(intent);
    }

}
