package com.arana.oruije_messaging.Adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arana.oruije_messaging.Models.Notifications;
import com.arana.oruije_messaging.R;
import com.arana.oruije_messaging.Session;
import com.arana.oruije_messaging.UsersModel;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends ArrayAdapter<Notifications> {
    protected LayoutInflater inflater;
    protected int layout;

    Session session;

    private ArrayList<Notifications> not_list;
    private ArrayList<Notifications> arrayList;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        ImageView user_image;
        TextView not_message;
        TextView add_btn;
    }

    public NotificationAdapter(ArrayList<Notifications> data, Context context) {
        super(context, R.layout.start_chat_layout, data);
        this.not_list = data;
        this.mContext=context;
        this.arrayList = new ArrayList<Notifications>();
        this.arrayList.addAll(not_list);
    }

    private int lastPosition = -1;

    @Override
    public int getCount() {
        return not_list.size() ;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Notifications not_model = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notification_layout, parent, false);
            
            
            viewHolder.add_btn = convertView.findViewById(R.id.add_btn);
            viewHolder.not_message = convertView.findViewById(R.id.not_message);
            viewHolder.user_image = convertView.findViewById(R.id.user_image);

            session = new Session(getContext());

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //      ************  NOTIFICATION GUIDE  **********
        //1 you have just been added
        //2 board notice
        //3 comment notice
        //4 like notice
        //5 new message notice
        //6 accept order notice
        //7 accept to deliver
        //8 en-route to collect notice
        //9 make order notice
        //10 reject order notice
        //11 en-route to drop-off destination

        //an array can be created for a shorter way but i feel this has more control
//        String user_name = not_model.getUsername();
//        SpannableStringBuilder str = new SpannableStringBuilder(user_name);
//        str.setSpan(new StyleSpan(Typeface.BOLD),0,user_name.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
        if(not_model.getNot_type().equals("1")){
            viewHolder.not_message.setText(not_model.getUsername()+" just added you");
        }else if(not_model.getNot_type().equals("2")){
            viewHolder.not_message.setText(not_model.getUsername()+" posted to board notice");
        }else if(not_model.getNot_type().equals("3")){
            viewHolder.not_message.setText(not_model.getUsername()+" commented on your post");
        }else if(not_model.getNot_type().equals("4")){
            viewHolder.not_message.setText(not_model.getUsername()+" liked your post");
        }else if(not_model.getNot_type().equals("5")){
            viewHolder.not_message.setText(not_model.getUsername()+" sent you a message");
        }else if(not_model.getNot_type().equals("6")){
            viewHolder.not_message.setText(not_model.getUsername()+" accepted your order");
        }else if(not_model.getNot_type().equals("7")){
            viewHolder.not_message.setText(not_model.getUsername()+" accepted to deliver product");
        }else if(not_model.getNot_type().equals("8")){
            viewHolder.not_message.setText(not_model.getUsername()+" en-route to collect package");
        }else if(not_model.getNot_type().equals("9")){
            viewHolder.not_message.setText(not_model.getUsername()+" ordered a product");
        }else if(not_model.getNot_type().equals("10")){
            viewHolder.not_message.setText(not_model.getUsername()+" declined a product");
        }else if(not_model.getNot_type().equals("11")){
            viewHolder.not_message.setText(not_model.getUsername()+" en-route to drop-off destination");
        }

        //add btn is only visible when the user is added
        if(not_model.getNot_type().equals("1")){
            viewHolder.add_btn.setVisibility(View.VISIBLE);
        }else{
            viewHolder.add_btn.setVisibility(View.GONE);
        }
        //sets the user_image
        viewHolder.user_image.setImageBitmap(not_model.getUser_image());
        

        // Return the completed view to render on screen
        return convertView;
    }
}
