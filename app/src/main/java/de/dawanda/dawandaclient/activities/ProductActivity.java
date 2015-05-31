package de.dawanda.dawandaclient.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.adapters.ProductsAdapter;
import de.dawanda.dawandaclient.model.Category;
import de.dawanda.dawandaclient.model.Product;
import de.dawanda.dawandaclient.networking.CommandExecutor;
import de.dawanda.dawandaclient.networking.CommandFactory;
import de.dawanda.dawandaclient.networking.CommandListener;
import de.dawanda.dawandaclient.services.PictureService;
import de.dawanda.dawandaclient.utils.PictureManager;
import de.dawanda.dawandaclient.utils.Utils;

/**
 * Created by emanuele on 30.05.15.
 */
public class ProductActivity extends DaWandaBaseActivity implements CommandListener<String> {


    private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ListView listView = (ListView) findViewById(R.id.products_list);
            String name = intent.getStringExtra(PictureService.KEY_PIC_NAME);
            int size = (int) (getResources().getDisplayMetrics().density * 75f);
            Bitmap bitmap = Utils.createRoundedBitmap(
                    (Bitmap) intent.getParcelableExtra(PictureService.KEY_PIC_PATH), size, size);
            if (listView != null) {
                ImageView row = (ImageView) listView.findViewWithTag(name);
                if (row != null) {
                    Utils.crossfade(row, bitmap);
                }
            }
        }
    };

    public static final String KEY_CATEGORY = "KEY_CATEGORY";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mLastExpandedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_activity_layout);
        Category category = getIntent().getParcelableExtra(KEY_CATEGORY);
        if (category != null) {
            setTitle(category.name);
        }
        TextView emptyView = (TextView) findViewById(R.id.empty);
        final ExpandableListView listView = (ExpandableListView) findViewById(R.id.products_list);
        listView.setEmptyView(emptyView);

        int size = (int) (getResources().getDisplayMetrics().density * 50f);
        Bitmap bitmap = PictureManager.getInstance(this).getRoundedBitmap(category.image_url, size);
        setHomeAsUpIndicator(new BitmapDrawable(getResources(), bitmap));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.product_swipe_refresh_layout);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) (24f * getResources().getDisplayMetrics().density));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CommandExecutor.getInstance().addCommand(CommandFactory.makeProductsCommand(ProductActivity.this));
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        CommandExecutor.getInstance().addCommand(CommandFactory.makeProductsCommand(this));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReceiver,
                new IntentFilter(PictureService.PICTURE_READY_URI));
    }


    @Override
    public void onCommandFinished(String result) {
        try {
            final List<Product> products = Utils.createProducts(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }
                    final ExpandableListView listView = (ExpandableListView) findViewById(R.id.products_list);
                    listView.setAdapter(new ProductsAdapter(ProductActivity.this, products));
                    listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                        @Override
                        public void onGroupExpand(int groupPosition) {
                            if (mLastExpandedPosition != -1
                                    && groupPosition != mLastExpandedPosition) {
                                listView.collapseGroup(mLastExpandedPosition);
                            }
                            mLastExpandedPosition = groupPosition;
                        }
                    });
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommandFailed(String message, Throwable throwable) {
        Utils.showNotification(this, message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
