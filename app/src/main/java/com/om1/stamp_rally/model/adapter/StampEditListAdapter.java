package com.om1.stamp_rally.model.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;

import java.util.List;
import java.util.Map;

import database.entities.StampRallys;
import database.entities.Stamps;


public class StampEditListAdapter extends ArrayAdapter<Stamps> {
    private LayoutInflater layoutInflater;
    private List<Stamps> stampList;

    public StampEditListAdapter(Context context, int resource, List<Stamps> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stampList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Stamps stamp = getItem(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_row_stamp, parent, false);
        }

        byte[] pictureByte = stamp.getPicture();
        Bitmap picture = BitmapFactory.decodeByteArray(pictureByte, 0, pictureByte.length);
        ((ImageView)convertView.findViewById(R.id.stampThumbnail)).setImageBitmap(picture);
        ((TextView)convertView.findViewById(R.id.stampTitle)).setText(stamp.getStampName());
        StampRallys stampRallys = stamp.getStampRallysList().get(0);
        ((TextView)convertView.findViewById(R.id.stampRallyTitle)).setText(stampRallys.getStamprallyName());
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position, stamp.getStampId());
            }
        });

        return convertView;
    }

    private void showDeleteDialog(final int position, final int stampId){
        new AlertDialog.Builder(getContext())
                .setTitle("本当に削除しますか？")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new StampDbAdapter(getContext()).deleteByStampId(stampId);
                        stampList.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .create().show();
    }
}
