package com.zyw.horrarndoo.parallaxlistview.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;

import com.zyw.horrarndoo.parallaxlistview.utils.ColorUtil;

/**
 * Created by Horrarndoo on 2017/3/27.
 */

public class GradientLayout extends FrameLayout implements OnScrollListener {
    private TitleBar tb_title;
    private ParallaxListView plv;
    private static final float CRITICA_LVALUE = 0.5f;

    public GradientLayout(Context context) {
        this(context, null);
    }

    public GradientLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("only can 2 child in this view");
        } else {
            if (getChildAt(0) instanceof ParallaxListView) {
                plv = (ParallaxListView) getChildAt(0);
                plv.setOnScrollListener(this);
            } else {
                throw new IllegalArgumentException("child(0) must be ParallaxListView");
            }
            tb_title = (TitleBar) getChildAt(1);
            tb_title.setOnGradientStateChangeListenr(this);
        }
    }

    /**
     * 设置title背景色
     *
     * @param color
     */
    public void setTitleBackground(int color) {
        tb_title.setBackgroundColor(color);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        if (firstVisibleItem == 0) {
            View headView = view.getChildAt(0);
            if (headView != null) {
                //如果上滑超过headView高度值一半+title高度，开启伴随动画
                float slideValue = Math.abs(headView.getTop()) - headView.getHeight() / 2.f +
                        tb_title.getHeight();
                if (slideValue < 0)
                    slideValue = 0;
                float fraction = slideValue / (headView.getHeight() / 2.f);
                if (fraction > 1) {
                    fraction = 1;
                }
                //Log.e("tag", "fraction = " + fraction);
                excuteAnim(fraction);
            }
        } else {
            float fraction = 1;
            excuteAnim(fraction);
        }
    }

    private void excuteAnim(float fraction) {
        int color = (int) ColorUtil.evaluateColor(fraction, Color.parseColor("#0000ccff"), Color
                .parseColor("#ff00ccff"));
        setTitleBackground(color);
        onGradientStateChangeListenr.onChange(fraction, CRITICA_LVALUE);
    }

    /**
     * 设置TitleBar text
     * @param msg
     */
    public void setTitleText(String msg){
        tb_title.setTitleText(msg);
    }

    private OnGradientStateChangeListenr onGradientStateChangeListenr;

    public void setOnGradientStateChangeListenr(OnGradientStateChangeListenr onGradientStateChangeListenr){
        this.onGradientStateChangeListenr = onGradientStateChangeListenr;
    }

    /**
     * Gradient变化临界值监听
     */
    public interface OnGradientStateChangeListenr{
        /**
         * 当fraction超过临界值时回调
         * @param fraction
         * @param criticalValue
         */
        public void onChange(float fraction, float criticalValue);
    }
}
