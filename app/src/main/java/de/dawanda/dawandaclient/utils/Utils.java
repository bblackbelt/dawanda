package de.dawanda.dawandaclient.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.model.Category;
import de.dawanda.dawandaclient.model.Product;


public class Utils {

    public static List<Category> createCategories(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject dataJSONObject = jsonObject.optJSONObject("data");
        if (dataJSONObject != null) {
            JSONArray dataJSONArray = dataJSONObject.optJSONArray("data");
            ArrayList<Category> categories = new ArrayList<>(dataJSONArray.length());
            for (int i = 0; i < dataJSONArray.length(); i++) {
                JSONObject airlineJSONObject = dataJSONArray.optJSONObject(i);
                if (airlineJSONObject != null) {
                    Category category = new Category();
                    category.id = airlineJSONObject.optInt("id");
                    category.image_url = airlineJSONObject.optString("image_url");
                    category.name = airlineJSONObject.optString("name");
                    if (!categories.contains(category)) {
                        categories.add(category);
                    }
                }
            }
            return categories;
        }
        return null;
    }

    public static List<Product> createProducts(String results) throws JSONException {
        JSONObject obj = new JSONObject(results);
        JSONObject dataext = obj.optJSONObject("data");
        if (dataext != null) {
            JSONObject data = dataext.optJSONObject("data");
            JSONArray products = data.optJSONArray("products");
            Type listType = new TypeToken<LinkedHashSet<Product>>() {
            }.getType();
            if (data != null) {
                LinkedHashSet p = new Gson().fromJson(products.toString(), listType);
                return new ArrayList<>(p);
            }
        }
        return null;
    }

    public static void crossfade(ImageView imageView, Drawable[] drawables) {
        final TransitionDrawable crossfader = new TransitionDrawable(drawables);
        imageView.setImageDrawable(crossfader);
        crossfader.startTransition(500);
    }

    public static void crossfade(ImageView imageView, Bitmap dest) {
        BitmapDrawable drawables[] = new BitmapDrawable[2];
        final Resources resource = imageView.getResources();
        drawables[0] = new BitmapDrawable(resource, BitmapFactory.decodeResource(resource, R.drawable.ic_launcher));
        drawables[1] = new BitmapDrawable(resource, dest);
        final TransitionDrawable crossfader = new TransitionDrawable(drawables);
        imageView.setImageDrawable(crossfader);
        crossfader.startTransition(500);
    }


    public static void showNotification(final Context context, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Bitmap createRoundedBitmap(final Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return createRoundedBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
    }

    public static Bitmap createRoundedBitmap(final Bitmap bitmap, final int width, final int heigth) {
        if (bitmap == null) {
            return null;
        }
        int side = Math.min(width, heigth);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap dst = Bitmap.createScaledBitmap(bitmap, side, side, true);
        paint.setShader(new BitmapShader(dst, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap result = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawCircle(side / 2, side / 2, side / 2, paint);

        return result;
    }

    private Utils() {
    }

    public static String getNameFromUrl(String url) {
        if (url == null) {
            return null;
        }
        return UUID.nameUUIDFromBytes(url.getBytes()).toString();
    }
}
