package de.dawanda.dawandaclient.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;
import java.util.Random;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.model.Product;
import de.dawanda.dawandaclient.networking.CommandExecutor;
import de.dawanda.dawandaclient.networking.CommandFactory;
import de.dawanda.dawandaclient.networking.CommandListener;
import de.dawanda.dawandaclient.services.PictureService;
import de.dawanda.dawandaclient.utils.PictureManager;
import de.dawanda.dawandaclient.utils.Utils;

/**
 * Created by emanuele on 29.05.15.
 */
public class DetailsFragment extends Fragment implements CommandListener<String> {

    private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateDetails();
        }
    };

    private DialogFragment mDialog;
    private int mLastColorSeen;
    private Random mRandom = new Random();
    private List<Product> mProducts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommandExecutor.getInstance().addCommand(CommandFactory.makeProductsCommand(this));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        view.setBackgroundColor(mLastColorSeen = getRandomColor());
        view.findViewById(R.id.details_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdded() || isRemoving() || isDetached()) {
                    return;
                }
                updateBackground(view);
                updateDetails();
            }
        });

        view.findViewById(R.id.clickme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                mDialog = new HelloWorldDialogFragment();
                mDialog.show(getFragmentManager(), null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadCastReceiver,
                new IntentFilter(PictureService.PICTURE_READY_URI));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadCastReceiver);
    }

    private void updateBackground(View view) {
        ColorDrawable[] colorDrawables = new ColorDrawable[]{new ColorDrawable(mLastColorSeen), new ColorDrawable(getRandomColor())};
        final TransitionDrawable crossfader = new TransitionDrawable(colorDrawables);
        view.setBackgroundDrawable(crossfader);
        crossfader.startTransition(500);
    }

    private int getRandomColor() {
        return Color.rgb(mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
    }

    @Override
    public void onCommandFinished(String result) {
        try {
            mProducts = Utils.createProducts(result);
            updateDetails();
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showNotification(getActivity(), "error:  " + e.getMessage());
        }
    }

    private void updateDetails() {
        if (mProducts == null || mProducts.isEmpty()) {
            return;
        }
        final Product product = mProducts.get(mRandom.nextInt(mProducts.size()));
        final View view = getView();
        if (view == null) {
            return;
        }

        final ImageView imageView = (ImageView) view.findViewById(R.id.details_picture);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || isRemoving() || isDetached()) {
                    return;
                }
                imageView.setImageBitmap(PictureManager.getInstance(getActivity()).getBitmap(product.default_image.full));
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(500);
                imageView.startAnimation(animation);
            }
        });
    }

    @Override
    public void onCommandFailed(String message, Throwable throwable) {
        Utils.showNotification(getActivity(), message);
    }
}
