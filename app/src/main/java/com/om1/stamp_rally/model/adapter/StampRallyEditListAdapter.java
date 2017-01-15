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

import java.util.List;

import database.entities.StampRallys;
import database.entities.Stamps;

/**
 * Created by yaboo on 2017/01/15.
 */

public class StampRallyEditListAdapter extends ArrayAdapter<StampRallys> {
    private LayoutInflater layoutInflater;

    public StampRallyEditListAdapter(Context context, int resource, List<StampRallys> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StampRallys stampRally = getItem(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_row_create_stamprally, parent, false);
        }

//        byte[] pictureByte = stamp.getPicture();
//        Bitmap picture = BitmapFactory.decodeByteArray(pictureByte, 0, pictureByte.length);
//        ((ImageView)convertView.findViewById(R.id.stampThumbnail)).setImageBitmap(picture);
        ((TextView)convertView.findViewById(R.id.stampRallyTitle)).setText(stampRally.getStamprallyName());

        return convertView;
    }
}
