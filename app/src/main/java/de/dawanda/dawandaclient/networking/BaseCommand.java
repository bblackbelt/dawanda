package de.dawanda.dawandaclient.networking;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.dawanda.dawandaclient.utils.Constants;


public class BaseCommand implements Runnable {

    private String mPath;
    private String mStringEncoding;
    private CommandListener<String> mListener;

    public BaseCommand(String path, CommandListener<String> listener) {
        this(path, "UTF-8", listener);
    }

    public BaseCommand(String path, String stringEncoding, CommandListener<String> listener) {
        if (path == null) {
            throw new IllegalArgumentException("path can't be null");
        }
        mPath = path;
        mStringEncoding = stringEncoding;
        mListener = listener;
    }


    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        try {
            Uri.Builder builder = new Uri.Builder()
                    .scheme("http")
                    .authority(Constants.BASE_URL)
                    .path(mPath);
            urlConnection = (HttpURLConnection) new URL(builder.build().toString()).openConnection();
            InputStream in = null;
            if (urlConnection.getResponseCode() == 200) {
                in = urlConnection.getInputStream();
                if (mListener != null) {
                    mListener.onCommandFinished(readStream(new InputStreamReader(in, mStringEncoding)));
                }
            } else {
                in = urlConnection.getErrorStream();
                if (mListener != null) {
                    mListener.onCommandFailed(readStream(new InputStreamReader(in)), null);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onCommandFailed(e.getMessage(), e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onCommandFailed(e.getMessage(), e);
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String readStream(Reader in) throws IOException {
        char[] buffer = new char[8096];
        StringBuilder builder = new StringBuilder();
        int read = 0;
        while ((read = in.read(buffer)) > 0) {
            builder.append(buffer, 0, read);
        }
        in.close();
        return builder.toString();
    }
}

