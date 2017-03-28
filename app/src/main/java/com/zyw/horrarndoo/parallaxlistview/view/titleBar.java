package com.zyw.horrarndoo.parallaxlistview.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.zyw.horrarndoo.parallaxlistview.R;

import java.util.ArrayList;

import static com.zyw.horrarndoo.parallaxlistview.view.GradientLayout.OnGradientStateChangeListenr;

/**
 * Created by Horrarndoo on 2017/3/27.
 */

public class TitleBar extends LinearLayout implements OnClickListener,
        OnGradientStateChangeListenr {
    private Button btn_back;
    private Button btn_add;
    private TextView tv_title;
    private boolean isShow;
    private HintPopupWindow hintPopupWindow;
    private Context context;

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
        initPopupWindow((Activity) context);
    }

    private void initPopupWindow(final Activity context){
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
        hintPopupWindow = new HintPopupWindow(context, strList, clickList);
    }

    /**
     * 设置Gradient临界值监听
     *
     * @param gl
     */
    public void setOnGradientStateChangeListenr(GradientLayout gl) {
        gl.setOnGradientStateChangeListenr(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 3) {
            throw new IllegalArgumentException("only can 3 child in this view");
        }

        btn_back = (Button) getChildAt(0);
        tv_title = (TextView) getChildAt(1);
        btn_add = (Button) getChildAt(2);
        btn_back.setOnClickListener(this);
        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                if (onBarClicklistener != null) {
                    if (isShow) {
                        dismissPopupWindow();
                        hintPopupWindow.dismissPopupWindow();
                    } else {
                        showPopupWindow();
                        hintPopupWindow.showPopupWindow(v);
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

    public void showPopupWindow() {
        //Add按钮逆时针转90度
        isShow = true;
        ObjectAnimator.ofFloat(btn_add, "rotation", 0.f, -90.f).setDuration(500).start();
    }

    public void dismissPopupWindow() {
        //Add按钮瞬时间转90度
        isShow = false;
        ObjectAnimator.ofFloat(btn_add, "rotation", 0.f, 90.f).setDuration(500).start();
    }

    private OnBarClicklistener onBarClicklistener;

    public void setOnBarChildClicklistener(OnBarClicklistener onBarClicklistener) {
        this.onBarClicklistener = onBarClicklistener;
    }

    public interface OnBarClicklistener {
        public void onBarClick(int id);
    }

    /**
     * 设置TitleBar text
     *
     * @param msg
     */
    public void setTitleText(String msg) {
        tv_title.setText(msg);
    }

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
}
