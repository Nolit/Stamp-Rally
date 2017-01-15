package com.om1.stamp_rally.model.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.bean.StampBean;

import java.util.ArrayList;

public class MyStampBookListAdapter extends ArrayAdapter<StampBean> {
    private LayoutInflater layoutInflater;

    public MyStampBookListAdapter(Context context, int resource, ArrayList<StampBean> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StampBean stamp = getItem(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_row_mystamp_book, parent, false);
        }

        byte[] pictureByte = stamp.getPictPath();
        Log.d("スタンプラリー", ""+pictureByte);
        Bitmap picture = BitmapFactory.decodeByteArray(pictureByte, 0, pictureByte.length);
        ((ImageView)convertView.findViewById(R.id.stampBookThumbnail)).setImageBitmap(picture);
        ((TextView)convertView.findViewById(R.id.stampTitle)).setText(stamp.getStampTitle());
        ((TextView)convertView.findViewById(R.id.getStampDate)).setText(stamp.getStampDate());

        return convertView;
    }
}
