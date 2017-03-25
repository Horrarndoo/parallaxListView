package com.zyw.horrarndoo.parallaxlistview;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.zyw.horrarndoo.parallaxlistview.view.ParallaxAdapter;
import com.zyw.horrarndoo.parallaxlistview.view.ParallaxListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ParallaxListView lvParallax;
    private List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initState();
        init();
    }

    private void init() {
        for (int i = 0; i < 30; i++){
            list.add("content - " + i);
        }
        lvParallax = (ParallaxListView) findViewById(R.id.lv_parallax);
        lvParallax.setOverScrollMode(ListView.OVER_SCROLL_NEVER);//滑到底部顶部不显示蓝色阴影
        View headerView = View.inflate(this, R.layout.list_item_head, null);//添加header
        ImageView ivHead = (ImageView) headerView.findViewById(R.id.iv_head);
        lvParallax.initParallaxImageParams(ivHead);
        lvParallax.addHeaderView(headerView);
        lvParallax.setAdapter(new ParallaxAdapter(this, list));
    }

    /**
     * 初始化应用状态栏
     * 设置Activity状态栏透明效果
     */
    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//android5.0及以上才有透明效果
            View decorView = getWindow().getDecorView();
            //让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //将状态栏设置成透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
    }
}
