package com.arana.oruije_messaging;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eunoia.oruije.Database;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    protected LayoutInflater inflater;
    protected int layout;

    public static final int BY_ME = 1;
    public static final int NOT_BY_ME = 0;
    public static final int IMAGE_BY_ME = 11;
    public static final int IMAGE_NOT_BY_ME = 10;

    Session session;
    Context mContext;
    Chat mChat;

    public ArrayList<ChatListModel> selected_usersList,chat_list;

    com.eunoia.oruije.Database db;

    public ChatListAdapter(ArrayList<ChatListModel> data, ArrayList<ChatListModel> selectedList,Context context) {
        this.chat_list = data;
        this.selected_usersList = selectedList;
        this.mContext=context;
        mChat = (Chat)context;

        db = new Database(mContext);
    }

    @NonNull
    @Override
    public ChatListAdapter.MyViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BY_ME){  //posts by me
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
            return new MyViewHolder(itemView,mChat);
        }else if(viewType == NOT_BY_ME){ //posts by the other person
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout_recipient, parent, false);
            return new MyViewHolder(itemView,mChat);
        }else if (viewType == IMAGE_BY_ME){ //image posts by me
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_chat_layout, parent, false);
            return new MyViewHolder(itemView,mChat);
        }else{  //image posts by the other person
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_chat_layout_recipient, parent, false);
            return new MyViewHolder(itemView,mChat);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {
        final ChatListModel mChats = chat_list.get(position);

        if (getItemViewType(position) == BY_ME || getItemViewType(position) ==  NOT_BY_ME) {
            viewHolder.content.setText(mChats.getMessage());
        }else{
            if (mChats.getImage_message() != null) { //check if image is not null

                Glide.with(mContext)
                        .load(mChats.getImage_message())
                        .into(viewHolder.image_message);
            }
        }

        String time = mChats.getTime_posted().substring(0,4);
        String pm = mChats.getTime_posted().substring(7);
        viewHolder.time_posted.setText(time+""+pm);

        if(selected_usersList.contains(chat_list.get(position))) {
            viewHolder.row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.selected_item));
            //if the same type of data is selected -i.e. if text or images are selected all through
        }
        else
            viewHolder.row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));


        viewHolder.row.setTag(position);
        viewHolder.row.setId(position);
//
        //header stuff
//        if (position>0) {
//            if (!chat_list.get(position).getDate_posted().equals(chat_list.get(position - 1).getDate_posted())) {
//                viewHolder.section_header.setText(mChats.getDate_posted()); //sets the section date
//                viewHolder.date_header.setVisibility(View.VISIBLE);// makes the container visible
//            }
//        }
    }


    @Override
    public int getItemCount() {
        return chat_list.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatListModel chatListModel = chat_list.get(position);
        if (chatListModel.isHas_image().equalsIgnoreCase("true")) {
            if (chatListModel.getAuthor_id().equalsIgnoreCase(chatListModel.getUser_id())) {
                return IMAGE_BY_ME;
            } else {
                return IMAGE_NOT_BY_ME;
            }
        } else if (chatListModel.getAuthor_id().equalsIgnoreCase(chatListModel.getUser_id())) {
            return BY_ME;
        } else {
            return NOT_BY_ME;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView content, section_header, time_posted;
        ImageView image_message;
        RelativeLayout date_header;
        LinearLayout row;
        View v;
        CheckBox row_selector;
        Chat chat;

        public MyViewHolder(final View itemView, Chat chat) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            section_header = itemView.findViewById(R.id.section_header);
            time_posted = itemView.findViewById(R.id.time_posted);
            image_message = itemView.findViewById(R.id.image_message);
            date_header = itemView.findViewById(R.id.date_header);
            row = itemView.findViewById(R.id.row);
        }
    }
}
