package com.zyw.horrarndoo.parallaxlistview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.zyw.horrarndoo.parallaxlistview.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

/**
 * 弹出动画的popupwindow
 */
public class HintPopupWindow {

    private Activity activity;
    private WindowManager.LayoutParams params;
    private boolean isShow;
    private WindowManager windowManager;
    private ViewGroup rootView;
    private ViewGroup linearLayout;

    private final int animDuration = 250;//动画执行时间
    private boolean isAniming;//动画是否在执行

    /**
     * @param contentList 点击item的内容文字
     * @param clickList   点击item的事件
     *                    文字和click事件的list是对应绑定的
     */
    public HintPopupWindow(Activity activity, List<String> contentList, List<View
            .OnClickListener> clickList) {

        this.activity = activity;
        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);

        initLayout(contentList, clickList);
    }

    /**
     * @param contentList 点击item内容的文字
     * @param clickList   点击item的事件
     */
    public void initLayout(List<String> contentList, List<View.OnClickListener> clickList) {

        //这是根布局
        rootView = (ViewGroup) View.inflate(activity, R.layout.item_root_hintpopupwindow, null);
        linearLayout = (ViewGroup) rootView.findViewById(R.id.linearLayout);

        //格式化点击item, 将文字和click事件一一绑定上去
        List<View> list = new ArrayList<>();
        for (int x = 0; x < contentList.size(); x++) {
            View view = View.inflate(activity, R.layout.item_hint_popupwindow, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_content);
            View v_line = view.findViewById(R.id.v_line);
            textView.setText(contentList.get(x));
            linearLayout.addView(view);
            list.add(view);
            if (x == 0) {
                v_line.setVisibility(View.INVISIBLE);
            } else {
                v_line.setVisibility(View.VISIBLE);
            }
        }
        for (int x = 0; x < list.size(); x++) {
            list.get(x).setOnClickListener(clickList.get(x));
        }

        //这里给你根布局设置背景透明, 为的是让他看起来和activity的布局一样
        params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE //焦点
                | FLAG_LAYOUT_IN_SCREEN
                | FLAG_TRANSLUCENT_STATUS
                | FLAG_TRANSLUCENT_NAVIGATION;
        params.format = PixelFormat.TRANSLUCENT;// 支持透明
        params.gravity = Gravity.LEFT | Gravity.TOP;

        //当点击根布局时, 隐藏
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();
            }
        });

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //如果是显示状态那么隐藏视图
                if (keyCode == KeyEvent.KEYCODE_BACK && isShow)
                    dismissPopupWindow();
                return isShow;
            }
        });
    }

    /**
     * 弹出选项弹窗
     *
     * @param locationView 默认在该view的下方弹出, 和popupWindow类似
     */
    public void showPopupWindow(View locationView) {
        if (!isAniming) {
            isAniming = true;
            try {
                //这个步骤是得到该view相对于屏幕的坐标, 注意不是相对于父布局哦!
                int[] arr = new int[2];
                locationView.getLocationOnScreen(arr);
                linearLayout.measure(0, 0);
                float x = arr[0] + locationView.getWidth() - linearLayout.getMeasuredWidth();
                float y = arr[1] + locationView.getHeight();
                linearLayout.setX(x);
                linearLayout.setY(y);

                View decorView = activity.getWindow().getDecorView();
                Bitmap bitmap = getBitmapByView(decorView);//将view转成bitmap
                View blurView = getBlurView(rootView, bitmap);//模糊图片
                //这里就是使用WindowManager直接将我们处理好的view添加到屏幕最前端
                windowManager.addView(blurView, params);

                //这一步就是有回弹效果的弹出动画, 我用属性动画写的, 很简单
                showAnim(linearLayout, 0, 1, animDuration, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到bitmap位图, 传入View对象
     */
    public static Bitmap getBitmapByView(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private View getBlurView(View view, Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap
                .getHeight() / 3, false);
        Bitmap blurBitmap = getBlurBitmap(activity, scaledBitmap, 5);
        view.setAlpha(0);
        view.setBackgroundDrawable(new BitmapDrawable(null, blurBitmap));
        alphaAnim(view, 0, 1, animDuration);
        return view;
    }

    public static Bitmap getBlurBitmap(Context context, Bitmap bitmap, int radius) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return blurBitmap(context, bitmap, radius);
        }
        return bitmap;
    }

    /**
     * android系统的模糊方法
     *
     * @param bitmap 要模糊的图片
     * @param radius 模糊等级 >=0 && <=25
     */
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, int radius) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Let's create an empty bitmap with the same size of the bitmap we want to blur
            Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap
                    .Config.ARGB_8888);
            //Instantiate a new Renderscript
            RenderScript rs = RenderScript.create(context);
            //Create an Intrinsic Blur Script using the Renderscript
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
            //Set the radius of the blur
            blurScript.setRadius(radius);
            //Perform the Renderscript
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);
            //Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap);
            //recycle the original bitmap
            bitmap.recycle();
            //After finishing everything, we destroy the Renderscript.
            rs.destroy();
            return outBitmap;
        } else {
            return bitmap;
        }
    }

    public void dismissPopupWindow() {
        if (!isAniming) {
            isAniming = true;
            isShow = false;
            goneAnim(linearLayout, 0.95f, 1, animDuration / 3, true);
        }
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return params;
    }

    public ViewGroup getLayout() {
        return linearLayout;
    }

    /**
     * popupwindow是否是显示状态
     */
    public boolean isShow() {
        return isShow;
    }

    private void alphaAnim(final View view, int start, int end, int duration) {

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setAlpha(value);
            }
        });
        va.start();
    }

    private void showAnim(final View view, float start, final float end, int duration, final
    boolean isWhile) {

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setPivotX(view.getWidth());
                view.setPivotY(0);
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isWhile) {
                    showAnim(view, end, 0.95f, animDuration / 3, false);
                } else {
                    isAniming = false;
                }
            }
        });
        va.start();
    }

    public void goneAnim(final View view, float start, final float end, int duration, final
    boolean isWhile) {

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(duration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setPivotX(view.getWidth());
                view.setPivotY(0);
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isWhile) {
                    alphaAnim(rootView, 1, 0, animDuration);
                    goneAnim(view, end, 0f, animDuration, false);
                } else {
                    try {
                        windowManager.removeViewImmediate(rootView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isAniming = false;
                }
            }
        });
        va.start();
    }
}
