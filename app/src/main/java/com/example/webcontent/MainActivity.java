package com.example.webcontent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements ImageDownloader.ICallback {

    int pos = 0;
    ArrayList<String> imageURLs = new ArrayList<String>();
    String[] paths = new String[20];
    EditText urlInput;
    Button fetchButton;
    String html;
    ProgressBar progressBar;
    int counter = 0; //For progress bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.urlEditText);
        fetchButton = findViewById(R.id.fetchButton);
        progressBar = findViewById(R.id.progressBar);
        resetProgressBar();
    }

    @Override
    public void onCompleted(Bitmap bitmap) {
        // Loading images
        ImageButton imageButton = findViewById(getResources().getIdentifier(
                "imageButton" + pos,
                "id", getPackageName()
        ));
//        Log.i("In OnCompleted, ImageButton is: ", imageButton.toString());

        imageButton.setImageBitmap(bitmap);
        imageButton.setAdjustViewBounds(true);
        pos++;

        // Image loading progress bar
        counter++;
        progressBar.setProgress(counter);
    }

    //Fetch button
    public void onFetch(View view){

        //Check if url pattern is valid
        if(Patterns.WEB_URL.matcher(urlInput.getText().toString()).matches()){
            try {
                //Clear list of imageURLs
                imageURLs.clear();
                //Reset imageButton position to 0
                pos = 0;
                // Reset progress bar
                resetProgressBar();
                // Retrieve HTML
                DownloadTask downloadTask = new DownloadTask();
                html = null;
                html = downloadTask.execute(urlInput.getText().toString()).get();
                Log.i("url", urlInput.getText().toString()); // TESTING
                // Match HTML against Regex pattern
                Pattern p = Pattern.compile("img src=\"(.*?)\"");
                Matcher m = p.matcher(html);

                // Add all image urls from HTML to array
                while (m.find()) {
                    imageURLs.add(m.group(1));
                }

                //Limit to 20 image urls in array
                for(int i = 0; i < 20; i++){
                    paths[i] = imageURLs.get(i + 1);
                }

                new ImageDownloader(this).execute(paths);

            } catch(Exception e) {
                e.printStackTrace();
            }
            // If url pattern is invalid
        } else {
            Toast.makeText(getApplicationContext(),"Please enter valid Url!", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetProgressBar(){
        counter = 0;
        progressBar.setProgress(0);
        progressBar.setMax(20);
    }
}
