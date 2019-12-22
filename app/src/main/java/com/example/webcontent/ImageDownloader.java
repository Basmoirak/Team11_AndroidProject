package com.example.webcontent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Patterns;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Bitmap, Void> {
    ICallback callback;

    public ImageDownloader (ICallback callback){
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... urls) {
        InputStream inputStream = null;

        try {

            for (int i = 0; i < urls.length; i ++ ){

                if(Patterns.WEB_URL.matcher(urls[i]).matches()){
                    URL url = new URL(urls[i]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    if(connection == null){
                        return null;
                    }

                    connection.setDoInput(true);
                    connection.connect();

                    inputStream = connection.getInputStream();
                    Bitmap myBitMap = BitmapFactory.decodeStream(inputStream);
                    publishProgress(myBitMap);
                } else {
                    return null;
                }
            }

        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            FileUtils.close(inputStream);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... bitmaps) {
        if(this.callback == null){
            return;
        }
        this.callback.onCompleted(bitmaps[0]);
    }

    public interface ICallback {
        void onCompleted(Bitmap bitmap);
    }
}
