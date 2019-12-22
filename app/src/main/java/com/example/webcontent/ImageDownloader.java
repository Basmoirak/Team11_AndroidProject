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
        InputStream inputStream = null;

        try {
            for (int i = 0; i < urls.length; i ++ ){

                URL url = new URL(urls[i]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if(connection == null){
                    return null;
                }

//                connection.setDoInput(true);
                connection.connect();

                inputStream = connection.getInputStream();
                Bitmap myBitMap = BitmapFactory.decodeStream(inputStream);
                publishProgress(myBitMap);
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
//        Log.i("Bitmaps",bitmaps[0].toString());
        this.callback.onCompleted(bitmaps[0]);
    }

    public interface ICallback {
        void onCompleted(Bitmap bitmap);
    }
}
