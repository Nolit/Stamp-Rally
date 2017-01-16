package com.om1.stamp_rally.model.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.bean.StampBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureStampListAdapter extends ArrayAdapter<StampBean> {
    private LayoutInflater layoutInflater;
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private List<StampBean> stampBeanList;
    private List<Integer> selectedStampIdList = new ArrayList<>();
    private Map<Integer, Boolean> isSetFlags = new HashMap<>();

    public StructureStampListAdapter(Context context, int resource, ArrayList<StampBean> objects) {
        super(context, resource, objects);
        stampBeanList = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0; i < stampBeanList.size(); i++){
            isSetFlags.put(i, false);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StampBean stamp = stampBeanList.get(position);
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.list_row_structure_stamp, parent, false);
        }
        byte[] pictureByte = stamp.getPictPath();
        Bitmap picture = BitmapFactory.decodeByteArray(pictureByte, 0, pictureByte.length);
        ((ImageView)convertView.findViewById(R.id.structureStampThumbnail)).setImageBitmap(picture);
        ((TextView)convertView.findViewById(R.id.structureStampTitle)).setText(stamp.getStampTitle());

        final int p = position;
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.structureStampCheckBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSetFlags.put(p, isChecked);
            }
        });
        checkBox.setChecked(isSetFlags.get(position));

        return convertView;
    }

    public void setSelectedStampIds(List<Integer> selectedStampIdList){
        for(int id : selectedStampIdList){
            for(int i = 0; i < stampBeanList.size(); i++){
                if(id == stampBeanList.get(i).getStampId()){
                    isSetFlags.put(i, true);
                    break;
                }
            }
        }
    }

    public List<Integer> getSelectedStampId(){
        List<Integer> idList = new ArrayList<>();
        for(int i = 0; i < isSetFlags.size(); i++){
            if(isSetFlags.get(i)){
                idList.add(stampBeanList.get(i).getStampId());
            }
        }

        return idList;
    }
}
