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
import com.om1.stamp_rally.model.bean.CreateStampRallyBean;

import java.util.ArrayList;

public class CreateStampRallyListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<CreateStampRallyBean> createStampRallyList;

    public CreateStampRallyListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCreateStampRallyList(ArrayList<CreateStampRallyBean> createStampRallyList) {
        this.createStampRallyList = createStampRallyList;
    }

    @Override
    public int getCount() {
        return createStampRallyList.size();
    }

    @Override
    public Object getItem(int position) {
        return createStampRallyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return createStampRallyList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_row_create_stamprally,parent,false);

        ((ImageView)convertView.findViewById(R.id.stampThumbnail))
                .setImageBitmap(BitmapFactory.decodeByteArray(
                        createStampRallyList.get(position).getPictPath(),
                        0,
                        createStampRallyList.get(position).getPictPath().length));
        ((TextView)convertView.findViewById(R.id.stampRallyTitle)).setText(createStampRallyList.get(position).getStampRallyTitle());

        return convertView;
    }
}
