package de.dawanda.dawandaclient.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LruCache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.utils.DiskLruCache;
import de.dawanda.dawandaclient.utils.PictureManager;
import de.dawanda.dawandaclient.utils.Utils;


public class PictureService extends Service {

    private class DownloadPictureRunnable implements Runnable {

        private final String mPath;
        private final String mName;


        public DownloadPictureRunnable(String picUrl, String name) {
            mPath = picUrl;
            if (name == null) {
                mName = Utils.getNameFromUrl(picUrl);
            } else {
                mName = name;
            }
        }

        public DownloadPictureRunnable(String picUrl) {
            this(picUrl, Utils.getNameFromUrl(picUrl));
        }

        @Override
        public void run() {
            HttpURLConnection urlConnection = null;
            try {
                DiskLruCache.Snapshot snapshot = mManager.getBitmapFromDisk(mName);
                if (snapshot != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                    sendBitmapBroadcast(bitmap, mName);
                    mManager.addBitmapToCache(mName, bitmap);
                    return;
                }
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http")
                        .appendEncodedPath(mPath);
                urlConnection = (HttpURLConnection) new URL("http:" + mPath).openConnection();
                if (urlConnection.getResponseCode() == 200 || urlConnection.getResponseCode() == 204) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    readStream(in);
                } else {
                    Utils.showNotification(PictureService.this, getString(R.string.error_downloading_image));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        private void readStream(InputStream in) throws IOException {
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            mManager.addBitmapToCache(mName, bitmap);
            sendBitmapBroadcast(bitmap, mName);
        }
    }

    public static final String PICTURE_READY_URI = "de.dawanda.dawandaclient.services.pictureservice.PICTURE_READY_URI";
    public static final String KEY_PIC_POSITION = "KEY_PIC_POSITION";
    public static final String KEY_PIC_URL = "KEY_PIC_URL";
    public static final String KEY_PIC_NAME = "KEY_PIC_NAME";
    public static final String KEY_PIC_PATH = "KEY_PIC_PATH";

    private PictureManager mManager;
    private final BlockingQueue<Runnable> mWorkQueue;
    private final Executor mExecutor;

    public PictureService() {
        mWorkQueue = new PriorityBlockingQueue<>();
        mExecutor = Executors.newFixedThreadPool(1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = PictureManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY;
        }
        String picUrl = intent.getStringExtra(KEY_PIC_URL);
        if (picUrl == null) {
            return Service.START_NOT_STICKY;
        }
        String name = intent.getStringExtra(KEY_PIC_NAME);
        DownloadPictureRunnable d = new DownloadPictureRunnable(picUrl, name);
        mExecutor.execute(d);
        return Service.START_STICKY;
    }

    private void sendBitmapBroadcast(Bitmap bitmap, String name) {
        Intent intent = new Intent(PICTURE_READY_URI);
        intent.putExtra(KEY_PIC_NAME, name);
        intent.putExtra(KEY_PIC_PATH, bitmap);
        LocalBroadcastManager.getInstance(PictureService.this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
