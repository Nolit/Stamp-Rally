package com.om1.stamp_rally.model.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.bean.StampRallyBean;

import java.util.ArrayList;

import static com.om1.stamp_rally.R.id.StampRally;


public class ResultSearchListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<StampRallyBean> stampRallyList;

    public ResultSearchListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setStampRallyList(ArrayList<StampRallyBean> stampRallyList) {
        this.stampRallyList = stampRallyList;
    }

    @Override
    public int getCount() {
        return stampRallyList.size();
    }

    @Override
    public Object getItem(int position) {
        return stampRallyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stampRallyList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_row_stamprally,parent,false);

        ((ImageView)convertView.findViewById(R.id.stampThumbnail))
                .setImageBitmap(BitmapFactory.decodeByteArray(
                        stampRallyList.get(position).getPictPath(),
                        0,
                        stampRallyList.get(position).getPictPath().length));
        ((TextView)convertView.findViewById(R.id.stampRallyTitle)).setText(stampRallyList.get(position).getStampRallyTitle());
        ((TextView)convertView.findViewById(R.id.CreatorName)).setText(stampRallyList.get(position).getCreatorName());


        return convertView;
    }
}
