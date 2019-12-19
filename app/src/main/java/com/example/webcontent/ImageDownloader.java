package com.example.webcontent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

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
        try {
            for (int i = 0, ct = urls.length; i < ct; i ++ ){

                URL url = new URL(urls[i]);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                if(connection == null){
                    return null;
                }

                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap myBitMap = BitmapFactory.decodeStream(inputStream);
                publishProgress(myBitMap);
            }

        } catch (Exception e){
            e.printStackTrace();
            return null;
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
