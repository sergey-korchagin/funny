package com.parse.starter.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.parse.starter.R;
import com.parse.starter.adapters.PhotoPagerAdapter;
import com.parse.starter.interfaces.CustomTouchListener;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by serge_000 on 04/12/2015.
 */
public class TopFragment extends Fragment implements View.OnClickListener,ViewPager.OnPageChangeListener,
        CustomTouchListener {
    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
    ImageView btnShare;
    ImageView btnSave;
    ImageView btnAll;
    ImageView btnLike;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    ImageView mSmallImage;
    TextView likesCounterView;
    int mPosition;
    LinearLayout layoutHeader;
    CustomTouchListener customTouchListener;

ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.top_fragment, container, false);
        Constants.FROM_SETTINGS = false;
        progressDialog = ProgressDialog.show(getActivity(),"","Картинки загружаются...");

        layoutHeader = (LinearLayout)root.findViewById(R.id.headerTopLayout);

        btnShare = (ImageView) root.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);

        btnSave = (ImageView)root.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);


        btnAll = (ImageView)root.findViewById(R.id.btnAll);
        btnAll.setOnClickListener(this);

        mPager = (ViewPager) root.findViewById(R.id.photos_image_pager);
        mPager.addOnPageChangeListener(this);

        mSmallImage = (ImageView)root.findViewById(R.id.smallImage);
        likesCounterView = (TextView)root.findViewById(R.id.likesCounter);

customTouchListener = this;
        initSmallImage();
        checkIfStorageAvailable();
        getCategories();

        return root;
    }

    public void getCategories() {

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
                    categories = (List<ParseObject>) o;
                    mAdapter = new PhotoPagerAdapter(categories, getActivity(),customTouchListener);
                    mPager.setAdapter(mAdapter);
                    likesCounterView.setText(Integer.toString((Integer) categories.get(0).get("likes")));
                    progressDialog.dismiss();

                }
            }
        });
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
                    if (small.get(0).get("mImage") != null) {
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

    @Override
    public void onClick(View v) {
        if(btnAll.getId() == v.getId()){
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

                        PicturesMainFragment picturesMainFragment = new PicturesMainFragment();
                        Utils.replaceFragment(getFragmentManager(), android.R.id.content, picturesMainFragment, false);

                    }
                }
            });
        }if (btnShare.getId() == v.getId()) {
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


        }

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
                        if (!dir.exists())
                            dir.mkdirs();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDate = sdf.format(new Date());
                        File file = new File(dir, "funny_" + currentDate + ".png");
                        try {
                            FileOutputStream fOut = new FileOutputStream(file);
                            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                            fOut.flush();
                            fOut.close();
                            Toast.makeText(getActivity(), "Saved to " + file_path, Toast.LENGTH_SHORT).show();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "funny");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "saved from app");
                        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
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
            Utils.showAlert(getActivity(), "Error", "No external storage available");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPosition = position;
        if(categories!=null){
            likesCounterView.setText(Integer.toString((Integer) categories.get(mPosition).get("likes")));

        }

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void fullScreenTouch() {

    }
}
