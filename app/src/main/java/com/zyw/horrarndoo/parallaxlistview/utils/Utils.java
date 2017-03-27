package com.zyw.horrarndoo.parallaxlistview.utils;

import android.graphics.Rect;
import android.view.View;

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
}
