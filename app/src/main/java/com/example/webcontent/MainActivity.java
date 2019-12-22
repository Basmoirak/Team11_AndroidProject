package com.example.webcontent;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements ImageDownloader.ICallback {

    int pos = 0;
    ArrayList<String> imageURLs = new ArrayList<String>();
    String[] paths = new String[20];
    EditText urlInput;
    TextView progressBarTextView;
    Button fetchButton;
    String html;
    ProgressBar progressBar;
    MenuItem countNumber;

    int counter = 0; //For progress bar

    boolean clicked = false;
    int clickedImages = 0;

    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.urlEditText);
        fetchButton = findViewById(R.id.fetchButton);
        progressBar = findViewById(R.id.progressBar);
        progressBarTextView = findViewById(R.id.progressBarTextView);
        resetProgressBar();
    }

    //create action bar icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu, menu);
        countNumber = menu.findItem(R.id.count);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_game) {
           Intent intent = new Intent(this, Game.class);
           startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCompleted(Bitmap bitmap) {
        // Loading images
        ImageButton imageButton = findViewById(getResources().getIdentifier(
                "imageButton" + pos,
                "id", getPackageName()
        ));
//        Log.i("In OnCompleted, ImageButton is: ", imageButton.toString());
        imageButton.setTag(bitmap);
        imageButton.setImageBitmap(bitmap);
        imageButton.setAdjustViewBounds(true);
        pos++;

        // Image loading progress bar
        counter++;
        progressBar.setProgress(counter);

        // Update progress bar status in text view
        if(counter == 20){
            progressBarTextView.setText("Download complete!");
        } else {
            progressBarTextView.setText("Downloading " + counter + " of 20 images...");
        }
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
                    Log.i("urls", paths[i]);
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
        progressBarTextView.setText("");
    }

    public void buttonClick(View view) {
        if(clickedImages >= 6){
            return;
        }
        if(clicked == false){

            clicked = true;
            ImageButton button = (ImageButton) view;
            button.setBackgroundColor(Color.BLUE);

            try {
                Bitmap bmp = ((BitmapDrawable)button.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();
                FileOutputStream fo = openFileOutput("image"+clickedImages, MODE_PRIVATE);

                fo.write(byteArray);

                clickedImages++;
                fo.flush();
                fo.close();

                countNumber.setTitle(clickedImages);

            } catch (Exception e){
                e.printStackTrace();
            }


        }
        else {
            clicked = false;
            view.setBackgroundColor(0x00000000);
            //clickedImages--;
        }
    }

    public void updatedClickedImages(){

    }
}
