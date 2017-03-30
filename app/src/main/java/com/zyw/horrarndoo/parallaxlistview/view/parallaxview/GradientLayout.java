package com.zyw.horrarndoo.parallaxlistview.view.parallaxview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.zyw.horrarndoo.parallaxlistview.utils.ColorUtil;
import com.zyw.horrarndoo.parallaxlistview.utils.UIUtils;

/**
 * Created by Horrarndoo on 2017/3/27.
 */

public class GradientLayout extends FrameLayout implements OnScrollListener, ParallaxListView.OnRefeshChangeListener {
    private TitleBar tb_title;
    private ParallaxListView plv;
    private static final float CRITICAL_VALUE = 0.5f;
    private OnGradientStateChangeListenr onGradientStateChangeListenr;
    private OnRefeshChangeListener onRefeshChangeListener;
    private Context context;

    /**
     * 设置list刷新状态监听
     * @param onRefeshChangeListener
     */
    public void setOnRefeshChangeListener(OnRefeshChangeListener onRefeshChangeListener){
        this.onRefeshChangeListener = onRefeshChangeListener;
    }

    /**
     * 设置Gradient状态监听
     * @param onGradientStateChangeListenr
     */
    public void setOnGradientStateChangeListenr(OnGradientStateChangeListenr onGradientStateChangeListenr){
        this.onGradientStateChangeListenr = onGradientStateChangeListenr;
    }

    public GradientLayout(Context context) {
        this(context, null);
    }

    public GradientLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
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
                plv.setOnRefeshChangeListener(this);
            } else {
                throw new IllegalArgumentException("child(0) must be ParallaxListView");
            }
            tb_title = (TitleBar) getChildAt(1);
            tb_title.setTitleBarListenr(this);
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
        onGradientStateChangeListenr.onChange(fraction, CRITICAL_VALUE);
    }

    /**
     * 设置TitleBar text
     * @param msg
     */
    public void setTitleText(String msg){
        tb_title.setTitleText(msg);
    }

    @Override
    public void onListRefesh() {
        onRefeshChangeListener.onListRefesh();
    }

    @Override
    public void onListRefeshFinish(final boolean isRefeshSuccess) {
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if(isRefeshSuccess){
                    Toast.makeText(UIUtils.getContext(), "refesh success.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UIUtils.getContext(), "refesh failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //不论刷新成功还是失败，都要通知titleBar刷新完成
        onRefeshChangeListener.onListRefeshFinish();
    }

    /**
     * GradientLayout中的子list列表刷新状态监听
     */
    public interface OnRefeshChangeListener{
        /**
         * 开始刷新列表，请求数据
         */
        void onListRefesh();
        /**
         * 刷新列表完成
         */
        void onListRefeshFinish();
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
