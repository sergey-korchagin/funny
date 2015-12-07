package com.parse.starter.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.MainActivity;
import com.parse.starter.R;
import com.parse.starter.adapters.PhotoPagerAdapter;
import com.parse.starter.interfaces.CustomTouchListener;
import com.parse.starter.managers.TinyDB;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 30/11/2015.
 */
public class PicturesMainFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener, CustomTouchListener{

    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
    List<ParseObject> updatedCategories;
    List<ParseObject> top;
    int skip = 0;
    int querySize;
    public int mPosition;
    ImageView btnShare;
    ImageView btnSave;
    ImageView btnTop;
    ImageView btnLike;
    ImageView btnMore;
    ImageView btnNtShown;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    int width = 0;
    int height = 0;
    ImageView mSmallImage;
    boolean isTop = false;
    LinearLayout layoutHeader;
    boolean isLikeClicked=false;
   // HashMap<String,String> likesHashMap;
    ArrayList<String> likesList;
    TinyDB tinydb;
    TextView likesCounterView;
    String SAVED_LIST = "saved_list";
    ProgressDialog progressDialog;
    ImageView btnUpload;
    private final int REQUEST_CODE_FROM_GALLERY_IMAGE = 1;
    boolean videoSelected =false;
    int orientation;
    Bitmap photo = null;
    ImageView menuTp;
    LinearLayout likesLayout;
    CustomTouchListener customTouchListener;
    ArrayList<String> seenItemsLIst;

//    @SuppressLint("ValidFragment")
//    public PicturesMainFragment(List<ParseObject> objects) {
//     this.categories = objects;
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View root = inflater.inflate(R.layout.pictures_main_fragment, container, false);
        Constants.FROM_SETTINGS = false;
        progressDialog = ProgressDialog.show(getActivity(), "", "Картинки загружаются...");
//        progressDialog = new ProgressDialog(getActivity(),R.style.MyTheme);
//        progressDialog.setMessage("Картинки загружаются...");
//        progressDialog.show();
        //likesHashMap = new HashMap<>();
        likesList = new ArrayList<>();
        seenItemsLIst = new ArrayList<>();
        tinydb = new TinyDB(getActivity());
        likesList = tinydb.getListString(SAVED_LIST);
        seenItemsLIst=tinydb.getListString(Constants.SEEN_LIST);
        likesCounterView = (TextView)root.findViewById(R.id.likesCounter);
        customTouchListener = this;


        btnShare = (ImageView) root.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);

        btnSave = (ImageView)root.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnTop = (ImageView)root.findViewById(R.id.btnTop);
        btnTop.setOnClickListener(this);

        btnLike = (ImageView)root.findViewById(R.id.btnLike);
        //btnLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.like_tmp));

        btnLike.setOnClickListener(this);

        btnNtShown =(ImageView)root.findViewById(R.id.btnNotSeen);
        btnNtShown.setOnClickListener(this);


        btnMore = (ImageView)root.findViewById(R.id.btnMore);
        btnMore.setOnClickListener(this);
      //  layoutHeader = (LinearLayout)root.findViewById(R.id.headerLayout);
      //  menuButton = (ImageView)root.findViewById(R.id.menuButton);
       likesLayout= (LinearLayout)root.findViewById(R.id.likesLayout);


        btnLike.setVisibility(View.INVISIBLE);
        btnShare.setVisibility(View.INVISIBLE);
        btnMore.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.INVISIBLE);
        btnTop.setVisibility(View.INVISIBLE);

        mPager = (ViewPager) root.findViewById(R.id.photos_image_pager);
        mPager.addOnPageChangeListener(this);

        mSmallImage = (ImageView)root.findViewById(R.id.smallImage);
        initSmallImage();

        checkIfStorageAvailable();
        getQuerySize();
        getCategories();
        initSmallImage();


        menuTp = (ImageView)root.findViewById(R.id.meu);
        menuTp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnLike.getVisibility() == View.INVISIBLE){
                    Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
                    btnLike.setVisibility(View.VISIBLE);
                    btnLike.setAnimation(animFadeIn);
                    btnShare.setVisibility(View.VISIBLE);
                    btnShare.setAnimation(animFadeIn);
                    btnMore.setVisibility(View.VISIBLE);
                    btnMore.setAnimation(animFadeIn);
                    btnSave.setVisibility(View.VISIBLE);
                    btnSave.setAnimation(animFadeIn);
                    btnTop.setVisibility(View.VISIBLE);
                    btnSave.setAnimation(animFadeIn);
                    Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
                    mSmallImage.setVisibility(View.INVISIBLE);
                    mSmallImage.setAnimation(animFadeOut);
                    likesLayout.setVisibility(View.INVISIBLE);
                    likesLayout.setAnimation(animFadeOut);


                }else{
                    Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
                    btnLike.setVisibility(View.INVISIBLE);
                    btnLike.setAnimation(animFadeOut);
                    btnShare.setVisibility(View.INVISIBLE);
                    btnShare.setAnimation(animFadeOut);
                    btnMore.setVisibility(View.INVISIBLE);
                    btnMore.setAnimation(animFadeOut);
                    btnSave.setVisibility(View.INVISIBLE);
                    btnSave.setAnimation(animFadeOut);
                    btnTop.setVisibility(View.INVISIBLE);
                    btnTop.setAnimation(animFadeOut);
                    Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
                    mSmallImage.setVisibility(View.VISIBLE);
                    mSmallImage.setAnimation(animFadeIn);
                    likesLayout.setVisibility(View.VISIBLE);
                    likesLayout.setAnimation(animFadeIn);

                }
            }
        });

        return root;
    }

    public void initLikeButton() {
     //   if (categories != null) {
          if (likesList.contains(categories.get(mPosition).getObjectId())) {
                btnLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.dislike_tmp));
            } else {
                btnLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.like_tmp));
            }
//        }
//        if(mPosition == 0){
//            btnLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.like_tmp));
//        }

    }


    public void getQuerySize() {

        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                    querySize = ((List) o).size();
                }
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
    if (position % 5 == 1 && skip < querySize) {
        skip = skip + 5;

        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("createdAt");
      //  query.whereNotContainedIn();
        query.setSkip(skip);
        query.setLimit(5);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                    updatedCategories = (List<ParseObject>) o;
                    mAdapter.getMorePhotos(updatedCategories, querySize);
                    mAdapter.notifyDataSetChanged();

                }
            }
        });


    }
    likesCounterView.setText(Integer.toString((Integer) categories.get(mPosition).get("likes")));
        if(!seenItemsLIst.contains(categories.get(mPosition).getObjectId())){
            seenItemsLIst.add(categories.get(mPosition).getObjectId());
        }
        initLikeButton();
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void getCategories() {

        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("createdAt");
        query.setLimit(5);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                    categories = (List<ParseObject>) o;
                    mAdapter = new PhotoPagerAdapter(categories, getActivity(),customTouchListener);
                    mPager.setAdapter(mAdapter);
                    initLikeButton();
                    likesCounterView.setText(Integer.toString((Integer) categories.get(0).get("likes")));
                    if(!seenItemsLIst.contains(categories.get(0).getObjectId())) {
                        seenItemsLIst.add(categories.get(0).getObjectId());
                    }
                    progressDialog.dismiss();

                }
            }
        });
    }

    @Override
    public void onResume() {
        likesList =  tinydb.getListString(SAVED_LIST);
        seenItemsLIst =  tinydb.getListString(Constants.SEEN_LIST);

        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if (btnShare.getId() == v.getId()) {
                if(mExternalStorageAvailable && mExternalStorageWriteable){
                    sendShareIntentPhoto(mPosition);
                }else{
                    sendShareIntentLink(mPosition);
            }
        }else if(btnSave.getId() == v.getId()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true).setMessage("Want save?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        savePicture();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            Window window = alert.getWindow();
            window.setGravity(Gravity.CENTER);
            alert.show();


        }else if(btnTop.getId() == v.getId()){
            TopFragment topFragment = new TopFragment();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, topFragment, true);

        }else if(btnLike.getId() == v.getId()){
            if(!likesList.contains(categories.get(mPosition).getObjectId())){
                incrementLikes();
                int d1 = ((Integer)categories.get(mPosition).get("likes"));
                d1=d1--;
                likesCounterView.setText(Integer.toString(d1));
            }else{
                decrementLikes();
                int d1 = ((Integer)categories.get(mPosition).get("likes"));
                d1=d1--;
                likesCounterView.setText(Integer.toString(d1));
            }
        }else if(btnMore.getId() == v.getId()){
            enableAlertMenu();
        }else if(btnNtShown.getId() == v.getId()){
            tinydb.putListString(Constants.SEEN_LIST,seenItemsLIst);

            NotShown notShown = new NotShown();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, notShown, false);

        }
    }


    public void decrementLikes() {
        categories.get(mPosition).increment("likes", -1);
        categories.get(mPosition).saveInBackground();
        likesList.remove(categories.get(mPosition).getObjectId());
        initLikeButton();

    }

    public void incrementLikes(){
        categories.get(mPosition).increment("likes");
        categories.get(mPosition).saveInBackground();
        likesList.add(categories.get(mPosition).getObjectId());
        initLikeButton();

    }

    public void sendShareIntentLink(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true).setMessage("No enough storage! But you still can share link to picture!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (categories.get(position).get("mPicture") != null) {
                            ParseFile applicantResume = (ParseFile) categories.get(position).get("mPicture");
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, applicantResume.getUrl());
                            startActivity(Intent.createChooser(shareIntent, "Share link"));
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        Window window = alert.getWindow();
        window.setGravity(Gravity.TOP);
        alert.show();


    }

    public void sendShareIntentPhoto(int position) {
        if (categories.get(position).get("mPicture") != null) {
            ParseFile applicantResume = (ParseFile) categories.get(position).get("mPicture");
            applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "title");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                    Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);

                    OutputStream outstream;
                    try {
                        outstream = getActivity().getContentResolver().openOutputStream(uri);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                        outstream.close();
                    } catch (Exception ex) {
                        System.err.println(e.toString());
                    }

                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share picture"));


                } else {
                    e.printStackTrace();
                }
            }
        }

            );

            }
        }


    public void checkIfStorageAvailable(){
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public void initSmallImage(){
        ParseQuery query = new ParseQuery("smallImage");
        query.addDescendingOrder("createdAt");
        query.setLimit(1);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }
            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                    List<ParseObject> small = (List<ParseObject>) o;
                    if(small.get(0).get("mImage") != null){
                        ParseFile applicantResume = (ParseFile) small.get(0).get("mImage");
                        applicantResume.getDataInBackground(new GetDataCallback() {
                                                                public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                 mSmallImage.setImageBitmap(bmp);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    }

                        );

                    }
                }
            }
        });
    }

public void savePicture(){
    if(mExternalStorageWriteable && mExternalStorageAvailable){
        //save
        if (categories.get(mPosition).get("mPicture") != null) {
            ParseFile applicantResume = (ParseFile) categories.get(mPosition).get("mPicture");
            applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    //save
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/FunnyPhotos";
                    File dir = new File(file_path);
                    if(!dir.exists())
                        dir.mkdirs();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate= sdf.format(new Date());
                    File file = new File(dir, "funny_" +currentDate+ ".png");
                    try {
                        FileOutputStream fOut = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                        Toast.makeText(getActivity(), "Saved to "+ file_path,  Toast.LENGTH_SHORT).show();

                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "funny");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "saved from app");
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
                    values.put("_data", file.getAbsolutePath());

                    ContentResolver cr = getActivity().getContentResolver();
                    cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    e.printStackTrace();
                }
            }
        }
            );
        }
    }else{
        Utils.showAlert(getActivity(),"Error", "No external storage available");
    }
}



    @Override
    public void onPause() {
        super.onPause();

        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST,seenItemsLIst);

    }

    @Override
    public void onStop() {
        super.onStop();

        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST,seenItemsLIst);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST,seenItemsLIst);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == getActivity().RESULT_OK) {
                if (requestCode == REQUEST_CODE_FROM_GALLERY_IMAGE) {

                    Uri selectedImage = data.getData();
                    if (!ifVideo(selectedImage)) {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"Alex.Bagranov@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT, "Смешная фотография");
                        email.putExtra(Intent.EXTRA_STREAM, selectedImage);
                        email.putExtra(Intent.EXTRA_TEXT, "Спасибо что вы посылаете нам свои материалы, сотни пользователей обязательно увидят их! Нафаня");
                        email.setType("message/rfc822");
                        startActivity(Intent.createChooser(email, "Выбор меил агента"));
                    } else {
                        Utils.showAlert(getActivity(), "Ошибка", "Неподдерживаемый формат файла");
                    }
                }
            }
    }




    public boolean ifVideo(Uri uri){
        if(uri.toString().contains("video")){
            return true;
        }
        return false;
    }


    public void enableAlertMenu(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
    //    builderSingle.setIcon(R.drawable.ic_launcher);
        //builderSingle.setTitle("Дополнительные возможности");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.dialog_list_item);
        arrayAdapter.add("Послать нам свой прикол");
        arrayAdapter.add("Пригласить друга");
        arrayAdapter.add("Настройки");


        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, REQUEST_CODE_FROM_GALLERY_IMAGE);
                                break;
                            case 1:
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/plain");
                                share.putExtra(Intent.EXTRA_TEXT, "когда нибудь это будет приглашение в нафаню, а пока качни айдаприкол https://play.google.com/store/apps/details?id=ru.idaprikol&hl=ru");
                                startActivity(Intent.createChooser(share, "Пригласить"));
                                break;
                            case 2:
                                SettingsFragment settingsFragment = new SettingsFragment();
                                Utils.replaceFragment(getFragmentManager(), android.R.id.content, settingsFragment, true);
                                break;
                        }

                    }
                });
        builderSingle.show();
    }


    @Override
    public void fullScreenTouch() {
        if(likesLayout.getVisibility() == View.VISIBLE)
        {
            Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
            menuTp.setVisibility(View.INVISIBLE);
            menuTp.setAnimation(animFadeOut);
            likesLayout.setVisibility(View.INVISIBLE);
            likesLayout.setAnimation(animFadeOut);
            mSmallImage.setVisibility(View.INVISIBLE);
            mSmallImage.setAnimation(animFadeOut);

        }else {
            Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);

            menuTp.setVisibility(View.VISIBLE);
            menuTp.setAnimation(animFadeIn);

            likesLayout.setVisibility(View.VISIBLE);
            likesLayout.setAnimation(animFadeIn);

            mSmallImage.setVisibility(View.VISIBLE);
            mSmallImage.setAnimation(animFadeIn);

        }
            if (btnLike.getVisibility() == View.VISIBLE){
                Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
                btnLike.setVisibility(View.INVISIBLE);
                btnLike.setAnimation(animFadeOut);
                btnShare.setVisibility(View.INVISIBLE);
                btnShare.setAnimation(animFadeOut);
                btnMore.setVisibility(View.INVISIBLE);
                btnMore.setAnimation(animFadeOut);
                btnSave.setVisibility(View.INVISIBLE);
                btnSave.setAnimation(animFadeOut);
                btnTop.setVisibility(View.INVISIBLE);
                btnTop.setAnimation(animFadeOut);
                likesLayout.setVisibility(View.INVISIBLE);
                mSmallImage.setVisibility(View.INVISIBLE);
                menuTp.setVisibility(View.INVISIBLE);
                menuTp.setAnimation(animFadeOut);

            }
        }




}
