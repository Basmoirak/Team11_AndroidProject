package com.example.webcontent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class Game extends AppCompatActivity implements View.OnClickListener {
    private MemoryButton[] buttons;
    private int numberOfButtons;

    private int[] buttonGraphicLocations;
    private Bitmap[] buttonGraphics;

    private MemoryButton selectedButton1;
    private MemoryButton selectedButton2;
    private MemoryButton button;
    private boolean isBusy;

    ArrayList<byte[]> arrayOfByteArray;

    private TextView matchesCountView;
    private int matchesCounter = 0;

    private TextView timerView;

    private long secondElapsed;

    private Runnable runnable = null;
    private Handler handler;
    private boolean mStarted;
    private int totalClicks = 0;

    long startTime = SystemClock.uptimeMillis();

    @Override
    protected void onStart() {
        super.onStart();
        mStarted = true;
        handler.postDelayed(runnable, 1000L);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStarted = false;
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        handler = new Handler();

        arrayOfByteArray = (ArrayList<byte[]>) getIntent().getSerializableExtra("list");

        matchesCountView = findViewById(R.id.matchesCountView);
        timerView = findViewById(R.id.timerView);

            runnable = new Runnable() {
                @Override
                public void run() {
                    if (mStarted) {
                        long milliSecondTime = 0L;
                        long timeBuff = 0L;
                        long updatedTime = 0L;

                        milliSecondTime = SystemClock.uptimeMillis() - startTime;

                        updatedTime = (timeBuff + milliSecondTime) / 1000;

                        timerView.setText(String.format("%02d:%02d", updatedTime / 60, updatedTime % 60));

                        handler.postDelayed(runnable, 1000L);
                    }
                }


            };

        GridLayout grid = findViewById(R.id.gridLayout);
        int numberOfC = grid.getColumnCount();
        int numberOfR = grid.getRowCount();
        numberOfButtons = numberOfC * numberOfR;

        buttons = new MemoryButton[numberOfButtons];
        buttonGraphics = new Bitmap[numberOfButtons / 2];
        Bitmap bmp = null;

            try {
                for(int i = 0; i <= 5; i++) {
                    buttonGraphics[i] = BitmapFactory.decodeStream(getApplicationContext().openFileInput("image" + i));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        buttonGraphicLocations = new int[numberOfButtons];

        shuffle();

        //create buttons
        for(int r = 0; r < numberOfR; r++){
            for(int c = 0; c < numberOfC; c++){
                MemoryButton tempButton = new MemoryButton(this, r, c, buttonGraphics[buttonGraphicLocations[r * numberOfC + c]]);
                tempButton.setId(View.generateViewId());
                Log.i("msg", String.valueOf(tempButton.getId()));
                tempButton.setOnClickListener(this);

                grid.addView(tempButton);

                //saving the reference to button
                buttons[r*numberOfC + c] = tempButton;
            }
        }
    }

    private void shuffle(){
        Random rand = new Random();
        for(int i = 0; i < numberOfButtons; i++){
            buttonGraphicLocations[i] = i % (numberOfButtons / 2);
        }
        for(int i = 0; i < numberOfButtons; i++){
            int temp = buttonGraphicLocations[i];
            int swap = rand.nextInt(12);
            buttonGraphicLocations[i] = buttonGraphicLocations[swap];
            buttonGraphicLocations[swap] = temp;
        }
    }

    @Override
    public void onClick(View view) {
        button = (MemoryButton) view;
        if(isBusy){
            return;
        }
        if(selectedButton1 == null){
            selectedButton1 = button;
            selectedButton1.flip();
        }
        if(selectedButton1.getId() == button.getId()){
            return;
        }
        if(selectedButton1.getFrontImageId() == button.getFrontImageId()){
            button.flip();

            button.setMatched(true);

            selectedButton1.setEnabled(false);
            button.setEnabled(false);

            selectedButton1 = null;

            matchesCounter++;
            totalClicks++;
            //updating match progress
            if(matchesCounter == 6){
                matchesCountView.setText("PERFECT!");
//                Intent intent = new Intent(this, Finish.class);
//                intent.putExtra("status", "You have won the game.");
//                secondElapsed = 30 - secondElapsed;
//                intent.putExtra("secondElapsed",  "You finished within "+secondElapsed+" seconds.");
//                startActivity(intent);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
            }
            else{
                matchesCountView.setText(matchesCounter+ " / " + totalClicks);
            }
            return;
        }
        else {
            selectedButton2 = button;
            selectedButton2.flip();
            isBusy = true;
            totalClicks++;
            matchesCountView.setText(matchesCounter+ " / " + totalClicks);

            //flipping back to normal pic after delaying 5 sec
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedButton1.flip();
                    selectedButton2.flip();
                    selectedButton1 = null;
                    selectedButton2 = null;
                    isBusy = false;
                }
            }, 500);
        }
    }
}
