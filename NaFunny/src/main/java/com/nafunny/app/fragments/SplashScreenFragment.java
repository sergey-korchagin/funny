package com.nafunny.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.nafunny.app.R;
import com.nafunny.app.adapters.PhotoPagerAdapter;
import com.nafunny.app.managers.TinyDB;
import com.nafunny.app.utils.Constants;
import com.nafunny.app.utils.Utils;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 30/11/2015.
 */
public class SplashScreenFragment extends Fragment {

    FrameLayout frameLayout;
    Thread thread;
    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
    TinyDB tinydb;
    ArrayList<String> seenItems;
    int querySize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.splash_screen_fragment, container, false);
        tinydb = new TinyDB(getActivity());

        seenItems = new ArrayList<>();
        ImageView sunImageView = (ImageView) root.findViewById(R.id.imageView);
        Animation sunRiseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

        sunRiseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                boolean t = tinydb.getBoolean(Constants.LAST_ON_CONNECTION);
                if (!t) {
                    getQuerySize();

                }else{
                    PicturesMainFragment picturesMainFragment = new PicturesMainFragment();
                    Utils.replaceFragment(getFragmentManager(), android.R.id.content, picturesMainFragment, false);
                }


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        sunImageView.startAnimation(sunRiseAnimation);


        return root;
    }



    public void getQuerySize() {

        ParseQuery query = new ParseQuery("picture");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                querySize = count;

                ParseQuery query = new ParseQuery("picture");
                query.addDescendingOrder("createdAt");
                query.setLimit(querySize);
                query.findInBackground(new FindCallback() {
                    @Override
                    public void done(List objects, ParseException e) {
                    }

                    @Override
                    public void done(Object o, Throwable throwable) {
                        if (o instanceof List) {
                            categories = (List<ParseObject>) o;
                            for (int i = 0; i < categories.size(); i++) {
                                seenItems.add(categories.get(i).getObjectId().toString());
                            }
                            tinydb.putBoolean(Constants.LAST_ON_CONNECTION, true);
                            tinydb.putListString(Constants.SEEN_LIST, seenItems);

                            PicturesMainFragment picturesMainFragment = new PicturesMainFragment();
                            Utils.replaceFragment(getFragmentManager(), android.R.id.content, picturesMainFragment, false);
                        }

                    }
                });
            }
        });
    }
}
