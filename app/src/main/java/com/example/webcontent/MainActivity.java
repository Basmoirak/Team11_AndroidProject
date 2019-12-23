package com.example.webcontent;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Patterns;
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
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements ImageDownloader.ICallback {

    int pos = 0;
    ArrayList<String> imageURLs = new ArrayList<String>();
    ArrayList<Bitmap> clickList = new ArrayList<Bitmap>();
    String[] paths = new String[20];
    EditText urlInput;
    TextView progressBarTextView;
    Button fetchButton;
    String html;
    ProgressBar progressBar;
    Bitmap defaultImageBitmap;

    int counter = 0; //For progress bar

    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.photoalbum);
        mediaPlayer.start();
        mediaPlayer.isLooping();

        urlInput = findViewById(R.id.urlEditText);
        fetchButton = findViewById(R.id.fetchButton);
        progressBar = findViewById(R.id.progressBar);
        progressBarTextView = findViewById(R.id.progressBarTextView);
        defaultImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pokeball);
        resetImageButtons();
        resetProgressBar(20);
    }



    @Override
    public void onCompleted(Bitmap bitmap) {
        // Loading images
        ImageButton imageButton = findViewById(getResources().getIdentifier(
                "imageButton" + pos,
                "id", getPackageName()
        ));

        imageButton.setTag(bitmap);
        imageButton.setImageBitmap(bitmap);
        imageButton.setAdjustViewBounds(true);
        pos++;

        // Image loading progress bar
        counter++;
        progressBar.setProgress(counter);

        // Update progress bar status in text view
        if(counter != 20){
            progressBarTextView.setText("Downloading " + counter + " of 20 images...");
        }else{
            progressBarTextView.setText("Download Completed");
        }
    }

    //Fetch button
    public void onFetch(View view){

        resetImageButtons();
        //Check if url pattern is valid
        if(Patterns.WEB_URL.matcher(urlInput.getText().toString()).matches()){
            try {
                //Clear list of imageURLs
                imageURLs.clear();
                //Reset imageButton position to 0
                pos = 0;
                // Retrieve HTML
                DownloadTask downloadTask = new DownloadTask();
                html = downloadTask.execute(urlInput.getText().toString()).get();
                if(html != null){

                    // Add all image urls to paths
                    paths = createImageURLs();

                    //Reset progress bar
                    resetProgressBar(imageURLs.size());

                    new ImageDownloader(this).execute(paths);

                } else {
                    Toast.makeText(getApplicationContext(),"Please enter valid Url!", Toast.LENGTH_SHORT).show();
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            // If url pattern is invalid
        } else {
            Toast.makeText(getApplicationContext(),"Please enter valid Url!", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetProgressBar(int size){
        counter = 0;
        progressBar.setProgress(0);
        progressBarTextView.setText("");
        if(size < 20){
            progressBar.setMax(size);
        } else {
            progressBar.setMax(20);
        }
    }

    public void resetImageButtons(){
        for (int i = 0; i < 20; i++){
            ImageButton imageButton = findViewById(getResources().getIdentifier(
                    "imageButton" + i,
                    "id", getPackageName()
            ));
            imageButton.setTag(defaultImageBitmap);
            imageButton.setImageBitmap(defaultImageBitmap);
            imageButton.setBackgroundColor(0x00000000);
            imageButton.setAdjustViewBounds(true);
        }

        //Clear entire clicked buttons history
        clickList.clear();
    }

    public String[] createImageURLs(){
        // Match HTML against Regex pattern
        Pattern p = Pattern.compile("img.*?src=\"(.*?)\"");
        Matcher m = p.matcher(html);
        String[] urlArray = new String[20];

        // Add all image urls from HTML to array
        while (m.find()) {
            if(Patterns.WEB_URL.matcher(m.group(1)).matches()){
                imageURLs.add(m.group(1));
            }
        }

        if(imageURLs.size() < 20){
            for(int i = 0; i < imageURLs.size(); i++){
                urlArray[i] = imageURLs.get(i);
            }
            for(int i = imageURLs.size(); i < 20; i++){
                urlArray[i] = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/No_image_3x4.svg/1280px-No_image_3x4.svg.png";
            }
        } else {
            for(int i = 0; i < 20; i++){
                urlArray[i] = imageURLs.get(i);
            }
        }

        return urlArray;
    }

    public void buttonClick(View view) {

        //Don't allow users to click on default image
        if(view.getTag() == defaultImageBitmap){
            return;
        }

        final ImageButton button = (ImageButton) view;
        boolean isClicked = (clickList.stream().anyMatch(new Predicate<Bitmap>() {
                                            @Override
                                            public boolean test(Bitmap bmp) {
                                                return bmp == button.getTag();
                                            }}));
        if(!isClicked){
            button.setBackgroundColor(Color.BLUE);
            clickList.add((Bitmap) button.getTag());

            try {
                Bitmap bmp = ((BitmapDrawable)button.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();
                FileOutputStream fo = openFileOutput("image" + (clickList.size() - 1), MODE_PRIVATE);

                fo.write(byteArray);

                fo.flush();
                fo.close();
                stream.close();

                if(clickList.size() == 6){
                    Intent intent = new Intent(this, Game.class);
                    startActivity(intent);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            button.setBackgroundColor(0x00000000);
            clickList.remove((Bitmap) button.getTag());
        }

    }

}
