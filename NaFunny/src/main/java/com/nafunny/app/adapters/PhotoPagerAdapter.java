package com.nafunny.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nafunny.app.interfaces.CustomTouchListener;
import com.nafunny.app.interfaces.GetMorePhotos;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.nafunny.app.R;
import com.nafunny.app.interfaces.BannerViewListener;
import com.nafunny.app.utils.TouchImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by User on 30/11/2015.
 */
public class PhotoPagerAdapter extends android.support.v4.view.PagerAdapter implements GetMorePhotos {
List<ParseObject> mImages;
    Activity activity;
    List<ParseObject> mMoreImages;
    boolean isLikeClicked = false;
    CustomTouchListener listener;
    BannerViewListener bannerViewListener;

    public PhotoPagerAdapter(List<ParseObject> images, Activity activity, CustomTouchListener listener,BannerViewListener bannerViewListener) {
        this.mImages = images;
        this.activity = activity;
        this.listener = listener;
        this.bannerViewListener = bannerViewListener;
        if (mImages == null)
            mImages = new ArrayList<>();
            //ImageView imageView  = new ImageView(activity);
            //
            //mImages.add(new CustomObject(mImages.get(0).getParseObject(),imageView));
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
        if(mImages.get(position).get("mPicture")!=null){ //&& !mImages.get(position).get("isBanner").equals("banner") ) {// && mImages.get(position).getImageView() == null){
            bannerViewListener.onImageShown();
            ParseFile applicantResume = (ParseFile) mImages.get(position).get("mPicture");
            //  applicantResume.getUrl();
            applicantResume.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        final Bitmap bmp;
                        try {
                            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            mImage.setImageBitmap(bmp);

                        } catch (OutOfMemoryError er) {
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
//     if(mImages.get(position).get("isBanner").equals("banner")){
//         bannerViewListener.onBannerShown();
//         progressBar.setVisibility(View.VISIBLE);
//         final AdView mAdView = (AdView) root.findViewById(R.id.adView);
//         mAdView.setVisibility(View.VISIBLE);
//         mImage.setVisibility(View.GONE);
//         AdRequest adRequest = new AdRequest.Builder().build();
//         mAdView.loadAd(adRequest);
//
//         mAdView.setAdListener(new AdListener() {
//             @Override
//             public void onAdClosed() {
//                 super.onAdClosed();
//             }
//
//             @Override
//             public void onAdFailedToLoad(int errorCode) {
//                 super.onAdFailedToLoad(errorCode);
//             }
//
//             @Override
//             public void onAdLeftApplication() {
//                 super.onAdLeftApplication();
//             }
//
//             @Override
//             public void onAdOpened() {
//                 super.onAdOpened();
//             }
//
//             @Override
//             public void onAdLoaded() {
//                 super.onAdLoaded();
//                 progressBar.setVisibility(View.GONE);
//             }
//         });
  //   }

        mImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.fullScreenTouch(1);

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

//
        //mImages.notifyAll();
    }

//    public void getMoreNotSeenPhotos(List<ParseObject> parseObjects){
//        mImages = parseObjects;
//    }
//
//    public void insertTopPictures(List<ParseObject> parseObjects){
//        mImages = parseObjects;
//    }
}
