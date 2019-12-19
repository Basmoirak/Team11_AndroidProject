package com.example.webcontent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements ImageDownloader.ICallback {

    int pos = 0;
    ArrayList<String> imageURLs = new ArrayList<String>();
    String[] paths = new String[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String html = null;

        try {
            // Retrieve HTML
            html = task.execute("https://stocksnap.io").get();

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

            for(int i = 0; i < 20; i++){
                System.out.println(paths[i]);
            }

            new ImageDownloader(this).execute(paths);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCompleted(Bitmap bitmap) {
        ImageButton imageButton = findViewById(getResources().getIdentifier(
                "imageButton" + pos,
                "id", getPackageName()
        ));

        imageButton.setImageBitmap(bitmap);
        imageButton.setAdjustViewBounds(true);

        pos++;
    }
}
