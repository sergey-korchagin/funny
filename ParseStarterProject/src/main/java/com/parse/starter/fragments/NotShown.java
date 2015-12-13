package com.parse.starter.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;
import com.parse.starter.adapters.PhotoPagerAdapter;
import com.parse.starter.interfaces.CustomTouchListener;
import com.parse.starter.managers.TinyDB;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.ShortcutBadger;
import com.parse.starter.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 07/12/2015.
 */
public class NotShown extends Fragment implements CustomTouchListener, ViewPager.OnPageChangeListener , View.OnClickListener{

    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
    CustomTouchListener customTouchListener;
    ProgressDialog progressDialog;
    ArrayList<String> seenItemList;
    TinyDB tinydb;
    String SAVED_LIST = "saved_list";
    ImageView toAllBtn;
    FrameLayout errorLayout;
    TextView noInternetText;
    int notSeenCounter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.not_shown_fragment, container, false);
        intiReciever();
        errorLayout = (FrameLayout)root.findViewById(R.id.errorLayout);
        noInternetText = (TextView)root.findViewById(R.id.no_internet_text);
        noInternetText.setOnClickListener(this);
        tinydb = new TinyDB(getActivity());
        toAllBtn = (ImageView)root.findViewById(R.id.btnAll);
        toAllBtn.setOnClickListener(this);
        seenItemList=tinydb.getListString(Constants.SEEN_LIST);
        notSeenCounter = tinydb.getInt(Constants.SEEN_ITEMS_COUNTER);

        progressDialog = ProgressDialog.show(getActivity(), "", "Картинки загружаются...");
        mPager = (ViewPager) root.findViewById(R.id.photos_image_pager);
        mPager.addOnPageChangeListener(this);
        getCategories();
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        ShortcutBadger.with(getActivity()).count(notSeenCounter);

    }

    @Override
    public void onPause() {
        super.onPause();
        ShortcutBadger.with(getActivity()).count(notSeenCounter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ShortcutBadger.with(getActivity()).count(notSeenCounter);

    }

    public void getCategories() {

        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("createdAt");
        query.whereNotContainedIn("objectId",seenItemList);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                    categories = (List<ParseObject>) o;
                    if(categories.size() == 0)
                    {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(true).setMessage("К сожалению нету новых картинок но они обязательно появятся!!")
                                .setPositiveButton("Назад на главную", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PicturesMainFragment notShown = new PicturesMainFragment();
                                        Utils.replaceFragment(getFragmentManager(), android.R.id.content, notShown, false);
                                    }
                                });

                        AlertDialog alert = builder.create();
                        Window window = alert.getWindow();
                        window.setGravity(Gravity.CENTER);
                        alert.show();
                    }else {
                        mAdapter = new PhotoPagerAdapter(categories, getActivity(), customTouchListener);
                        mPager.setAdapter(mAdapter);

                        if (!seenItemList.contains(categories.get(0).getObjectId())) {
                            seenItemList.add(categories.get(0).getObjectId());
                            notSeenCounter--;
                        }
                        progressDialog.dismiss();
                    }


                }
            }
        });
    }

    @Override
    public void fullScreenTouch(int t) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }

    @Override
    public void onPageSelected(int position) {
        if (!seenItemList.contains(categories.get(position).getObjectId())) {
            seenItemList.add(categories.get(position).getObjectId());
            notSeenCounter--;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == toAllBtn.getId()){
            tinydb.putInt(Constants.SEEN_ITEMS_COUNTER,notSeenCounter);
            tinydb.putListString(Constants.SEEN_LIST,seenItemList);
            PicturesMainFragment notShown = new PicturesMainFragment();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, notShown, false);

        }
        else if(noInternetText.getId() == v.getId()){
            Intent intent=new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        }
    }


    private  void intiReciever(){
        getActivity().registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (isDataConnected()) {
                    // Toast.makeText( context, "Active Network Type : connected", Toast.LENGTH_SHORT ).show();
                    errorLayout.setVisibility(View.GONE);
                    mPager.setVisibility(View.VISIBLE);

                } else {

                    errorLayout.setVisibility(View.VISIBLE);
                    mPager.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

    }
    private boolean isDataConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
