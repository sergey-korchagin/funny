package com.parse.starter.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.starter.MainActivity;
import com.parse.starter.R;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.Utils;

/**
 * Created by User on 30/11/2015.
 */
public class SplashScreenFragment extends Fragment {

    FrameLayout frameLayout;
    Thread thread;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.splash_screen_fragment, container, false);
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
