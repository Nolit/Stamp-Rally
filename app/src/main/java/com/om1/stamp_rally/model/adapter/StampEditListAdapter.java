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
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import java.util.List;
import java.util.Map;

import database.entities.StampRallys;
import database.entities.Stamps;


public class StampEditListAdapter extends ArrayAdapter<Stamps> {
    private LayoutInflater layoutInflater;

    public StampEditListAdapter(Context context, int resource, List<Stamps> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Stamps stamp = getItem(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_row_stamp, parent, false);
        }

        byte[] pictureByte = stamp.getPicture();
        Bitmap picture = BitmapFactory.decodeByteArray(pictureByte, 0, pictureByte.length);
        ((ImageView)convertView.findViewById(R.id.stampThumbnail)).setImageBitmap(picture);
        ((TextView)convertView.findViewById(R.id.stampTitle)).setText(stamp.getStampName());
        StampRallys stampRallys = stamp.getStampRallysList().get(0);
        ((TextView)convertView.findViewById(R.id.stampRallyTitle)).setText(stampRallys.getStamprallyName());

        return convertView;
    }
}
