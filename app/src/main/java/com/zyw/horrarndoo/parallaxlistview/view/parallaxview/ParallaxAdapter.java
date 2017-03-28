package com.zyw.horrarndoo.parallaxlistview.view.parallaxview;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zyw.horrarndoo.parallaxlistview.R;
import com.zyw.horrarndoo.parallaxlistview.view.popupwindow.BlurPopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Horrarndoo on 2017/3/24.
 */

public class ParallaxAdapter extends BaseAdapter implements BlurPopupWindow.OnPopupStateListener{
    private List<String> list;
    private Context context;
    private boolean isDisplay;
    private BlurPopupWindow blurPopupWindow;

    public ParallaxAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        initPopupWindow((Activity) context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.list_item, null);
        }
        ViewHolder holder = ViewHolder.getHolder(convertView);
        holder.tv_user.setText((String)list.get(position));
        holder.btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDisplay) {
                    blurPopupWindow.dismissPopupWindow();
                } else {
                    blurPopupWindow.displayPopupWindow(v);
                }
            }
        });
        return convertView;
    }

    @Override
    public void onDismiss(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    @Override
    public void onDisplay(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    private static class ViewHolder{
        TextView tv_user;
        Button btn_down;

        private ViewHolder(View convertView){
            tv_user = (TextView) convertView.findViewById(R.id.tv_user);
            btn_down = (Button) convertView.findViewById(R.id.btn_down);
        }

        public static ViewHolder getHolder(View convertView){
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if(holder == null){
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
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
        blurPopupWindow = new BlurPopupWindow(context, strList, clickList);
        blurPopupWindow.setOnPopupStateListener(this);
    }
}
