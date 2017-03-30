package com.zyw.horrarndoo.parallaxlistview.utils;

/**
 * Created by Horrarndoo on 2017/3/27.
 */

public class ColorUtil {
    /**
     * 将ArgbEvaluator计算两个颜色中间渐变色的方法单独提取出来使用
     * ArgbEvaluator argbEvaluator = new ArgbEvaluator();
     * argbEvaluator.evaluate(float fraction, Object startValue,Object endValue)
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public static Object evaluateColor(float fraction, Object startValue,
                                       Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
                | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
                | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
                | (int) ((startB + (int) (fraction * (endB - startB))));
    }
}
