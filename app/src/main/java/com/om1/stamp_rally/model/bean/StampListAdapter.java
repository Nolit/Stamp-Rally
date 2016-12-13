package com.om1.stamp_rally.model.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.om1.stamp_rally.R;

import java.util.ArrayList;


public class StampListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<StampBean> stampList;

    public StampListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setStampList(ArrayList<StampBean> stampList) {
        this.stampList = stampList;
    }

    @Override
    public int getCount() {
        return stampList.size();
    }

    @Override
    public Object getItem(int position) {
        return stampList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stampList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_row_stamp,parent,false);

        ((TextView)convertView.findViewById(R.id.stampTitle)).setText(stampList.get(position).getStampTitle());

        return convertView;
    }
}
