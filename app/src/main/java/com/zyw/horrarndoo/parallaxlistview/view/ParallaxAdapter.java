package com.zyw.horrarndoo.parallaxlistview.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyw.horrarndoo.parallaxlistview.R;

import java.util.List;

/**
 * Created by Horrarndoo on 2017/3/24.
 */

public class ParallaxAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;

    public ParallaxAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
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
        holder.tv_content.setText((String)list.get(position));
        return convertView;
    }

    private static class ViewHolder{
        ImageView imageView;
        TextView tv_content;
        private ViewHolder(View convertView){
            imageView = (ImageView) convertView.findViewById(R.id.iv_head);
            tv_content = (TextView) convertView.findViewById(R.id.tv_content);
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
}
