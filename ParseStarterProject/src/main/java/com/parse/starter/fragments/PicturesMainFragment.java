package com.parse.starter.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
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
import com.parse.starter.managers.TinyDB;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.Utils;

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
public class PicturesMainFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
    List<ParseObject> updatedCategories;
    int skip = 0;
    int querySize;
    public int mPosition;
    ImageView btnShare;
    ImageView btnSave;
    ImageView btnTop;
    ImageView btnLike;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View root = inflater.inflate(R.layout.pictures_main_fragment, container, false);
        Constants.FROM_SETTINGS = false;
        //likesHashMap = new HashMap<>();
        likesList = new ArrayList<>();
        tinydb = new TinyDB(getActivity());
        likesList = tinydb.getListString(SAVED_LIST);
        likesCounterView = (TextView)root.findViewById(R.id.likesCounter);


        btnShare = (ImageView) root.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);

        btnSave = (ImageView)root.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnTop = (ImageView)root.findViewById(R.id.btnTop);
        btnTop.setOnClickListener(this);

        btnLike = (ImageView)root.findViewById(R.id.btnLike);
        //btnLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.like_tmp));

        btnLike.setOnClickListener(this);

        layoutHeader = (LinearLayout)root.findViewById(R.id.headerLayout);

        initSmallImage();

        mPager = (ViewPager) root.findViewById(R.id.photos_image_pager);
        mPager.addOnPageChangeListener(this);

        mSmallImage = (ImageView)root.findViewById(R.id.smallImage);
        checkIfStorageAvailable();
        getQuerySize();
        getCategories();
        initSmallImage();

//       final LinearLayout layout = (LinearLayout)root.findViewById(R.id.headerLayout);
//
//        ViewTreeObserver vto = layout.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//               width  = layout.getMeasuredWidth();
//                height = layout.getMeasuredHeight();
//
//            }
//        });
//        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(height-5,height-5);
//        parms.gravity = Gravity.CENTER;
//        parms.setMargins(0, 0, 30, 0);
//        btnShare.setLayoutParams(parms);
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
if(!isTop) {

    mPosition = position;

    if (position % 5 == 1 && skip < querySize) {
        skip = skip + 5;

        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("createdAt");
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
}
        initLikeButton();
        likesCounterView.setText(categories.get(mPosition).get("likes").toString());
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
                    mAdapter = new PhotoPagerAdapter(categories, getActivity());
                    mPager.setAdapter(mAdapter);
                    initLikeButton();
                    likesCounterView.setText(Integer.toString((Integer)categories.get(mPosition).get("likes")));



                }
            }
        });
    }

    @Override
    public void onResume() {
      likesList =  tinydb.getListString(SAVED_LIST);

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
            builder.setCancelable(true).setMessage("Want save? Nigger!")
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
            if(!isTop){
                enableTopMode();
                isTop = true;
                layoutHeader.setBackgroundColor(Color.parseColor("#FF00CC"));
                btnLike.setVisibility(View.GONE);
            }else {
                skip =0;
                disableTopMode();
                isTop = false;
                layoutHeader.setBackgroundColor(Color.parseColor("#330099"));
                btnLike.setVisibility(View.VISIBLE);

            }
        }else if(btnLike.getId() == v.getId()){
            if(!likesList.contains(categories.get(mPosition).getObjectId())){
                incrementLikes();
               // likesCounterView.setText(Utils.addLikes(categories.get(mPosition).get("likes")));
                isLikeClicked = true;
            }else{
                decrementLikes();
            //    likesCounterView.setText(Utils.removeLikes(categories.get(mPosition).get("likes").toString()));

                isLikeClicked = false;
            }
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

    public void enableTopMode(){
        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("likes");
        query.setLimit(10);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                    List<ParseObject> top = (List<ParseObject>) o;
                    mAdapter.insertTopPictures(top);
//                    mAdapter.notifyDataSetChanged();
                    mPager.setAdapter(mAdapter);

                }
            }
        });
    }

        public void disableTopMode(){
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
                        mAdapter.insertTopPictures(categories);
                        mPager.setAdapter(mAdapter);
                    }
                }
            });
        }

    @Override
    public void onPause() {
        super.onPause();

        tinydb.putListString(SAVED_LIST,likesList);


    }

    @Override
    public void onStop() {
        super.onStop();

        tinydb.putListString(SAVED_LIST,likesList);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tinydb.putListString(SAVED_LIST,likesList);

    }


}
