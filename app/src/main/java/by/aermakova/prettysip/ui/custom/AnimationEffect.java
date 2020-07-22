package by.aermakova.prettysip.ui.custom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;

/**
 * Turn around and change color animation of UI-element
 */

public class AnimationEffect {

    public void animateImageView(final ImageView v, final int newColor) {
        final ValueAnimator colorAnim = ObjectAnimator.ofFloat(0f, 1f);
        colorAnim.addUpdateListener(animation -> {
            float mul = (Float) animation.getAnimatedValue();
            int alphaOrange = adjustAlpha(newColor, mul);
            v.setColorFilter(alphaOrange, PorterDuff.Mode.SRC_ATOP);
            if (mul == 0.0) {
                v.setColorFilter(null);
            }
        });
        colorAnim.setDuration(1000);
        colorAnim.start();
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
