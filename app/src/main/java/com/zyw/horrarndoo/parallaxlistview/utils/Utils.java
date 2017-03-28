package com.zyw.horrarndoo.parallaxlistview.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Horrarndoo on 2017/3/27.
 */

public class Utils {
    /** 获取状态栏高度
     * @param v
     * @return
     */
    public static int getStatusBarHeight(View v) {
        if (v == null) {
            return 0;
        }
        Rect frame = new Rect();
        v.getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 设置状态栏背景色
     * 4.4以下不处理
     * 4.4使用默认沉浸式状态栏
     *
     * @param color
     */
    public static void setBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            View decorView = win.getDecorView();
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//沉浸式状态栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//android5.0及以上才有透明效果
                win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//清除flag
                //让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | option);
                win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                win.setStatusBarColor(color);//设置状态栏背景色
            }
        }
    }
}
