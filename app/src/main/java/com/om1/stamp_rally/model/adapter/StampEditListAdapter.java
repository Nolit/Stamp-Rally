package com.om1.stamp_rally.model.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.om1.stamp_rally.R;

import java.util.List;


public class StampEditListAdapter extends ArrayAdapter<StampEditListAdapter.StampData> {
    private LayoutInflater layoutInflater;

    public StampEditListAdapter(Context context, int resource, List<StampData> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StampData stampData = getItem(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_row_stamp, parent, false);
        }

        ((TextView)convertView.findViewById(R.id.stampTitle)).setText(stampData.getTitle());
//        ((ImageView)convertView.findViewById(R.id.stampThumbnail)).setImageBitmap(stampData.getThumbnail());

        return convertView;
    }

    public StampData createData(){
        return new StampData();
    }

    public class StampData{
        private String title;
        private Bitmap thumbnail;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public Bitmap getThumbnail() {
            return thumbnail;
        }
        public void setThumbnail(Bitmap thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
}
