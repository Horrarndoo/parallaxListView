package com.zyw.horrarndoo.parallaxlistview.view.parallaxview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by Horrarndoo on 2017/3/24.
 */

public class ParallaxListView extends ListView{
    /**
     * imageView最大高度
     */
    private int maxHeight;
    /**
     * imageView初始高度
     */
    private int orignalHeight;
    /**
     * 头布局imageView
     */
    private ImageView ivHead;


    public ParallaxListView(Context context) {
        super(context);
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化ParallaxImage的初始参数
     *
     * @param imageView
     */
    public void initParallaxImageParams(final ImageView imageView) {
        this.ivHead = imageView;
        //设定ImageView最大高度
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                orignalHeight = imageView.getHeight();
                Log.e("tag", "orignalHeight = " + orignalHeight);
                //获取图片的高度
                int drawbleHeight = imageView.getDrawable().getIntrinsicHeight();
                maxHeight = orignalHeight > drawbleHeight ? orignalHeight * 2 : drawbleHeight;
                Log.e("tag", "maxHeight = " + maxHeight);
            }
        });
    }

    /**
     * 在listview滑动到头的时候执行，可以获取到继续滑动的距离和方向
     * deltaX：继续滑动x方向的距离
     * deltaY：继续滑动y方向的距离     负：表示顶部到头   正：表示底部到头
     * maxOverScrollX:x方向最大可以滚动的距离
     * maxOverScrollY：y方向最大可以滚动的距离
     * isTouchEvent: true: 是手指拖动滑动     false:表示fling靠惯性滑动;
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int
            scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean
                                           isTouchEvent) {
        //Log.e("tag", "deltaY: " + deltaY + "  isTouchEvent:" + isTouchEvent);
            if (deltaY < 0 && isTouchEvent) {//顶部到头，并且是手动拖到顶部
            if (ivHead != null) {
                int newHeight = ivHead.getHeight() - deltaY / 3;
                if (newHeight > maxHeight) {
                    newHeight = maxHeight;//限定拖动最大高度范围
                }
                ivHead.getLayoutParams().height = newHeight;//重新设置ivHead的高度值
                //使布局参数生效
                ivHead.requestLayout();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_UP){
                //放手的时候讲imageHead的高度缓慢从当前高度恢复到最初高度
                final ValueAnimator animator = ValueAnimator.ofInt(ivHead.getHeight(), orignalHeight);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int animateValue = (int) animator.getAnimatedValue();
                        ivHead.getLayoutParams().height = animateValue;
                        //使布局参数生效
                        ivHead.requestLayout();
                    }
                });
                animator.setInterpolator(new OvershootInterpolator(3.f));//弹性插值器
                animator.setDuration(350);
                animator.start();
        }
        return super.onTouchEvent(ev);
    }
}
