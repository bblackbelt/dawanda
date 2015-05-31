package de.dawanda.dawandaclient.widgets;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by emanuele on 31.05.15.
 */


public class FlipPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View page, float position) {
        ViewCompat.setTranslationX(page,-1*page.getWidth()*position);
        int pivot = position < 0 ? 0 : page.getWidth();
        ViewCompat.setPivotX(page, pivot);
        ViewCompat.setScaleX(page, 1 - Math.abs(position));

    /*    ViewCompat.setTranslationX(page, -1 * page.getWidth() * position);
        float alpha = (position >= -.5 && position <= .5) ? 1f : 0;
        ViewCompat.setAlpha(page, alpha);
        ViewCompat.setRotationY(page, position * 180);*/
    }
}