package com.arana.oruije_messaging;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.arana.oruije_messaging.Utils.AlertDialogHelper;
import com.arana.oruije_messaging.Utils.RecyclerItemClickListener;
import com.eunoia.oruije.Database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener{

    Session session;
    com.eunoia.oruije.Database db;
    ImageView gallery,go_back;
    EditText message;
    ImageButton send_message;
    
    TextView user_name,user_position;
    RecyclerView chats;
    ArrayList<ChatListModel> chatListModels;
    private static ChatListAdapter chatListAdapter;
    String get_username,get_user_position,get_user_category,author_id,get_friend_id;
    Bitmap bt;
    CircleImageView user_image_recipient;
    ImageView pick_from_camera_or_gallery;
    public static final int REQUEST_IMAGE_CAPTURE = 101;
    public static final int REQUEST_IMAGE_FROM_GALLERY = 201;
    private Bitmap imageBitmap;
    public Boolean IS_NEW =true;

    private Uri mCapturedImageURI;

    String pictureFilePath;
    RelativeLayout single, multiple,action_mode_container;
    RecyclerView multiple_listview;
    LinearLayout initials;
    
    Cursor chat_lists;
    ArrayList<String> array;
    String intent_type;
    Uri imageUri;
    LinearLayout chat_toolbar;
    Toolbar toolbar;
    TextView choose_image_text;
    boolean fromChooseImage = false;
    boolean is_group_image_message = false;
    ActionMode acMd;

    public boolean is_in_action_mode = false;
    ArrayList<ChatListModel> selected_items;
    TextView selected_item_count;
    int counter = 0;
    ImageView exit_action_mode,delete,copy;

    LinearLayout normal_toolbar;

    int[] icons = {R.mipmap.camera,R.mipmap.photo_library};
    String[] select= {"Camera", "Photo Gallery"};

//    copy and paste variables
    boolean isMultiSelect = false;
    boolean is_preview_image_open = false;
    AlertDialogHelper alertDialogHelper;
    ArrayList<ChatListModel> multiselect_list = new ArrayList<>();
    ArrayList<String> multiselect_list_id = new ArrayList<>();
    ArrayList<String> clipboard_text = new ArrayList<>();
    ArrayList<String> images_selected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(this);
        db = new Database(this);

        alertDialogHelper =new AlertDialogHelper(this);

        session.post_message(true);

        selected_items = new ArrayList<>();
        get_friend_id = getIntent().getStringExtra("user_id"); //from previous intent

        intent_type = getIntent().getStringExtra("intent_type"); //from previous intent

        user_name = findViewById(R.id.user_name);
        user_position = findViewById(R.id.user_position);
        user_image_recipient = findViewById(R.id.user_image_recipient);
        go_back = findViewById(R.id.go_back);
        pick_from_camera_or_gallery = findViewById(R.id.pick_from_camera_or_gallery);
        chat_toolbar = findViewById(R.id.chat_toolbar);
        chat_toolbar.setVisibility(View.VISIBLE);

        single = findViewById(R.id.single);
        multiple = findViewById(R.id.multiple);
        multiple_listview = findViewById(R.id.multi_post_listview);
        initials = findViewById(R.id.initials);
        choose_image_text = findViewById(R.id.choose_image_text);

        //contextual menu simulator
        normal_toolbar = findViewById(R.id.normal_toolbar);
        action_mode_container = findViewById(R.id.action_mode_container);
        selected_item_count = findViewById(R.id.selected_item_count);
        exit_action_mode = findViewById(R.id.exit_action_mode);
        delete = findViewById(R.id.delete);
        copy = findViewById(R.id.copy);

        chats = findViewById(R.id.chats);
        chatListModels = new ArrayList<>();

        chats.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chats.setLayoutManager(linearLayoutManager);


        multiple_listview.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        multiple_listview.setLayoutManager(lm);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chats.setLayoutManager(mLayoutManager);
        chats.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        multiple_listview.setLayoutManager(mLayoutManager2);
        multiple_listview.setItemAnimator(new DefaultItemAnimator());

        chats.addOnItemTouchListener(new RecyclerItemClickListener(this, chats, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ChatListModel clm = chatListModels.get(position);
                if (isMultiSelect) {
                    multi_select(position);
                }else{
                    if (TextUtils.isEmpty(clm.getMessage())){ //if image is clicked
                        is_preview_image_open = true;
                        previewImage(clm.getImage_message());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.black))); //change the toolbar's background
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<>();
                    multiselect_list_id = new ArrayList<>();
                    clipboard_text = new ArrayList<>();
                    images_selected = new ArrayList<>();
                    isMultiSelect = true;

                    //initiates the context menu and hides the initial menu
                    action_mode_container.setVisibility(View.VISIBLE);
                    normal_toolbar.setVisibility(View.GONE);
                }
                multi_select(position);
            }
        }));

        multiple_listview.addOnItemTouchListener(new RecyclerItemClickListener(this, multiple_listview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ChatListModel clm = chatListModels.get(position);
                if (isMultiSelect) {
                    multi_select(position);
                }else{
                    if (TextUtils.isEmpty(clm.getMessage())){ //if image is clicked
                        is_preview_image_open = true;
                        previewImage(clm.getImage_message());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.black))); //change the toolbar's background
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<>();
                    multiselect_list_id = new ArrayList<>();
                    clipboard_text = new ArrayList<>();
                    images_selected = new ArrayList<>();
                    isMultiSelect = true;

                    //initiates the context menu and hides the initial menu
                    action_mode_container.setVisibility(View.VISIBLE);
                    normal_toolbar.setVisibility(View.GONE);
                }
                multi_select(position);
            }
        }));

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ct = convertArrayToString(clipboard_text);
                
                //now copy to clipboard
                copyToClipboard(Chat.this,ct);
                finish_deletion();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (multiselect_list.size() == 1){
                    alertDialogHelper.showAlertDialog("", "Delete 1 Chat ", "DELETE", "CANCEL", 1, false);
                }else {
                    alertDialogHelper.showAlertDialog("", "Delete "+ multiselect_list.size()+" Chats ", "DELETE", "CANCEL", 1, false);
                }
            }
        });

        exit_action_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish_deletion();
            }
        });
        message = findViewById(R.id.message);
        send_message = findViewById(R.id.send_message);


        if (intent_type.equalsIgnoreCase("single")) {

            single.setVisibility(View.VISIBLE);
            multiple.setVisibility(View.INVISIBLE);
            user_image_recipient.setVisibility(View.VISIBLE);

            pick_from_camera_or_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pick_image();
                }
            });

            Cursor get_a_user = db.get_a_user(get_friend_id);
            if (get_a_user.getCount() > 0) {
                get_a_user.moveToFirst();
                get_username = get_a_user.getString(1);
                get_user_position = get_a_user.getString(3);
                get_user_category = get_a_user.getString(5);
                byte[] get_user_image = get_a_user.getBlob(4);
                bt = BitmapFactory.decodeByteArray(get_user_image, 0, get_user_image.length);

                user_image_recipient.setImageBitmap(bt);
            }

            user_name.setText(get_username);
            user_position.setText(get_user_position);

            chat_lists = db.displayChats(session.getUserId(), get_friend_id);
            chat_lists.moveToFirst();

            if (chat_lists.getCount() > 0) {
                do{  //loading previously initiated chats
                    chatListModels.add(new ChatListModel(chat_lists.getString(3), chat_lists.getString(5), chat_lists.getString(6), chat_lists.getString(4), chat_lists.getString(1), chat_lists.getString(9),chat_lists.getString(8),chat_lists.getString(10)));
                }while(chat_lists.moveToNext());

                chatListAdapter = new ChatListAdapter(chatListModels,multiselect_list, Chat.this);
                chats.setAdapter(chatListAdapter);
                chats.scrollToPosition(chatListAdapter.getItemCount() - 1); //scroll to bottom


                send_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        populateList(message.getText().toString().trim(), false);
                    }
                });
            } else {
                send_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        populateList(message.getText().toString().trim(), IS_NEW);
                    }
                });
            }

        }else if (intent_type.equalsIgnoreCase("multiple")){
            Bundle bundle = getIntent().getExtras();
            array = bundle.getStringArrayList("user_ids");

            single.setVisibility(View.INVISIBLE);
            user_image_recipient.setVisibility(View.GONE);
            multiple.setVisibility(View.VISIBLE);

            StringBuilder names = new StringBuilder();

            pick_from_camera_or_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    is_group_image_message = true;
                    pick_image();
                }
            });

            Cursor get_a_user;
            for (int y=0; y < array.size(); y++){ //get the names of the selected users
                get_a_user = db.get_a_user(array.get(y));
                if (get_a_user.getCount() > 0) {
                    TextView init = new TextView(Chat.this);
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(
                                    50,
                                    50);
                    get_a_user.moveToFirst();
                    names.append(get_a_user.getString(1)).append(", ");

                    init.setText(get_a_user.getString(1).substring(0,1));
                    init.setBackgroundResource(R.drawable.blue_circle);
                    init.setTextColor(Color.WHITE);
                    init.setId(y);
                    init.setGravity(Gravity.CENTER);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        init.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                    init.setTag(y);
                    init.setLayoutParams(layoutParams);

                    initials.addView(init,y);
                }
            }


            user_name.setText(array.size()+" recipients");
            user_position.setText(names);

            send_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date date = new Date();
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat timeformat = new SimpleDateFormat("h:mm:ss a");
                    String getDate = dateformat.format(date);
                    String getTime = timeformat.format(date);
                    String getText = message.getText().toString().trim();

                    String chat_id =  generateChatId();
                    long ccl;

                    if (!TextUtils.isEmpty(getText)) {
                        Cursor users;
                        for (int y=0; y<array.size(); y++){ //get the names of the selected users
                            users = db.get_a_user(array.get(y));
                            users.moveToFirst();
                            if (users.getCount() > 0) {
                                ccl = db.create_chat_lists(session.getUserId(),array.get(y), users.getString(3), getTime, users.getString(5), getText,"false",chat_id);
                                if (ccl != -1){
                                    db.create_chat(session.getUserId(), array.get(y), getText, session.getUserId(), getDate, getTime,"unseen", "","false",chat_id);
                                }
                            }
                        }
                        chatListModels.add(new ChatListModel(getText, getDate, getTime, session.getUserId(), session.getUserId(),"false",null,chat_id)); //populate the listview
                        message.setText("");


                        chat_lists = db.displayChats(session.getUserId(), get_friend_id);
                        chat_lists.moveToFirst();

                        if (chat_lists.getCount() > 0) {
                            chatListAdapter.notifyDataSetChanged();
                        }else{
                            chatListAdapter = new ChatListAdapter(chatListModels, multiselect_list,Chat.this);
                            multiple_listview.setAdapter(chatListAdapter);
                            IS_NEW = false;
                        }
                        multiple_listview.smoothScrollToPosition(chatListAdapter.getItemCount() - 1);
                    }
                }
            });

        }

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromChooseImage){
                    getSupportFragmentManager().popBackStack();
                    chat_toolbar.setVisibility(View.VISIBLE);
                    user_image_recipient.setVisibility(View.VISIBLE);
                    choose_image_text.setVisibility(View.GONE);

                    fromChooseImage = false;
                }else if(is_preview_image_open) {
                    is_preview_image_open = false;
                    getSupportFragmentManager().popBackStack(); //closes the preview fragment
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.OruijeBlue)));
                    }
                }else{
                    finish();
                }
                hideKeys();
            }
        });
    }

    public void previewImage(String image_path){
        PreviewImage p = new PreviewImage();

        Bundle bundle = new Bundle();
        bundle.putString("image_path",image_path);

        p.setArguments(bundle);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.select_image_container, p, "Preview Image").commit();
    }


    public void populateList(String getText,Boolean newchat){
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeformat = new SimpleDateFormat("h:mm:ss a");
        String getDate = dateformat.format(date);
        String getTime = timeformat.format(date);

        if (!TextUtils.isEmpty(getText)) {
            db.check_chat(session.getUserId(), get_friend_id);
            String chat_id = generateChatId();
            db.create_chat_lists(session.getUserId(), get_friend_id, get_user_position, getTime, get_user_category, getText,"false",chat_id);

            db.create_chat(session.getUserId(), get_friend_id, getText, session.getUserId(), getDate, getTime,"unseen", "","false",chat_id);
            db.create_chat(session.getUserId(), get_friend_id, "Yo man, i got your text", get_friend_id, getDate, getTime,"unseen", "","false",chat_id);

            chatListModels.add(new ChatListModel(getText, getDate, getTime, session.getUserId(), session.getUserId(),"false",null,chat_id)); //populate the listview
            chatListModels.add(new ChatListModel("Yo man, i got your text", getDate, getTime, get_friend_id, session.getUserId(),"false",null,chat_id)); //populate the listview

            if (newchat){
                chatListAdapter = new ChatListAdapter(chatListModels, multiselect_list, Chat.this);
                chats.setAdapter(chatListAdapter);
                IS_NEW = false;
            }else {
                chatListAdapter.notifyDataSetChanged();
            }
            message.setText("");
            chats.smoothScrollToPosition(chatListAdapter.getItemCount() - 1);
        }
    }

    public String generateChatId(){
        int id = (int)(Math.random()*10000);
        return "chat_id"+id+id;
    }

    @Override
    public void onBackPressed() {
        hideKeys();

        if (isMultiSelect){
            finish_deletion();
        }else if(is_preview_image_open) {
            getSupportFragmentManager().popBackStack(); //closes the preview fragment
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.OruijeBlue)));
            }
            is_preview_image_open = false;
        }else{
            finish();
        }
    }


    public void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_IMAGE_FROM_GALLERY);
    }


    private void pick_image() {
        // Image and text item data's key.
        final String CUSTOM_ADAPTER_IMAGE = "image";
        final String CUSTOM_ADAPTER_TEXT = "text";

        // Create a alert dialog builder.
        AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
        // Set icon value.
//        builder.setIcon(R.mipmap.ic_launcher);
        // Set title value.
//        builder.setTitle("Choose image from...");

        // Create SimpleAdapter list data.
        List<Map<String, Object>> dialogItemList = new ArrayList<Map<String, Object>>();
        int listItemLen = icons.length;
        for(int i=0;i<listItemLen;i++)
        {
            Map<String, Object> itemMap = new HashMap<String, Object>();
            itemMap.put(CUSTOM_ADAPTER_IMAGE, icons[i]);
            itemMap.put(CUSTOM_ADAPTER_TEXT, select[i]);

            dialogItemList.add(itemMap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(Chat.this, dialogItemList,
                R.layout.bottom_sheet,
                new String[]{CUSTOM_ADAPTER_IMAGE, CUSTOM_ADAPTER_TEXT},
                new int[]{R.id.picker_icon,R.id.picker_text});

        // Set the data adapter.
        builder.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemIndex) {
                if (select[itemIndex].equalsIgnoreCase("camera")){
                    captureImage();
                }else{
                    pickFromGallery();
                }
            }
        });

        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    /**
     * Capture Image and save into database
     */

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Set capture image to database and set to image preview
     *
     * @param data
     */
    private void onCaptureImageResult(Intent data) {

        Bundle extras = data.getExtras();
        String imageTempName = "ORUIJE"+(int)(Math.random()*10000);
        Bitmap imageBitmap = (Bitmap) extras.get("data");

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getApplicationContext(), imageBitmap, imageTempName);
        String picturePath = getRealPathFromURI(tempUri);
        updateUi(picturePath);

    }
    public Uri getImageUri(Context inContext, Bitmap inImage, String imageName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, imageName, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public Bitmap convertSrcToBitmap(String imageSrc) {
        Bitmap myBitmap = null;
        File imgFile = new File(imageSrc);
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return myBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {

            onCaptureImageResult(data);
        }else if (requestCode == REQUEST_IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null){

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver()
                    .query(selectedImage, filePathColumn, null, null,
                            null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            updateUi(picturePath);

        }
    }

    private void updateUi(String imageUri) {
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeformat = new SimpleDateFormat("h:mm:ss a");
        String getDate = dateformat.format(date);
        String getTime = timeformat.format(date);

        try {

            Cursor users;
            long ccl;

            if (is_group_image_message){
                String chat_id="";
                for (int y=0; y<array.size(); y++){ //get the names of the selected users
                    chat_id = generateChatId();
                    users = db.get_a_user(array.get(y));
                    users.moveToFirst();
                    if (users.getCount() > 0) {
                        ccl = db.create_chat_lists(session.getUserId(),array.get(y), users.getString(3), getTime, users.getString(5), "","true",chat_id);
                        if (ccl != -1){
                            db.create_chat(session.getUserId(), array.get(y), "", session.getUserId(), getDate, getTime,"unseen", imageUri,"true",chat_id);
                        }
                    }
                }
                chatListModels.add(new ChatListModel("", getDate, getTime, session.getUserId(), session.getUserId(),"true",imageUri,chat_id)); //populate the listview

                chat_lists = db.displayChats(session.getUserId(), get_friend_id);
                chat_lists.moveToFirst();

                if (chat_lists.getCount() > 0) {
                    chatListAdapter.notifyDataSetChanged();
                }else{
                    chatListAdapter = new ChatListAdapter(chatListModels, multiselect_list,Chat.this);
                    multiple_listview.setAdapter(chatListAdapter);
                    IS_NEW = false;
                }
                multiple_listview.smoothScrollToPosition(chatListAdapter.getItemCount() - 1);


                is_group_image_message = false;
            }else {
                String chat_id = generateChatId();

                db.create_chat_lists(session.getUserId(), get_friend_id, get_user_position, getTime, get_user_category, "", "true", chat_id);
                db.create_chat(session.getUserId(), get_friend_id, "", session.getUserId(), getDate, getTime, "unseen", imageUri, "true", chat_id);

                chatListModels.add(new ChatListModel("", getDate, getTime, session.getUserId(), session.getUserId(), "true", imageUri, chat_id)); //populate the listview

                chat_lists = db.displayChats(session.getUserId(), get_friend_id);
                chat_lists.moveToFirst();

                if (chat_lists.getCount() > 0) {
                    chatListAdapter.notifyDataSetChanged();
                } else {
                    chatListAdapter = new ChatListAdapter(chatListModels, multiselect_list, Chat.this);
                    chats.setAdapter(chatListAdapter);
                    IS_NEW = false;
                }
                chats.smoothScrollToPosition(chatListAdapter.getItemCount() - 1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void hideKeys(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View vw = this.getCurrentFocus();
        if(vw!=null){

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(vw.getWindowToken(),0);
        }
    }


    public void multi_select(int position) {
        ChatListModel ccm = chatListModels.get(position);
        copy.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);

        if (multiselect_list.contains(chatListModels.get(position))) {
            multiselect_list.remove(chatListModels.get(position));
            multiselect_list_id.remove(ccm.getChat_id());
            clipboard_text.remove(ccm.getMessage());

            if (images_selected.contains(ccm.getImage_message())){
                images_selected.remove(ccm.getImage_message());
            }

        }else {
            multiselect_list.add(chatListModels.get(position));
            multiselect_list_id.add(ccm.getChat_id());
            clipboard_text.add(ccm.getMessage()); //copy text to clipboard when selected

            //hide the copy icon if the user selected both images and text at the same time
            if (TextUtils.isEmpty(ccm.getMessage())){ //means the selected chat is image
                images_selected.add(ccm.getImage_message());
            }
        }

        if (images_selected.size()>0){ //if an image has been selected
           copy.setVisibility(View.GONE); //images cannot be copied- at least not in this version
        }else{
            copy.setVisibility(View.VISIBLE); //show the copy icon for the selected text to be copied
        }

        //update the selected chat_count
        if (multiselect_list.size() > 0) {
            if (multiselect_list.size() == 1) {
                selected_item_count.setText(multiselect_list.size()+" chat selected");
            }else{
                selected_item_count.setText(multiselect_list.size()+" chats selected");
            }
        }else{
            selected_item_count.setText("No chats selected");
            copy.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }
        refreshAdapter();
    }

    public void refreshAdapter() {
        chatListAdapter.selected_usersList=multiselect_list;
        chatListAdapter.chat_list=chatListModels;
        chatListAdapter.notifyDataSetChanged();
    }

    public void finish_deletion(){
        isMultiSelect = false;
        multiselect_list = new ArrayList<>();
        multiselect_list_id = new ArrayList<>();
        action_mode_container.setVisibility(View.GONE);
        normal_toolbar.setVisibility(View.VISIBLE);
        refreshAdapter();
    }

    private void copyToClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(Chat.this, "Text Copied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPositiveClick(int from) {
        if(from==1) {
            if (multiselect_list.size() > 0) {
                for (int i = 0; i < multiselect_list.size(); i++) {
                    chatListModels.remove(multiselect_list.get(i));
                    db.delete_chat(multiselect_list_id.get(i));
                }
                chatListAdapter.notifyDataSetChanged();
                finish_deletion();
            }
        }
    }
    
    public static String convertArrayToString(ArrayList<String> strArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strArray.size(); i++) {
            stringBuilder.append(strArray.get(i)).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void onNegativeClick(int from) { }

    @Override
    public void onNeutralClick(int from) { }
}
