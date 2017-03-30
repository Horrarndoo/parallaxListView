package com.zyw.horrarndoo.parallaxlistview.view.popupwindow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zyw.horrarndoo.parallaxlistview.R;

import java.util.List;

/**
 * Created by Horrarndoo on 2017/3/29.
 */

public class PopupListAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;

    public PopupListAdapter(Context context, List<String> list) {
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
            convertView = View.inflate(context, R.layout.popup_list_item, null);
        }
        ViewHolder holder = ViewHolder.getHolder(convertView);
        holder.tv_popup_item.setText(list.get(position));
        return convertView;
    }

    public static class ViewHolder{
        TextView tv_popup_item;
        private ViewHolder(View convertView){
            tv_popup_item = (TextView) convertView.findViewById(R.id.tv_popup_item);
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
