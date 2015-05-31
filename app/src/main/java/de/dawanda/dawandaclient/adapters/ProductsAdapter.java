package de.dawanda.dawandaclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.model.Product;
import de.dawanda.dawandaclient.utils.PictureManager;
import de.dawanda.dawandaclient.utils.Utils;

/**
 * Created by emanuele on 30.05.15.
 */
public class ProductsAdapter extends BaseExpandableListAdapter {


    public static class GroupViewHolder {

        public TextView mProductName;
        public TextView mProductPrice;
        public ImageView mProductIcon;
    }

    private final int mPictureDip;
    private final Context mContext;
    private List<Product> mDataSet;
    private final LayoutInflater mInflater;

    public ProductsAdapter(Activity activity, List<Product> dataset) {
        mContext = activity;
        mDataSet = dataset;
        mInflater = activity.getLayoutInflater();
        mPictureDip = (int) (activity.getResources().getDisplayMetrics().density * 75f);
    }

    @Override
    public int getGroupCount() {
        if (mDataSet == null) {
            return 0;
        }
        return mDataSet.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Product getGroup(int groupPosition) {
        return mDataSet.get(groupPosition);
    }

    @Override
    public Product getChild(int groupPosition, int childPosition) {
        return mDataSet.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.products_group_item, parent, false);
            holder = new GroupViewHolder();
            holder.mProductIcon = (ImageView) convertView.findViewById(R.id.product_icon);
            holder.mProductName = (TextView) convertView.findViewById(R.id.product_name);
            holder.mProductPrice = (TextView) convertView.findViewById(R.id.product_price);
            convertView.setTag(holder);
        }
        holder = (GroupViewHolder) convertView.getTag();
        Product product = getGroup(groupPosition);
        holder.mProductName.setText(product.title);

        String currency = product.price.currency;
        if ("EUR".equals(product.price.currency)) {
            currency = "\u20AC";
        }
        holder.mProductPrice.setText(String.format(currency + " %.2f", product.price.cents / 100f));

        holder.mProductIcon.setImageBitmap(
                PictureManager.getInstance(mContext).getRoundedBitmap(product.default_image.listview, mPictureDip));
        holder.mProductIcon.setTag(Utils.getNameFromUrl(product.default_image.listview));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.product_details, parent, false);
        }
        Product product = getGroup(groupPosition);
        ((TextView) convertView.findViewById(R.id.username)).setText(mContext.getString(R.string.shop_name, product.seller.username));
        ((TextView) convertView.findViewById(R.id.coutry)).setText(mContext.getString(R.string.country, product.seller.country));
        ((RatingBar) convertView.findViewById(R.id.rating)).setRating(product.seller.rating);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
