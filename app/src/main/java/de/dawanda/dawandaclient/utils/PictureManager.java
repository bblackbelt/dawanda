package de.dawanda.dawandaclient.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.services.PictureService;

/**
 * Created by emanuele on 30.05.15.
 */
public class PictureManager {

    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(pictureFolder, 1, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    class WriteBitmapToDisk extends AsyncTask<Bitmap, Void, Void> {

        final String mFileName;

        public WriteBitmapToDisk(String name) {
            mFileName = name;
        }

        @Override
        protected Void doInBackground(Bitmap... params) {
            synchronized (mDiskCacheLock) {
                writeBitmapToDisk(mFileName, params[0]);
            }
            return null;
        }

        private void writeBitmapToDisk(String key, Bitmap data) {
            DiskLruCache.Editor editor = null;
            try {
                editor = mDiskLruCache.edit(key);
                if (editor == null) {
                    return;
                }

                if (writeBitmapToFile(data, editor)) {
                    mDiskLruCache.flush();
                    editor.commit();
                } else {
                    editor.abort();
                }
            } catch (IOException e) {
                try {
                    if (editor != null) {
                        editor.abort();
                    }
                } catch (IOException ignored) {
                }
            }
        }

        private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
                throws IOException, FileNotFoundException {
            OutputStream out = null;
            try {
                out = new BufferedOutputStream(editor.newOutputStream(0), 8 * 1024);
                return bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    private static PictureManager sInstance;
    private final Object mDiskCacheLock = new Object();
    private final LruCache<String, Bitmap> mMemoryCache;
    private final File pictureFolder;

    private DiskLruCache mDiskLruCache;

    private Context mContext;

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";


    private PictureManager(Context context) {

        mContext = context;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                final int bitCount;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    bitCount = bitmap.getByteCount();
                } else {
                    bitCount = bitmap.getRowBytes() * bitmap.getHeight();
                }
                return bitCount / 1024;
            }
        };
        pictureFolder = new File(context.getExternalFilesDir(null), "pictures");
        pictureFolder.mkdirs();
        pictureFolder.mkdir();
        new InitDiskCacheTask().execute(pictureFolder);
    }

    public synchronized static PictureManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PictureManager(context);
        }
        return sInstance;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }


    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    public Bitmap getBitmap(String name) {
        String key = Utils.getNameFromUrl(name);
        if (mMemoryCache.get(key) != null) {
            return mMemoryCache.get(key);
        }
        Log.d(getClass().getSimpleName(), " cache miss " + key);
        requestBitmap(name, key);
        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
    }

    public Bitmap getRoundedBitmap(String name, int size) {
        Bitmap bitmap = getBitmap(name);
        return Utils.createRoundedBitmap(bitmap, size, size);
    }

    private void requestBitmap(String name, String key) {
        Intent intent = new Intent(mContext, PictureService.class);
        intent.putExtra(PictureService.KEY_PIC_NAME, "" + key);
        intent.putExtra(PictureService.KEY_PIC_URL, "" + name);
        mContext.startService(intent);
    }

    public boolean isInDiskCache(final String name) throws IOException {
        return mDiskLruCache.get(name) != null;
    }

    public DiskLruCache.Snapshot getBitmapFromDisk(String name) throws IOException {
        if (mDiskLruCache == null) {
            return null;
        }
        return mDiskLruCache.get(name);
    }


    public void addBitmapToCache(String name, Bitmap bitmap) throws IOException {
        if (mMemoryCache.get(name) == null) {
            Log.d(getClass().getSimpleName(), " adding to cache " + name);
            mMemoryCache.put(name, bitmap);
        }
        if (mDiskLruCache != null && mDiskLruCache.get(name) == null) {
            new WriteBitmapToDisk(name).execute(bitmap);
        }
    }
}
