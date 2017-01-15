package com.vetal.tennispartner.adaptersAndOthers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.vetal.tennispartner.R;

import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends ArrayAdapter {

    private Context context;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ArrayList<User> users ;
    private List<String> mCommentIds = new ArrayList<>();

    public UsersAdapter(Context context, ArrayList<User> users){
        super(context, R.layout.list_view,users);
        this.context = context;
        this.users = users;
    }

    private class ViewHolder{
        TextView fullName;
        TextView age;
        TextView level;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView =  LayoutInflater.from(context).inflate(R.layout.list_view,null);
            viewHolder.fullName = (TextView)convertView.findViewById(R.id.full_name_list_view);
            viewHolder.age = (TextView)convertView.findViewById(R.id.age_list_view);
            viewHolder.level = (TextView)convertView.findViewById(R.id.level_list_view);

            convertView.setTag(viewHolder);
        }

        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.fullName.setText(users.get(position).getFirstName() +" "+ users.get(position).getLastName());
        viewHolder.age.setText(users.get(position).getAge());
        viewHolder.level.setText(users.get(position).getLevel());

        return convertView;
    }
}
