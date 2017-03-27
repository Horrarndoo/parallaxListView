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
import android.widget.Toast;

import com.zyw.horrarndoo.parallaxlistview.view.ParallaxAdapter;
import com.zyw.horrarndoo.parallaxlistview.view.ParallaxListView;
import com.zyw.horrarndoo.parallaxlistview.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import static com.zyw.horrarndoo.parallaxlistview.view.TitleBar.*;

public class MainActivity extends AppCompatActivity implements OnBarClicklistener {
    private ParallaxListView lvParallax;
    private TitleBar tb_title;
    private List<String> list = new ArrayList<>();

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
        tb_title = (TitleBar) findViewById(R.id.tb_title);
        lvParallax = (ParallaxListView) findViewById(R.id.lv_parallax);
        lvParallax.setOverScrollMode(ListView.OVER_SCROLL_NEVER);//滑到底部顶部不显示蓝色阴影
        View headerView = View.inflate(this, R.layout.list_item_head, null);//添加header
        ImageView ivHead = (ImageView) headerView.findViewById(R.id.iv_head);
        lvParallax.initParallaxImageParams(ivHead);
        lvParallax.addHeaderView(headerView);
        lvParallax.setAdapter(new ParallaxAdapter(this, list));
        tb_title.setOnBarChildClicklistener(this);
    }

    /**
     * 初始化状态栏状态
     * 设置Activity状态栏透明效果
     * 隐藏ActionBar
     */
    private void initState() {
        //将状态栏设置成透明色
        setBarColor(Color.TRANSPARENT);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * 设置状态栏背景色
     * @param color
     */
    private void setBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//android5.0及以上才有透明效果
            View decorView = getWindow().getDecorView();
            //让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void onBarClick(int id) {
        switch (id){
            case R.id.btn_add:
                Toast.makeText(this, "btn_add is clicked.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_back:
                Toast.makeText(this, "btn_back is clicked.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
