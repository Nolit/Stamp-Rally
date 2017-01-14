package com.om1.stamp_rally.model.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.bean.StampBean;

import java.util.ArrayList;
import java.util.List;

import database.entities.StampRallys;
import database.entities.Stamps;


public class MyStampListAdapter extends ArrayAdapter<StampBean> {
    private LayoutInflater layoutInflater;

    public MyStampListAdapter(Context context, int resource, ArrayList<StampBean> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StampBean stamp = getItem(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_row_stamp, parent, false);
        }

        byte[] pictureByte = stamp.getPictPath();
        Bitmap picture = BitmapFactory.decodeByteArray(pictureByte, 0, pictureByte.length);
        ((ImageView)convertView.findViewById(R.id.stampThumbnail)).setImageBitmap(picture);
        ((TextView)convertView.findViewById(R.id.stampTitle)).setText(stamp.getStampTitle());
        ((TextView)convertView.findViewById(R.id.stampRallyTitle)).setText(stamp.getStampRallyName());

        return convertView;
    }
}
