package com.om1.stamp_rally.model.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.controller.MainActivity;
import com.om1.stamp_rally.controller.StampPreviewActivity;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;
import com.om1.stamp_rally.utility.dbadapter.StructureStampDbAdapter;

import java.util.ArrayList;
import java.util.List;

import database.entities.StampRallys;
import database.entities.Stamps;

/**
 * Created by yaboo on 2017/01/15.
 */

public class StampRallyEditListAdapter extends ArrayAdapter<StampRallys> {
    private LayoutInflater layoutInflater;
    private List<StampRallys> stampRallyList = new ArrayList<>();

    public StampRallyEditListAdapter(Context context, int resource, List<StampRallys> objects) {
        super(context, resource, objects);
        stampRallyList = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final StampRallys stampRally = getItem(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_row_create_stamprally, parent, false);
        }

        ((TextView)convertView.findViewById(R.id.stampRallyTitle)).setText(stampRally.getStamprallyName());
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(position, stampRally.getStamprallyId());
            }
        });

        return convertView;
    }

    private void showConfirmDialog(final int position, final int stampRallyId){
        new AlertDialog.Builder(getContext())
                .setTitle("本当に削除しますか？")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new StampRallyDbAdapter(getContext()).deleteById(stampRallyId);
                        new StructureStampDbAdapter(getContext()).deleteBySrampRallyId(stampRallyId);
                        stampRallyList.remove(position);
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
