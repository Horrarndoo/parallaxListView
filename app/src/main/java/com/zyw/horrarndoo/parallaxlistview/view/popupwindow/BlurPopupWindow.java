package com.zyw.horrarndoo.parallaxlistview.view.popupwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nineoldandroids.animation.ObjectAnimator;
import com.zyw.horrarndoo.parallaxlistview.R;
import com.zyw.horrarndoo.parallaxlistview.utils.UIUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.zyw.horrarndoo.parallaxlistview.view.popupwindow.PopupRootLayout.DispatchKeyEventListener;

/**
 * 弹出动画的popupwindow
 */
public class BlurPopupWindow {
    private Activity activity;
    private WindowManager.LayoutParams params;
    private boolean isDisplay;
    private WindowManager windowManager;
    private PopupRootLayout rootView;
    private ViewGroup contentLayout;

    private final int animDuration = 250;//动画执行时间
    private boolean isAniming;//动画是否在执行

    public BlurPopupWindow(Activity activity, View view) {
        this.activity = activity;

        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);

        view.setPadding(10, 10, 10, 0);//由于.9图片有部分是透明，往下padding 10个pix，左右padding10个pix为了美观
        view.setBackgroundResource(R.drawable.item_pop_bg);

        initLayout(view);
    }

    public void initLayout(View view) {

        //这是根布局
        rootView = (PopupRootLayout) View.inflate(activity, R.layout.bg_popupwindow, null);
        contentLayout = (ViewGroup) rootView.findViewById(R.id.content_layout);

        initParams();

        contentLayout.addView(view);

        //当点击根布局时, 隐藏
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();
            }
        });

        rootView.setDispatchKeyEventListener(new DispatchKeyEventListener() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (rootView.getParent() != null) {
                        dismissPopupWindow();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initParams() {
        params = new WindowManager.LayoutParams();
        params.width = MATCH_PARENT;
        params.height = MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN //布满屏幕，忽略状态栏
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS //透明状态栏
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION; //透明虚拟按键栏
        params.format = PixelFormat.TRANSLUCENT;//支持透明
        params.gravity = Gravity.LEFT | Gravity.TOP;
    }

    /**
     * 弹出选项弹窗
     *
     * @param locationView
     */
    public void displayPopupWindow(View locationView) {
        if (!isAniming) {
            isAniming = true;
            try {
                //得到该view相对于屏幕的坐标
                int[] point = new int[2];
                locationView.getLocationOnScreen(point);
                contentLayout.measure(0, 0);
                float x = point[0] + locationView.getWidth() - contentLayout.getMeasuredWidth();
                float y = point[1] + locationView.getHeight();
                contentLayout.setX(x);
                contentLayout.setY(y);

                View decorView = activity.getWindow().getDecorView();
                Bitmap bitmap = UIUtils.viewToBitmap(decorView);//将view转成bitmap
                View blurView = getBlurView(rootView, bitmap);//模糊图片
                windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                //WindowManager将处理过的blurView添加到屏幕最前端
                windowManager.addView(blurView, params);

                //回弹效果 弹出动画
                displayAnim(contentLayout, 0, 1, animDuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将bitmap模糊虚化并设置给view background
     *
     * @param view
     * @param bitmap
     * @return 虚化后的view
     */
    private View getBlurView(View view, Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap
                .getHeight() / 3, false);
        Bitmap blurBitmap = UIUtils.getBlurBitmap(activity, scaledBitmap, 5);
        view.setAlpha(0);
        view.setBackgroundDrawable(new BitmapDrawable(null, blurBitmap));
        alphaAnim(view, 0, 1, animDuration);
        return view;
    }

    public void dismissPopupWindow() {
        if (!isAniming) {
            isAniming = true;
            if (isDisplay) {
                dismissAnim(contentLayout, 1.f, 0.f, animDuration);
            }
        }
    }

    /**
     * 设置透明度属性动画
     *
     * @param view     要执行属性动画的view
     * @param start    起始值
     * @param end      结束值
     * @param duration 动画持续时间
     */
    private void alphaAnim(final View view, int start, int end, int duration) {
        ObjectAnimator.ofFloat(view, "alpha", start, end).setDuration(duration).start();
    }

    /**
     * 弹出popupWindow属性动画
     *
     * @param view
     * @param start
     * @param end
     * @param duration
     */
    private void displayAnim(final View view, float start, final float end, int duration) {
        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setPivotX(view.getMeasuredWidth());
                view.setPivotY(0);
                view.setScaleX(value);
                view.setScaleY(value);
                view.setAlpha(value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAniming = false;
                isDisplay = true;
                onPopupStateListener.onDisplay(isDisplay);
            }
        });
        va.start();
    }

    /**
     * 消失popupWindow属性动画
     *
     * @param view
     * @param start
     * @param end
     * @param duration
     */
    public void dismissAnim(final View view, float start, final float end, final int duration) {

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setPivotX(view.getWidth());
                view.setPivotY(0);
                view.setScaleX(value);
                view.setScaleY(value);
                view.setAlpha(value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    if (isDisplay) {
                        windowManager.removeViewImmediate(rootView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isAniming = false;
                isDisplay = false;
                onPopupStateListener.onDismiss(isDisplay);
            }
        });
        va.start();
    }

    private OnPopupStateListener onPopupStateListener;

    public void setOnPopupStateListener(OnPopupStateListener onPopupStateListener) {
        this.onPopupStateListener = onPopupStateListener;
    }

    /**
     * popupWindow显示和消失状态变化接口
     */
    public interface OnPopupStateListener {
        /**
         * popupWindow状态变化
         * @param isDisplay popupWindow当前状态 true:显示 false:消失
         */
        //        void onChange(boolean isDisplay);

        /**
         * popupWindow为显示状态
         */
        void onDisplay(boolean isDisplay);

        /**
         * popupWindow为消失状态
         */
        void onDismiss(boolean isDisplay);
    }
}
