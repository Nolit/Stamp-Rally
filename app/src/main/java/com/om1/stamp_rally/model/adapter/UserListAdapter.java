package com.om1.stamp_rally.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.bean.StampRallyBean;
import com.om1.stamp_rally.model.bean.UserBean;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<UserBean> userList;

    public UserListAdapter(Context context, int i, ArrayList<UserBean> userList) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setUserList(ArrayList<UserBean> userList) {
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_row_user,parent,false);

        ((ImageView)convertView.findViewById(R.id.profileThumbnail)).setImageBitmap(userList.get(position).getPictureBitmap());
        ((TextView)convertView.findViewById(R.id.userName)).setText(userList.get(position).getUserName());
        ((TextView)convertView.findViewById(R.id.searchId)).setText(userList.get(position).getSearchId());

        return convertView;
    }
}
