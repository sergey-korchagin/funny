package com.parse.starter.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.starter.MainActivity;
import com.parse.starter.R;
import com.parse.starter.interfaces.CustomTouchListener;
import com.parse.starter.interfaces.GetMorePhotos;
import com.parse.starter.utils.TouchImageView;
import com.parse.starter.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


/**
 * Created by User on 30/11/2015.
 */
public class PhotoPagerAdapter extends android.support.v4.view.PagerAdapter implements GetMorePhotos{
List<ParseObject> mImages;
    Activity activity;
    List<ParseObject> mMoreImages;
    boolean isLikeClicked = false;
    CustomTouchListener listener;

    public PhotoPagerAdapter(List<ParseObject> images, Activity activity, CustomTouchListener listener) {
        this.mImages = images;
        this.activity = activity;
        this.listener = listener;
        if (mImages == null)
            mImages = new ArrayList<>();


    }
    @Override
    public int getCount() {
        return mImages.size();
    }




    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.photo_layout, container, false);
        final TouchImageView mImage = (TouchImageView) root.findViewById(R.id.touchImage);
        final ProgressBar progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        if(mImages.get(position).get("mPicture")!=null){
            ParseFile applicantResume = (ParseFile) mImages.get(position).get("mPicture");
          //  applicantResume.getUrl();
            applicantResume.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                       final Bitmap bmp;
                        try {
                          bmp  = BitmapFactory.decodeByteArray(data, 0, data.length);
                            mImage.setImageBitmap(bmp);

                        }catch (OutOfMemoryError er){
                            er.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);


                    } else {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
        mImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                   switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                listener.fullScreenTouch(1);

            case MotionEvent.ACTION_UP: // отпускание
                listener.fullScreenTouch(2);


        }
                return false;
            }


        });

                   ((ViewPager) container).addView(root);


        return root;

    }


    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void getMorePhotos(List<ParseObject> parseObjects, int size) {

        mImages.addAll(parseObjects);

        //mImages.notifyAll();
    }

    public void getMoreNotSeenPhotos(List<ParseObject> parseObjects){
        mImages = parseObjects;
    }

    public void insertTopPictures(List<ParseObject> parseObjects){
        mImages = parseObjects;
        parseObjects.toString();
    }
}
