package de.dawanda.dawandaclient.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import de.dawanda.dawandaclient.R;
import de.dawanda.dawandaclient.adapters.DaWandaFragmentPagerAdapter;
import de.dawanda.dawandaclient.adapters.DaWandaFragmentPagerAdapter.FRAGMENT_ID;
import de.dawanda.dawandaclient.adapters.DaWandaFragmentPagerAdapter.FragmentDescriptor;
import de.dawanda.dawandaclient.widgets.FlipPageTransformer;

public class DaWandaActivity extends DaWandaBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_da_wanda);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final ArrayList<FragmentDescriptor> dataset = new ArrayList<>();
        FragmentDescriptor descriptor = new FragmentDescriptor();
        descriptor.mFragmentTitle = getString(R.string.categories);
        descriptor.mFragmentId = FRAGMENT_ID.CATEGORIES;
        dataset.add(descriptor);
        descriptor = new FragmentDescriptor();
        descriptor.mFragmentTitle = getString(R.string.details);
        descriptor.mFragmentId = FRAGMENT_ID.DETAILS;
        dataset.add(descriptor);

        viewPager.setAdapter(new DaWandaFragmentPagerAdapter(this, dataset));
        viewPager.setPageTransformer(false, new FlipPageTransformer());
    }
}
