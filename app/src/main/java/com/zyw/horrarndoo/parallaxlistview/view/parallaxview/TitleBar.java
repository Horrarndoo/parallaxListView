package com.zyw.horrarndoo.parallaxlistview.view.parallaxview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.zyw.horrarndoo.parallaxlistview.R;
import com.zyw.horrarndoo.parallaxlistview.utils.UIUtils;
import com.zyw.horrarndoo.parallaxlistview.view.popupwindow.BlurPopupWindow;

import java.util.ArrayList;

import static com.zyw.horrarndoo.parallaxlistview.view.parallaxview.GradientLayout.OnGradientStateChangeListenr;

/**
 * Created by Horrarndoo on 2017/3/27.
 */

public class TitleBar extends LinearLayout implements OnClickListener {
    private Button btn_back;
    private Button btn_add;
    private RelativeLayout rl_title;
    private ImageView iv_title;
    private TextView tv_title;
    private boolean isDisplay;
    private BlurPopupWindow blurPopupWindow;
    private Context context;

    private OnBarClicklistener onBarClicklistener;

    public void setOnBarChildClicklistener(OnBarClicklistener onBarClicklistener) {
        this.onBarClicklistener = onBarClicklistener;
    }

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setActivity(Activity activity) {
        this.context = activity;
        //initPopupWindow((Activity) context);
    }

    private void initPopupWindow(final Activity context) {
        //下面的操作是初始化弹出数据
        ArrayList<String> strList = new ArrayList<>();
        strList.add("选项item1");
        strList.add("选项item2");
        strList.add("选项item3");

        ArrayList<View.OnClickListener> clickList = new ArrayList<>();
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click click click.", Toast.LENGTH_SHORT).show();
            }
        };
        clickList.add(clickListener);
        clickList.add(clickListener);
        clickList.add(clickListener);
        clickList.add(clickListener);

        //具体初始化逻辑看下面的图
        //blurPopupWindow = new BlurPopupWindow(context, strList, clickList);
//        blurPopupWindow.setOnPopupStateListener(new BlurPopupWindow.OnPopupStateListener() {
//            @Override
//            public void onDisplay(boolean isDisplay) {
//                TitleBar.this.isDisplay = isDisplay;
//            }
//
//            @Override
//            public void onDismiss(boolean isDisplay) {
//                TitleBar.this.isDisplay = isDisplay;
//                dismissAnim();
//            }
//        });
    }

    /**
     * 设置Gradient临界值监听
     *
     * @param gl
     */
    public void setTitleBarListenr(GradientLayout gl) {
        gl.setOnGradientStateChangeListenr(new OnGradientStateChangeListenr() {
            @Override
            public void onChange(float fraction, float criticalValue) {
                /**
                 * 当变化值超过临界值
                 */
                if (fraction >= criticalValue) {
                    btn_add.setBackgroundResource(R.mipmap.add_trans);
                } else {
                    btn_add.setBackgroundResource(R.mipmap.add_white);
                }
            }
        });
        gl.setOnRefeshChangeListener(new GradientLayout.OnRefeshChangeListener() {
            @Override
            public void onListRefesh() {
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_title.setVisibility(View.VISIBLE);
                        //执行动画
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.refesh_roate);
                        anim.setInterpolator(new LinearInterpolator());
                        iv_title.startAnimation(anim);
                    }
                });
            }

            @Override
            public void onListRefeshFinish() {
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_title.setVisibility(View.INVISIBLE);
                        iv_title.clearAnimation();
                    }
                });
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 3) {
            throw new IllegalArgumentException("only can 3 child in this view");
        }

        btn_back = (Button) getChildAt(0);
        rl_title = (RelativeLayout) getChildAt(1);
        iv_title = (ImageView) rl_title.getChildAt(0);
        tv_title = (TextView) rl_title.getChildAt(1);
        btn_add = (Button) getChildAt(2);
        btn_back.setOnClickListener(this);
        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                if (onBarClicklistener != null) {
                    if (isDisplay) {
                        dismissPopupWindow();
                    } else {
                        displayPopupWindow(v);
                    }
                    onBarClicklistener.onBarClick(R.id.btn_add);
                }
                break;
            case R.id.btn_back:
                if (onBarClicklistener != null) {
                    onBarClicklistener.onBarClick(R.id.btn_back);
                }
                break;
        }
    }

    public void displayPopupWindow(View v) {
        displayAnim();
        blurPopupWindow.displayPopupWindow(v);
    }

    public void dismissPopupWindow() {
        dismissAnim();
        blurPopupWindow.dismissPopupWindow();
    }

    /**
     * Add按钮逆时针转90度
     */
    private void displayAnim() {
        ObjectAnimator.ofFloat(btn_add, "rotation", 0.f, -90.f).setDuration(500).start();
    }

    /**
     * Add按钮瞬时间转90度
     */
    private void dismissAnim() {
        ObjectAnimator.ofFloat(btn_add, "rotation", 0.f, 90.f).setDuration(500).start();
    }

    public interface OnBarClicklistener {
        /**
         * TitleBar中控件点击监听
         * @param id
         */
        void onBarClick(int id);
    }

    /**
     * 设置TitleBar text
     *
     * @param msg
     */
    public void setTitleText(String msg) {
        tv_title.setText(msg);
    }
}
