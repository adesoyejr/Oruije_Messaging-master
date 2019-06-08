package com.arana.oruije_messaging;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends ArrayAdapter<UsersModel> {
    protected LayoutInflater inflater;
    protected int layout;

    Session session;

    private ArrayList<UsersModel> modelList;
    private ArrayList<UsersModel> arrayList;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView pos;
        TextView category;
        CircleImageView user_image;
        LinearLayout chat_row;
        ImageButton is_selected;
    }

    public UsersAdapter(ArrayList<UsersModel> data, Context context) {
        super(context, R.layout.start_chat_layout, data);
        this.modelList = data;
        this.mContext=context;
        this.arrayList = new ArrayList<UsersModel>();
        this.arrayList.addAll(modelList);
    }

    private int lastPosition = -1;

    @Override
    public int getCount() {
        return modelList.size() ;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UsersModel usersModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.start_chat_layout, parent, false);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.pos = convertView.findViewById(R.id.position);
            viewHolder.category = convertView.findViewById(R.id.category);
            viewHolder.user_image = convertView.findViewById(R.id.user_image);

            viewHolder.chat_row = convertView.findViewById(R.id.row);
            viewHolder.is_selected = convertView.findViewById(R.id.is_selected);
            session = new Session(getContext());

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.name.setText(usersModel.getName());
        viewHolder.pos.setText(usersModel.getPosition());
        if (usersModel.getCategory().equalsIgnoreCase("circle")){
            viewHolder.category.setTextColor(getContext().getResources().getColor(R.color.OruijeBlue));
            viewHolder.category.setBackground(getContext().getResources().getDrawable(R.drawable.pressed_circle));
            viewHolder.category.setPadding(10,2,10,2);
            viewHolder.category.setText(usersModel.getCategory());
        }else if (usersModel.getCategory().equalsIgnoreCase("Dinky")){
            viewHolder.category.setTextColor(getContext().getResources().getColor(R.color.OruijeGreen));
            viewHolder.category.setBackground(getContext().getResources().getDrawable(R.drawable.pressed_dinky));
            viewHolder.category.setPadding(10,2,10,2);
            viewHolder.category.setText(usersModel.getCategory());
        }else if (usersModel.getCategory().equalsIgnoreCase("Random") || usersModel.getCategory().equalsIgnoreCase("Page")){
            viewHolder.category.setTextColor(getContext().getResources().getColor(R.color.white));
            viewHolder.category.setBackground(getContext().getResources().getDrawable(R.drawable.border));
            viewHolder.category.setPadding(10,2,10,2);
            viewHolder.category.setText(usersModel.getCategory());
        }

        viewHolder.user_image.setImageBitmap(usersModel.getUser_image());

        viewHolder.is_selected.setTag(position);
//        viewHolder.is_selected.setVisibility(View.VISIBLE);

        viewHolder.chat_row.setTag(position);


        // Return the completed view to render on screen
        return convertView;
    }

//    public void filterList(ArrayList<UsersModel> filteredList){
//        modelList = filteredList;
//        notifyDataSetChanged();
//    }
    
    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        modelList.clear();
        if (charText.length() == 0){
            modelList.addAll(arrayList);
        }else{
            for (UsersModel model : arrayList){
                if (model.getName().toLowerCase(Locale.getDefault()).contains(charText)){
                    modelList.add(model);
                }else if (model.getPosition().toLowerCase(Locale.getDefault()).contains(charText)){
                    modelList.add(model);
                }else if (model.getCategory().toLowerCase(Locale.getDefault()).contains(charText)){
                    modelList.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }
}
