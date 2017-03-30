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
import android.widget.ImageView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.zyw.horrarndoo.parallaxlistview.R;
import com.zyw.horrarndoo.parallaxlistview.utils.UIUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.zyw.horrarndoo.parallaxlistview.view.popupwindow.PopupRootLayout
        .DispatchKeyEventListener;

/**
 * 弹出动画的popupwindow
 */
public class BlurPopupWindow {
    /**
     * 顶部弹出popupWindow关键字
     */
    public static final int KEYWORD_LOCATION_TOP = 1;
    /**
     * 点击处弹出popupWindow关键字
     */
    public static final int KEYWORD_LOCATION_CLICK = 2;
    private Activity activity;
    private WindowManager.LayoutParams params;
    private boolean isDisplay;
    private WindowManager windowManager;
    private PopupRootLayout rootView;
    private ViewGroup contentLayout;

    private final int animDuration = 250;//动画执行时间
    private boolean isAniming;//动画是否在执行

    /**
     * BlurPopupWindow构造函数
     *
     * @param activity 当前弹出/消失BlurPopupWindow的Activity
     * @param view     要弹出/消失的view内容
     *                 默认从点击处弹出/消失popupWindow
     */
    public BlurPopupWindow(Activity activity, View view) {
        initBlurPopupWindow(activity, view, KEYWORD_LOCATION_CLICK);
    }

    /**
     * BlurPopupWindow构造函数
     *
     * @param activity 当前弹出/消失BlurPopupWindow的Activity
     * @param view     要弹出/消失的view内容
     * @param keyword  弹出/消失位置关键字 KEYWORD_LOCATION_TOP：顶部弹出
     *                 KEYWORD_LOCATION_CLICK：点击位置弹出
     */
    public BlurPopupWindow(Activity activity, View view, int keyword) {
        initBlurPopupWindow(activity, view, keyword);
    }

    /**
     * BlurPopupWindow初始化
     *
     * @param activity 当前弹出BlurPopupWindow的Activity
     * @param view     要弹出/消失的view内容
     * @param keyword  弹出/消失位置关键字 KEYWORD_LOCATION_TOP：顶部弹出
     *                 KEYWORD_LOCATION_CLICK：点击位置弹出
     */
    private void initBlurPopupWindow(Activity activity, View view, int keyword) {
        this.activity = activity;

        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        switch (keyword) {
            case KEYWORD_LOCATION_CLICK:
                view.setPadding(5, 10, 5, 0);//由于.9图片有部分是透明，往下padding 10个pix，左右padding 5个pix为了美观
                view.setBackgroundResource(R.drawable.popup_bg);
                break;
            case KEYWORD_LOCATION_TOP:
                ImageView imageView = (ImageView) view;
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                imageView.setImageDrawable(activity.getResources().getDrawable(R.mipmap.popup_top_bg));
                break;
            default:
                break;
        }
        initLayout(view, keyword);
    }

    private void initLayout(View view, final int keyword) {
        rootView = (PopupRootLayout) View.inflate(activity, R.layout.popupwindow_layout, null);
        contentLayout = (ViewGroup) rootView.findViewById(R.id.content_layout);

        initParams();

        contentLayout.addView(view);

        //点击根布局时, 隐藏弹出的popupWindow
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow(keyword);
            }
        });

        rootView.setDispatchKeyEventListener(new DispatchKeyEventListener() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (rootView.getParent() != null) {
                        dismissPopupWindow(keyword);
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

    /**
     * 弹出选项弹窗
     * 默认从点击位置弹出
     *
     * @param locationView
     */
    public void displayPopupWindow(View locationView) {
        displayPopupWindow(locationView, KEYWORD_LOCATION_CLICK);
    }

    /**
     * 弹出选项弹窗
     *
     * @param locationView 被点击的view
     * @param keyword      弹出位置关键字
     */
    public void displayPopupWindow(View locationView, int keyword) {
        if (!isAniming) {
            isAniming = true;
            try {
                int[] point = new int[2];
                float x = 0;
                float y = 0;

                contentLayout.measure(0, 0);
                switch (keyword) {
                    case KEYWORD_LOCATION_CLICK:
                        //得到该view相对于屏幕的坐标
                        locationView.getLocationOnScreen(point);
                        x = point[0] + locationView.getWidth() - contentLayout.getMeasuredWidth();
                        y = point[1] + locationView.getHeight();
                        break;
                    case KEYWORD_LOCATION_TOP:
                        x = 0;
                        y = 0;
                        break;
                    default:
                        break;
                }

                contentLayout.setX(x);
                contentLayout.setY(y);

                View decorView = activity.getWindow().getDecorView();
                Bitmap bitmap = UIUtils.viewToBitmap(decorView);//将view转成bitmap
                View blurView = getBlurView(rootView, bitmap);//模糊图片
                windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                //将处理过的blurView添加到window
                windowManager.addView(blurView, params);

                //popupWindow动画
                popupAnim(contentLayout, 0.f, 1.f, animDuration, keyword, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 消失popupWindow
     * 默认从点击处开始消失
     */
    public void dismissPopupWindow() {
        dismissPopupWindow(KEYWORD_LOCATION_CLICK);
    }

    /**
     * 消失popupWindow
     * @param keyword  消失位置关键字 KEYWORD_LOCATION_TOP：顶部弹出
     *                 KEYWORD_LOCATION_CLICK：点击位置弹出
     */
    public void dismissPopupWindow(int keyword) {
        if (!isAniming) {
            isAniming = true;
            if (isDisplay) {
                popupAnim(contentLayout, 1.f, 0.f, animDuration, keyword, false);
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
     * popupWindow属性动画
     *
     * @param view
     * @param start
     * @param end
     * @param duration
     * @param keyword
     * @param isToDisplay 显示或消失 flag值
     */
    private void popupAnim(final View view, float start, final float end, int duration, final int
            keyword, final boolean isToDisplay) {
        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                switch (keyword) {
                    case KEYWORD_LOCATION_CLICK:
                        view.setPivotX(view.getMeasuredWidth());
                        view.setPivotY(0);
                        view.setScaleX(value);
                        view.setScaleY(value);
                        view.setAlpha(value);
                        break;
                    case KEYWORD_LOCATION_TOP:
                        view.setPivotX(0);
                        view.setPivotY(0);
                        view.setScaleY(value);
                        break;
                    default:
                        break;
                }

            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAniming = false;
                if(isToDisplay) {//当前为弹出popupWindow
                    isDisplay = true;
                    onPopupStateListener.onDisplay(isDisplay);
                }else{//当前为消失popupWindow
                    try {
                        if (isDisplay) {
                            windowManager.removeViewImmediate(rootView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isDisplay = false;
                    onPopupStateListener.onDismiss(isDisplay);
                }
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
