package de.dawanda.dawandaclient.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.activities.ProductActivity;
import de.dawanda.dawandaclient.services.PictureService;
import de.dawanda.dawandaclient.utils.Utils;
import de.dawanda.dawandaclient.adapters.CategoryBaseAdapter;
import de.dawanda.dawandaclient.model.Category;
import de.dawanda.dawandaclient.networking.CommandExecutor;
import de.dawanda.dawandaclient.networking.CommandFactory;
import de.dawanda.dawandaclient.networking.CommandListener;

/**
 * Created by emanuele on 29.05.15.
 */
public class CategoriesFragment extends Fragment implements CommandListener<String>, AdapterView.OnItemClickListener {

    private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isAdded() || isRemoving() || isDetached()) {
                return;
            }
            ListView listView = (ListView) getView().findViewById(R.id.category_list);
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


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CategoryBaseAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.category_swipe_refresh_layout);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) (24f * getResources().getDisplayMetrics().density));
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CommandExecutor.getInstance().addCommand(CommandFactory.makeCategoriesCommand(CategoriesFragment.this));
            }
        });
        TextView emptyView = (TextView) view.findViewById(R.id.empty);
        ListView listView = (ListView) view.findViewById(R.id.category_list);
        listView.setOnItemClickListener(this);
        listView.setEmptyView(emptyView);
        CommandExecutor.getInstance().addCommand(CommandFactory.makeCategoriesCommand(this));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadCastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadCastReceiver,
                new IntentFilter(PictureService.PICTURE_READY_URI));
    }

    @Override
    public void onCommandFinished(String result) {
        final View view = getView();
        if (view == null) {
            return;
        }
        try {
            final List<Category> categories = Utils.createCategories(result);
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (!isAdded() || isRemoving() || isDetached()) {
                        return;
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                    if (mAdapter == null) {
                        ListView listView = (ListView) view.findViewById(R.id.category_list);
                        listView.setAdapter(mAdapter = new CategoryBaseAdapter(getActivity(), categories));
                        return;
                    }
                    mAdapter.setData(categories);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommandFailed(String message, Throwable throwable) {
        Utils.showNotification(getActivity(), message);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtra(ProductActivity.KEY_CATEGORY, category);
        startActivity(intent);
    }
}
