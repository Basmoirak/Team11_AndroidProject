package com.example.webcontent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Finish extends AppCompatActivity {
    private TextView status;
    private TextView secondElapsed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Intent intent = getIntent();
        status = findViewById(R.id.status);
        secondElapsed = findViewById(R.id.secondElapsed);
        status.setText(intent.getStringExtra("status"));
        secondElapsed.setText(intent.getStringExtra("secondElapsed"));
    }
}
