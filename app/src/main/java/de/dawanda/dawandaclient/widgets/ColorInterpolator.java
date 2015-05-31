package de.dawanda.dawandaclient.widgets;

import android.graphics.Color;
import android.view.animation.Interpolator;

/**
 * Created by emanuele on 31.05.15.
 */
public class ColorInterpolator implements Interpolator {

    private int mStartColor;
    private int mEndColor;

    public ColorInterpolator(int from, int to) {
        mStartColor = from;
        mEndColor = to;
    }

    @Override
    public float getInterpolation(float input) {
        return getValue(input);
    }

    private int getValue(float input) {
        int startRed = (mStartColor >> 16) & 0xFF;
        int endRed = (mEndColor >> 16) & 0xFF;

        int red = (int) (startRed + (endRed - startRed) * input);

        int startGreen = (mStartColor >> 8) & 0xFF;
        int endGreen = (mEndColor >> 8) & 0xFF;

        int green = (int) (startGreen + (endGreen - startGreen) * input);

        int startBlue = mStartColor & 0xFF;
        int endBlue = mEndColor & 0xFF;

        int blue = (int) (startBlue + (endBlue - startBlue) * input);

        return (0xFF << 24 | red << 16) | green << 8 | blue;
    }
}
