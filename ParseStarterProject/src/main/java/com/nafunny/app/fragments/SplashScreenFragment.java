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

import com.parse.ParseObject;
import com.nafunny.app.R;
import com.nafunny.app.adapters.PhotoPagerAdapter;
import com.nafunny.app.managers.TinyDB;
import com.nafunny.app.utils.Constants;
import com.nafunny.app.utils.Utils;

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
    List<String> seenItems;
    int notSeenCounter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.splash_screen_fragment, container, false);
        tinydb = new TinyDB(getActivity());
        seenItems = tinydb.getListString(Constants.SEEN_LIST);

//        thread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (this) {
//                        wait(2000);
//                 ;
//                    }
//                } catch (InterruptedException ex) {
//                }

        Constants.FROM_SETTINGS = false;

        ImageView sunImageView = (ImageView)root.findViewById(R.id.imageView);
                // Анимация для восхода солнца
                Animation sunRiseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

                sunRiseAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        PicturesMainFragment picturesMainFragment = new PicturesMainFragment();
                        Utils.replaceFragment(getFragmentManager(), android.R.id.content, picturesMainFragment, false);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                // Подключаем анимацию к нужному View
                sunImageView.startAnimation(sunRiseAnimation);

//



//            }
//        };

      //  thread.start();
        return root;
    }
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        ((MainActivity) activity).hideActionbar();
//
//    }


}
