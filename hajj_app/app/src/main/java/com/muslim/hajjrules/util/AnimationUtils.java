package com.muslim.hajjrules.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.muslim.hajjrules.R;

public class AnimationUtils {
    
    public static void slideIn(Activity activity, View view, boolean fromRight) {
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(
            activity,
            fromRight ? R.anim.slide_in_right : R.anim.slide_in_left
        );
        view.startAnimation(animation);
    }
    
    public static void slideOut(Activity activity, View view, boolean toRight) {
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(
            activity,
            toRight ? R.anim.slide_out_right : R.anim.slide_out_left
        );
        view.startAnimation(animation);
    }
    
    public static void expand(final View view) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Set initial height to 0 and show the view
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                    ? ViewGroup.LayoutParams.WRAP_CONTENT
                    : (int)(targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration((int)(targetHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }
    
    public static void collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration((int)(initialHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }
    
    public static void fadeIn(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
            .alpha(1f)
            .setDuration(300)
            .setListener(null);
    }
    
    public static void fadeOut(View view) {
        view.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction(() -> view.setVisibility(View.GONE));
    }
}
