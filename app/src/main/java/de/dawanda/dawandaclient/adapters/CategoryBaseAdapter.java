package de.dawanda.dawandaclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.model.Category;
import de.dawanda.dawandaclient.utils.PictureManager;
import de.dawanda.dawandaclient.utils.Utils;

/**
 * Created by emanuele on 29.05.15.
 */
public class CategoryBaseAdapter extends BaseAdapter {


    public static class ViewHolder {
        public ImageView mCategoryImage;
        public TextView mCategoryName;
    }

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<Category> mDataSet;
    private final int mPictureDip;

    public CategoryBaseAdapter(Activity activity, List<Category> dataset) {
        mContext = activity;
        mDataSet = dataset;
        mInflater = activity.getLayoutInflater();
        mPictureDip = (int) (activity.getResources().getDisplayMetrics().density * 75f);
    }

    @Override
    public int getCount() {
        if (mDataSet == null) {
            return 0;
        }
        return mDataSet.size();
    }

    @Override
    public Category getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.category_item, parent, false);
            holder = new ViewHolder();
            holder.mCategoryImage = (ImageView) convertView.findViewById(R.id.category_image);
            holder.mCategoryName = (TextView) convertView.findViewById(R.id.category_name);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        Category category = getItem(position);
        holder.mCategoryName.setText(category.name);

        holder.mCategoryImage.setImageBitmap(
                PictureManager.getInstance(mContext).getRoundedBitmap(category.image_url, mPictureDip));
        holder.mCategoryImage.setTag(Utils.getNameFromUrl(category.image_url));

        return convertView;
    }


    public void setData(List<Category> categories) {
        synchronized (mDataSet) {
            mDataSet = new ArrayList<>(categories);
        }
        notifyDataSetChanged();
    }
}
