package com.nafunny.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nafunny.app.adapters.PhotoPagerAdapter;
import com.nafunny.app.interfaces.CustomTouchListener;
import com.nafunny.app.utils.ShortcutBadger;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.nafunny.app.R;
import com.nafunny.app.interfaces.BannerViewListener;
import com.nafunny.app.managers.AnalyticsManager;
import com.nafunny.app.managers.TinyDB;
import com.nafunny.app.utils.Constants;
import com.nafunny.app.utils.Utils;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 30/11/2015.
 */
public class PicturesMainFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener, CustomTouchListener, BannerViewListener, AdapterView.OnItemSelectedListener {
    //Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
    // Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);

    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
    List<ParseObject> updatedCategories;
    int skip = 0;
    int querySize;
    public int mPosition;
    ImageView btnShare;
    ImageView btnTop;
    ImageView btnLike;
    ImageView btnMore;
    ImageView btnNtShown;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    ImageView mSmallImage;
    ArrayList<String> likesList;
    TinyDB tinydb;
    TextView likesCounterView;
    String SAVED_LIST = "saved_list";
    ProgressDialog progressDialog;
    ImageView btnUpload;
    private final int REQUEST_CODE_FROM_GALLERY_IMAGE = 1;
    LinearLayout menuLayout;
    CustomTouchListener customTouchListener;
    ArrayList<String> seenItemsLIst;
    FrameLayout errorLayout;
    LinearLayout mainLayout;
    int notSeenCounter;
    TextView btnSendUsImage;
    TextView btnInviteFriend;
    TextView btnSaveImage;
    TextView btnPushState;
    RelativeLayout mainRelative;
    LinearLayout topLayout;
    RelativeLayout bottomLayout;
    TextView picNumber;
    TextView allPicNumber;
    LinearLayout counterLayout;
    ImageView pushMenuImage;
    InterstitialAd mInterstitialAd;
    BannerViewListener bannerViewListener;
    BroadcastReceiver receiver;
    boolean isRegistered;
    Spinner spinner;
    boolean isSpinnerTouched = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.pictures_main_fragment, container, false);
        Constants.FROM_SETTINGS = false;
        tinydb = new TinyDB(getActivity());

        progressDialog = ProgressDialog.show(getActivity(), "", "Картинки загружаются...");

        errorLayout = (FrameLayout) root.findViewById(R.id.errorLayout);
        mainRelative = (RelativeLayout) root.findViewById(R.id.mainRelativeLayout);
        topLayout = (LinearLayout) root.findViewById(R.id.topLayout);
        bottomLayout = (RelativeLayout) root.findViewById(R.id.bottomLayout);
        counterLayout = (LinearLayout) root.findViewById(R.id.counterLayout);
        picNumber = (TextView) root.findViewById(R.id.picNumber);
        allPicNumber = (TextView) root.findViewById(R.id.allPics);


        mainLayout = (LinearLayout) root.findViewById(R.id.mainLinearLayout);
        menuLayout = (LinearLayout) root.findViewById(R.id.menuLayout);
        menuLayout.setVisibility(View.GONE);
        btnSendUsImage = (TextView) root.findViewById(R.id.sendUsPictre);
        btnSendUsImage.setOnClickListener(this);




        btnInviteFriend = (TextView) root.findViewById(R.id.inviteFriend);
        btnInviteFriend.setOnClickListener(this);

        btnPushState = (TextView) root.findViewById(R.id.enablePush);
        btnPushState.setOnClickListener(this);

        pushMenuImage = (ImageView)root.findViewById(R.id.pushMenuIcon);
        btnSaveImage = (TextView) root.findViewById(R.id.savePicture);
        btnSaveImage.setOnClickListener(this);

        if (tinydb.getInt(Constants.PUSH_INDICATOR) != 1) {
            btnPushState.setText("Отключить Push уведомления");
            pushMenuImage.setBackground(getResources().getDrawable(R.drawable.iaiconsmenu01_nonews_f));

        } else {
            btnPushState.setText("Включить Push уведомления");
            pushMenuImage.setBackground(getResources().getDrawable(R.drawable.iaiconsmenu01_yesnews_f));

        }
        intiReciever();
        seenItemsLIst = new ArrayList<>();


        likesList = new ArrayList<>();
        likesList = tinydb.getListString(SAVED_LIST);
        likesCounterView = (TextView) root.findViewById(R.id.likesCounter);
        customTouchListener = this;
        bannerViewListener = this;


        btnShare = (ImageView) root.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);


        btnTop = (ImageView) root.findViewById(R.id.btnTop);
        btnTop.setOnClickListener(this);

        btnLike = (ImageView) root.findViewById(R.id.btnLike);
        //btnLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.like_tmp));

        btnLike.setOnClickListener(this);

        btnNtShown = (ImageView) root.findViewById(R.id.btnNotSeen);
        btnNtShown.setOnClickListener(this);


        btnMore = (ImageView) root.findViewById(R.id.btnMore);
        btnMore.setOnClickListener(this);


        mPager = (ViewPager) root.findViewById(R.id.photos_image_pager);
        mPager.addOnPageChangeListener(this);

        mSmallImage = (ImageView) root.findViewById(R.id.smallImage);

        spinner = (Spinner) root.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.ImageType, R.layout.custom_spinner);
        adapter1.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter1);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });

        initInterestitial();
        initSmallImage();
        checkIfStorageAvailable();
        getQuerySize();


        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (menuLayout.getVisibility() == View.VISIBLE) {
                    Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
                    menuLayout.setVisibility(View.GONE);
                    menuLayout.setAnimation(animFadeOut);
                }

                return false;
            }
        });
        getCategories();

        //mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        AnalyticsManager.getInstance().sendScreenEvent(AnalyticsManager.SCREEN_ALL);
        return root;
    }


    public void initInterestitial(){
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.interestial));//"ca-app-pub-4305158415143864/7682225439");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

            }
        });

        requestNewInterstitial();
    }


    public void initLikeButton() {
        if (isAdded()) {
            if (likesList.contains(categories.get(mPosition).getObjectId())) {
                btnLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.iaicons_like_f_act));
            } else {
                btnLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.iaicons_like_f_sttc));
            }
        }

    }


    public void getQuerySize() {

        ParseQuery query = new ParseQuery("picture");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                querySize = count;
                seenItemsLIst = tinydb.getListString(Constants.SEEN_LIST);
                notSeenCounter = querySize - seenItemsLIst.size();

                if (notSeenCounter <= 0) {
                    notSeenCounter = 0;
                    btnNtShown.setVisibility(View.GONE);
                }
                String pics = "/" + querySize;
                allPicNumber.setText(pics);

            }
        });


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(notSeenCounter <= 0){
            notSeenCounter = 0;
            btnNtShown.setVisibility(View.GONE);
        }

        int tt = position+1;// (querySize - ((int) categories.get(position).get("pictureNum"))) + 1;
        String tmp = String.valueOf(tt);
        picNumber.setText(tmp);

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
        if (categories.size() >= position) {
            likesCounterView.setText(Integer.toString((Integer) categories.get(mPosition).get("likes")));
            if(categories.get(position).get("isBanner").equals("banner")){
                btnLike.setVisibility(View.INVISIBLE);
                likesCounterView.setVisibility(View.INVISIBLE);
                btnMore.setClickable(false);
                btnShare.setClickable(false);
            }
            else{
                btnLike.setVisibility(View.VISIBLE);
                likesCounterView.setVisibility(View.VISIBLE);
                btnMore.setClickable(true);
                btnShare.setClickable(true);


            }
        }
        String t = categories.get(mPosition).getObjectId();
        if (!seenItemsLIst.contains(t)) {
            seenItemsLIst.add(t);
            notSeenCounter--;
        }
        if(position == 6 || position == 55 || position == 150){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

           // AdBuddiz.showAd(getActivity()); // this = current Activity

        }
        if(position == 15 || position == 58)
        {
            AdBuddiz.showAd(getActivity()); // this = current Activity

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


                    mAdapter = new PhotoPagerAdapter(categories, getActivity(), customTouchListener, bannerViewListener);
                    mPager.setAdapter(mAdapter);
                    likesCounterView.setText(Integer.toString((Integer) categories.get(0).get("likes")));
                    if (!seenItemsLIst.contains(categories.get(0).getObjectId())) {
                        seenItemsLIst.add(categories.get(0).getObjectId());
                        notSeenCounter--;

                    }
                    String tmp = String.valueOf(1);
                    picNumber.setText(tmp);
                    initLikeButton();
                    progressDialog.dismiss();

                }
            }
        });
    }

    @Override
    public void onResume() {
        spinner.setOnItemSelectedListener(this);

        btnSendUsImage.setOnClickListener(this);
        btnInviteFriend.setOnClickListener(this);
        btnPushState.setOnClickListener(this);
        btnSaveImage.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnTop.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        btnNtShown.setOnClickListener(this);
        btnMore.setOnClickListener(this);

        getQuerySize();
        likesList = tinydb.getListString(SAVED_LIST);
        seenItemsLIst = tinydb.getListString(Constants.SEEN_LIST);
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if (btnShare.getId() == v.getId()) {
            if (mExternalStorageAvailable && mExternalStorageWriteable) {
                sendShareIntentPhoto(mPosition);
            } else {
                sendShareIntentLink(mPosition);
            }
        } else if (btnSaveImage.getId() == v.getId()) {
            menuLayout.setVisibility(View.GONE);
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


        } else if (btnTop.getId() == v.getId()) {
//            tinydb.putInt(Constants.SEEN_ITEMS_COUNTER,notSeenCounter);
//            TopFragment topFragment = new TopFragment();
//            Utils.replaceFragment(getFragmentManager(), android.R.id.content, topFragment, true);
//            if (mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show();
//            } else {
                tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);
            tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);

            TopFragment topFragment = new TopFragment();
                Utils.replaceFragment(getFragmentManager(), android.R.id.content, topFragment, true);
//            }

        } else if (btnLike.getId() == v.getId()) {
            if (!likesList.contains(categories.get(mPosition).getObjectId())) {
                incrementLikes();
                int d1 = ((Integer) categories.get(mPosition).get("likes"));
                d1 = d1--;
                likesCounterView.setText(Integer.toString(d1));
            } else {
                decrementLikes();
                int d1 = ((Integer) categories.get(mPosition).get("likes"));
                d1 = d1--;
                likesCounterView.setText(Integer.toString(d1));
            }
        } else if (btnMore.getId() == v.getId()) {
            //  enableAlertMenu();
            if (menuLayout.getVisibility() == View.GONE) {
                Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
                menuLayout.setVisibility(View.VISIBLE);
                menuLayout.setAnimation(animFadeIn);
            } else {
                Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
                menuLayout.setVisibility(View.GONE);
                menuLayout.setAnimation(animFadeOut);
            }
        } else if (btnNtShown.getId() == v.getId()) {
            tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);
            tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);
            NotShown notShown = new NotShown();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, notShown, false);

        } else if (btnSendUsImage.getId() == v.getId()) {
            menuLayout.setVisibility(View.GONE);
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_CODE_FROM_GALLERY_IMAGE);
        } else if (btnInviteFriend.getId() == v.getId()) {
            menuLayout.setVisibility(View.GONE);
//            Intent share = new Intent(Intent.ACTION_SEND);
//            share.setType("text/plain");
//            share.putExtra(Intent.EXTRA_TEXT, "когда нибудь это будет приглашение в нафаню, а пока качни айдаприкол https://play.google.com/store/apps/details?id=ru.idaprikol&hl=ru");
//            startActivity(Intent.createChooser(share, "Пригласить"));

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "NaFunny");
            String sAux = "\nРекомендую! Нафанни\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.nafunny.app \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Пригласить!"));
        } else if (btnPushState.getId() == v.getId()) {
            menuLayout.setVisibility(View.GONE);
            if (tinydb.getInt(Constants.PUSH_INDICATOR) != 1) {
                try {
                    btnPushState.setText("Включить Push уведомления");
                    pushMenuImage.setBackground(getResources().getDrawable(R.drawable.iaiconsmenu01_yesnews_f));
                    tinydb.putInt(Constants.PUSH_INDICATOR, 1);
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.removeAll("channels", Arrays.asList("photos"));
                    installation.saveInBackground();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            } else {
                btnPushState.setText("Отключить Push уведомления");
                pushMenuImage.setBackground(getResources().getDrawable(R.drawable.iaiconsmenu01_nonews_f));

                tinydb.putInt(Constants.PUSH_INDICATOR, 0);
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.addAllUnique("channels", Arrays.asList("photos"));
                installation.saveInBackground();
            }
        }
    }


    public void decrementLikes() {
        categories.get(mPosition).increment("likes", -1);
        categories.get(mPosition).saveInBackground();
        likesList.remove(categories.get(mPosition).getObjectId());
        initLikeButton();

    }

    public void incrementLikes() {
        categories.get(mPosition).increment("likes");
        categories.get(mPosition).saveInBackground();
        likesList.add(categories.get(mPosition).getObjectId());
        initLikeButton();

    }

    public void sendShareIntentLink(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true).setMessage("No enough storage! But you still can share link to picture!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (categories.get(position).get("mPicture") != null) {
                            ParseFile applicantResume = (ParseFile) categories.get(position).get("mPicture");
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");

                            shareIntent.putExtra(Intent.EXTRA_TEXT, applicantResume.getUrl() + "\n" + getResources().getString(R.string.share_image_text));
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
                                                            share.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.share_image_text));

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


    public void checkIfStorageAvailable() {
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

    public void initSmallImage() {
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

    public void savePicture() {
        if (mExternalStorageWriteable && mExternalStorageAvailable) {
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
        } else {
            Utils.showAlert(getActivity(), "Error", "No external storage available");
        }
    }


    @Override
    public void onPause() {
        btnSendUsImage.setOnClickListener(null);
        btnInviteFriend.setOnClickListener(null);
        btnPushState.setOnClickListener(null);
        btnSaveImage.setOnClickListener(null);
        btnShare.setOnClickListener(null);
        btnTop.setOnClickListener(null);
        btnLike.setOnClickListener(null);
        btnNtShown.setOnClickListener(null);
        btnMore.setOnClickListener(null);
        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);
        tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);


        super.onPause();

    }

    @Override
    public void onStop() {

        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);
        tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);
        if(isAdded()){
            Activity activity = getActivity();
            if(activity!=null){
               // ShortcutBadger.with(getActivity()).count(notSeenCounter);
                ShortcutBadger.with(getActivity()).count(0);
            }
        }
        if(receiver!=null && isRegistered){
            getActivity().unregisterReceiver(receiver);
            isRegistered = false;

        }
        super.onStop();



    }

    @Override
    public void onDestroy() {
        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);
        tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);
        if(isAdded()){
            if(getActivity()!=null){
                //ShortcutBadger.with(getActivity()).count(notSeenCounter);
                ShortcutBadger.with(getActivity()).count(0);
            }        }
        if(receiver!=null && isRegistered){
            getActivity().unregisterReceiver(receiver);
            isRegistered = false;

        }
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_FROM_GALLERY_IMAGE) {

                Uri selectedImage = data.getData();
                if (!ifVideo(selectedImage)) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.support_mail)});
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


    public boolean ifVideo(Uri uri) {
        if (uri.toString().contains("video")) {
            return true;
        }
        return false;
    }


    public void enableAlertMenu() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        //    builderSingle.setIcon(R.drawable.ic_launcher);
        //builderSingle.setTitle("Дополнительные возможности");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.dialog_list_item);
        arrayAdapter.add("Послать нам свой прикол");
        arrayAdapter.add("Пригласить друга");
        if (tinydb.getInt(Constants.PUSH_INDICATOR) != 1) {
            arrayAdapter.add("Отключить Push уведомления");
        } else {
            arrayAdapter.add("Включить Push уведомления");

        }


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
//                                SettingsFragment settingsFragment = new SettingsFragment();
//                                Utils.replaceFragment(getFragmentManager(), android.R.id.content, settingsFragment, true);
                                if (tinydb.getInt(Constants.PUSH_INDICATOR) != 1) {
                                    try {
                                        tinydb.putInt(Constants.PUSH_INDICATOR, 1);
                                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                        installation.removeAll("channels", Arrays.asList("photos"));
                                        installation.saveInBackground();
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    tinydb.putInt(Constants.PUSH_INDICATOR, 0);
                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    installation.addAllUnique("channels", Arrays.asList("photos"));
                                    installation.saveInBackground();
                                }

                                break;
                        }

                    }
                });
        builderSingle.show();
    }


    @Override
    public void fullScreenTouch(int touch) {
//        switch (touch) {
//            case 1:
        Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
        if (topLayout.getVisibility() == View.VISIBLE) {
            topLayout.setVisibility(View.GONE);
            topLayout.setAnimation(animFadeOut);
            bottomLayout.setVisibility(View.GONE);
            bottomLayout.setAnimation(animFadeOut);
            counterLayout.setVisibility(View.GONE);
            counterLayout.setAnimation(animFadeOut);
        } else {
            topLayout.setVisibility(View.VISIBLE);
            topLayout.setAnimation(animFadeIn);
            bottomLayout.setVisibility(View.VISIBLE);
            bottomLayout.setAnimation(animFadeIn);
            counterLayout.setVisibility(View.VISIBLE);
            counterLayout.setAnimation(animFadeIn);
        }
        if (menuLayout.getVisibility() == View.VISIBLE) {
            menuLayout.setVisibility(View.GONE);
            menuLayout.setAnimation(animFadeOut);
        }
    }


    private void intiReciever() {

         receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (isDataConnected()) {
                    // Toast.makeText( context, "Active Network Type : connected", Toast.LENGTH_SHORT ).show();
                    errorLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    //getCategories();
                } else {
                    mainLayout.setVisibility(View.INVISIBLE);
                    errorLayout.setVisibility(View.VISIBLE);

                    progressDialog.dismiss();
                }
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        isRegistered = true;
    }

    private boolean isDataConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
           topLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            counterLayout.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            topLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            counterLayout.setVisibility(View.VISIBLE);}
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("43BC114261963850F5B021A66F5A07FB")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    public void onBannerShown() {

    }

    @Override
    public void onImageShown() {
       ;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) parent.getChildAt(0)).setText(getResources().getStringArray(R.array.ImageType)[position]);
        ((TextView) parent.getChildAt(0)).setTextSize(20);
        SelectedCategoryFragment selectedCategoryFragment = new SelectedCategoryFragment();
        if (isSpinnerTouched){
            switch (position) {

            case 0:
//                Constants.SELECTED_SCREEN = "All";
//                Constants.SELECTED_SCREEN_NUMBER = 0;
//                PicturesMainFragment picturesMainFragment = new PicturesMainFragment();
//                Utils.replaceFragment(getFragmentManager(), android.R.id.content, picturesMainFragment, false);
//                isSpinnerTouched = false;

           //     break;
                case 1:
                    Constants.SELECTED_SCREEN = "Pets";
                    Constants.SELECTED_SCREEN_NUMBER = 1;
                    Utils.replaceFragment(getFragmentManager(), android.R.id.content, selectedCategoryFragment, false);
                    isSpinnerTouched = false;
                    break;
                case 2:
                    Constants.SELECTED_SCREEN = "People";
                    Constants.SELECTED_SCREEN_NUMBER = 2;
                    Utils.replaceFragment(getFragmentManager(), android.R.id.content, selectedCategoryFragment, false);
                    isSpinnerTouched = false;
                    break;
                case 3:
                    Constants.SELECTED_SCREEN = "Jokes";
                    Constants.SELECTED_SCREEN_NUMBER = 3;
                    Utils.replaceFragment(getFragmentManager(), android.R.id.content, selectedCategoryFragment, false);
                    isSpinnerTouched = false;
                    break;
                case 4:
                    Constants.SELECTED_SCREEN = "Funny images";
                    Constants.SELECTED_SCREEN_NUMBER = 4;
                    Utils.replaceFragment(getFragmentManager(), android.R.id.content, selectedCategoryFragment, false);
                    isSpinnerTouched = false;
                    break;
                case 5:
                    Constants.SELECTED_SCREEN = "Slang";
                    Constants.SELECTED_SCREEN_NUMBER = 5;
                    Utils.replaceFragment(getFragmentManager(), android.R.id.content, selectedCategoryFragment, false);
                    isSpinnerTouched = false;
                    break;
                case 6:
                    Constants.SELECTED_SCREEN = "Interesting facts";
                    Constants.SELECTED_SCREEN_NUMBER = 6;
                    Utils.replaceFragment(getFragmentManager(), android.R.id.content, selectedCategoryFragment, false);
                    isSpinnerTouched = false;
                    ((TextView) parent.getChildAt(0)).setText(getResources().getStringArray(R.array.ImageType)[position]);
                    break;
            }
    }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
