package de.dawanda.dawandaclient.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import de.dawanda.dawandaclient.fragments.CategoriesFragment;
import de.dawanda.dawandaclient.fragments.DetailsFragment;

/**
 * Created by emanuele on 29.05.15.
 */
public class DaWandaFragmentPagerAdapter extends FragmentPagerAdapter {

    public static class FragmentDescriptor {
        public FRAGMENT_ID mFragmentId;
        public String mFragmentTitle;
        public Bundle mArguments;
    }

    public enum FRAGMENT_ID {
        CATEGORIES,
        DETAILS;
    }

    private final ArrayList<FragmentDescriptor> mDataSet;

    public DaWandaFragmentPagerAdapter(final AppCompatActivity activity, final ArrayList<FragmentDescriptor> dataset) {
        super(activity.getSupportFragmentManager());
        mDataSet = dataset;
    }

    @Override
    public Fragment getItem(int i) {
        final Fragment fragment;
        switch (mDataSet.get(i).mFragmentId) {
            case CATEGORIES:
                fragment = new CategoriesFragment();
                break;
            case DETAILS:
                fragment = new DetailsFragment();
                break;
            default:
                fragment = null;
        }
        if (fragment != null) {
            fragment.setArguments(mDataSet.get(i).mArguments);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        if (mDataSet == null) {
            return 0;
        }
        return mDataSet.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDataSet.get(position).mFragmentTitle;
    }
}
