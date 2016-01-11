package com.parse.starter.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;
import com.parse.starter.adapters.PhotoPagerAdapter;
import com.parse.starter.interfaces.BannerViewListener;
import com.parse.starter.interfaces.CustomTouchListener;
import com.parse.starter.managers.AnalyticsManager;
import com.parse.starter.managers.TinyDB;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.ShortcutBadger;
import com.parse.starter.utils.Utils;

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
 * Created by serge_000 on 04/12/2015.
 */
public class NotShown extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener,
        CustomTouchListener, BannerViewListener {
    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
   List<ParseObject> updatedCategories;
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
    LinearLayout menuLayout;
    ArrayList<String> likesList;
    String SAVED_LIST = "saved_list";
    ImageView btnMore;
    ArrayList<String> seenItemsLIst;
    BannerViewListener bannerViewListener;
    TextView btnSendUsImage;
    TextView btnInviteFriend;
    TextView btnSaveImage;
    TextView btnPushState;
    ImageView btnNtShown;
    TinyDB tinydb;
    private final int REQUEST_CODE_FROM_GALLERY_IMAGE = 1;
    int notSeenCounter;
    ProgressDialog progressDialog;
    int skip = 0;
    LinearLayout topLayout;
    RelativeLayout bottomLayout;
    LinearLayout counterLayout;
    TextView picNumber;
    TextView allPicNumber;
    int querySize;
    ImageView pushMenuImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.not_shown_fragment, container, false);
        Constants.FROM_SETTINGS = false;
        progressDialog = ProgressDialog.show(getActivity(), "", "Картинки загружаются...");
        picNumber = (TextView) root.findViewById(R.id.picNumber);
        allPicNumber = (TextView) root.findViewById(R.id.allPics);

        seenItemsLIst = new ArrayList<>();
        btnShare = (ImageView) root.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);

        tinydb = new TinyDB(getActivity());
        seenItemsLIst = tinydb.getListString(Constants.SEEN_LIST);
        topLayout = (LinearLayout) root.findViewById(R.id.topLayout);
        bottomLayout = (RelativeLayout) root.findViewById(R.id.bottomLayout);
        counterLayout = (LinearLayout) root.findViewById(R.id.counterLayout);

        notSeenCounter = tinydb.getInt(Constants.SEEN_ITEMS_COUNTER);
        btnMore = (ImageView) root.findViewById(R.id.btnMore);
        btnMore.setOnClickListener(this);

        btnAll = (ImageView) root.findViewById(R.id.btnTop);
        btnAll.setOnClickListener(this);

        btnNtShown = (ImageView) root.findViewById(R.id.btnNotSeen);
        btnNtShown.setOnClickListener(this);
        if (notSeenCounter == 0) {
            btnNtShown.setVisibility(View.GONE);
        }
        menuLayout = (LinearLayout) root.findViewById(R.id.menuLayout);
        menuLayout.setVisibility(View.GONE);
        btnSendUsImage = (TextView) root.findViewById(R.id.sendUsPictre);
        btnSendUsImage.setOnClickListener(this);

        btnInviteFriend = (TextView) root.findViewById(R.id.inviteFriend);
        btnInviteFriend.setOnClickListener(this);

        btnPushState = (TextView) root.findViewById(R.id.enablePush);
        btnPushState.setOnClickListener(this);

        btnSaveImage = (TextView) root.findViewById(R.id.savePicture);
        btnSaveImage.setOnClickListener(this);
        pushMenuImage = (ImageView) root.findViewById(R.id.pushMenuIcon);

        if (tinydb.getInt(Constants.PUSH_INDICATOR) != 1) {
            btnPushState.setText("Отключить Push уведомления");
            pushMenuImage.setBackground(getResources().getDrawable(R.drawable.iaiconsmenu01_nonews_f));

        } else {
            btnPushState.setText("Включить Push уведомления");
            pushMenuImage.setBackground(getResources().getDrawable(R.drawable.iaiconsmenu01_yesnews_f));

        }
        btnLike = (ImageView) root.findViewById(R.id.btnLike);
        btnLike.setOnClickListener(this);

        likesList = new ArrayList<>();
        likesList = tinydb.getListString(SAVED_LIST);


        mPager = (ViewPager) root.findViewById(R.id.photos_image_pager);
        mPager.addOnPageChangeListener(this);

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

        mSmallImage = (ImageView) root.findViewById(R.id.smallImage);
        likesCounterView = (TextView) root.findViewById(R.id.likesCounter);

        customTouchListener = this;
        bannerViewListener = this;
        initSmallImage();
        checkIfStorageAvailable();
        getQuerySize();
        getCategories();
        AnalyticsManager.getInstance().sendScreenEvent(AnalyticsManager.SCREEN_NEW);

        return root;
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


    public void getCategories() {

        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("createdAt");
        query.setLimit(5);
        query.whereNotContainedIn("objectId", seenItemsLIst);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                  categories = (List<ParseObject>) o;
                    for(int i = 0; i<((List<ParseObject>) o).size();i++){
                        seenItemsLIst.add(((List<ParseObject>) o).get(i).getObjectId());
                    }
//                    List<CustomObject> co = new ArrayList<CustomObject>();
//                    for (int i = 0; i<((List) o).size();i++){
//                        co.add(new CustomObject(categories.get(i),null));
//                    }
                    if (categories.size() == 0) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(true).setMessage("К сожалению нет новых картинок но они обязательно появятся!!")
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
                    } else {
                        mAdapter = new PhotoPagerAdapter(categories, getActivity(), customTouchListener, bannerViewListener);
                        mPager.setAdapter(mAdapter);
                        likesCounterView.setText(Integer.toString((Integer) categories.get(0).get("likes")));

                        if (!seenItemsLIst.contains(categories.get(0).getObjectId())) {
                            seenItemsLIst.add(categories.get(0).getObjectId());
                            notSeenCounter--;
                        }
                        picNumber.setText(String.valueOf(1));
                        initLikeButton();
                        progressDialog.dismiss();
                    }


                }
            }
        });
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

    @Override
    public void onClick(View v) {
        if (btnAll.getId() == v.getId()) {
            tinydb.putListString(SAVED_LIST, likesList);
            PicturesMainFragment picturesMainFragment = new PicturesMainFragment();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, picturesMainFragment, false);

//                    }
//                }
//            });
        }
        if (btnShare.getId() == v.getId()) {
            if (mExternalStorageAvailable && mExternalStorageWriteable) {
                sendShareIntentPhoto(mPosition);
            } else {
                sendShareIntentLink(mPosition);
            }
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
        } else if (btnSendUsImage.getId() == v.getId()) {
            menuLayout.setVisibility(View.GONE);

            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_CODE_FROM_GALLERY_IMAGE);
        } else if (btnInviteFriend.getId() == v.getId()) {
            menuLayout.setVisibility(View.GONE);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "когда нибудь это будет приглашение в нафаню, а пока качни айдаприкол https://play.google.com/store/apps/details?id=ru.idaprikol&hl=ru");
            startActivity(Intent.createChooser(share, "Пригласить"));
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


        } else if (btnNtShown.getId() == v.getId()) {
            tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);
            tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);
            TopFragment top = new TopFragment();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, top, false);
        }
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


    public boolean ifVideo(Uri uri) {
        if (uri.toString().contains("video")) {
            return true;
        }
        return false;
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        if(position%5==0){

            ParseQuery query = new ParseQuery("picture");
            query.addDescendingOrder("createdAt");
            query.setLimit(5);
            query.whereNotContainedIn("objectId", seenItemsLIst);
            query.findInBackground(new FindCallback() {
                @Override
                public void done(List objects, ParseException e) {
                }

                @Override
                public void done(Object o, Throwable throwable) {
                    if (o instanceof List) {
                        updatedCategories = (List<ParseObject>) o;
//                    List<CustomObject> co = new ArrayList<CustomObject>();
//                    for (int i = 0; i<((List) o).size();i++){
//                        co.add(new CustomObject(categories.get(i),null));
//                    }
                        if (updatedCategories.size() == 0) {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setCancelable(true).setMessage("К сожалению нет новых картинок но они обязательно появятся!!")
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
                        } else {
                            mAdapter.getMorePhotos(updatedCategories, querySize);
                            mAdapter.notifyDataSetChanged();
                            likesCounterView.setText(Integer.toString((Integer) categories.get(0).get("likes")));

                            if (!seenItemsLIst.contains(categories.get(0).getObjectId())) {
                                seenItemsLIst.add(categories.get(0).getObjectId());
                                notSeenCounter--;
                            }
                            picNumber.setText(String.valueOf(1));
                            initLikeButton();
                            progressDialog.dismiss();
                        }


                    }
                }
            });
        }

        if(position%5==1){
            ParseQuery query = new ParseQuery("picture");
            query.addDescendingOrder("createdAt");
            query.setLimit(5);
            query.whereNotContainedIn("objectId", seenItemsLIst);
            query.findInBackground(new FindCallback() {
                @Override
                public void done(List objects, ParseException e) {
                }

                @Override
                public void done(Object o, Throwable throwable) {
                    if (o instanceof List) {
                        updatedCategories = (List<ParseObject>) o;
                        for(int i = 0; i<((List<ParseObject>) o).size();i++){
                            seenItemsLIst.add(((List<ParseObject>) o).get(i).getObjectId());
                        }
                        if (updatedCategories.size() == 0) {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setCancelable(true).setMessage("К сожалению нет новых картинок но они обязательно появятся!!")
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
                        } else {
                            mAdapter.getMorePhotos(updatedCategories, querySize);
                            mAdapter.notifyDataSetChanged();

                        }


                    }
                }
            });
        }


        if (categories != null) {
            likesCounterView.setText(Integer.toString((Integer) categories.get(mPosition).get("likes")));

        }
        String t = categories.get(mPosition).getObjectId();
        if (!seenItemsLIst.contains(t)) {
            seenItemsLIst.add(t);
            notSeenCounter--;
        }
        int pos = position + 1;
        picNumber.setText(String.valueOf(pos));

        initLikeButton();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void fullScreenTouch(int touch) {

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


    @Override
    public void onResume() {
        likesList = tinydb.getListString(SAVED_LIST);
        seenItemsLIst = tinydb.getListString(Constants.SEEN_LIST);
        notSeenCounter = tinydb.getInt(Constants.SEEN_ITEMS_COUNTER);
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);
        tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);
        if(isAdded()){
            ShortcutBadger.with(getActivity()).count(notSeenCounter);
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);
        tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);

        if(isAdded()){
            ShortcutBadger.with(getActivity()).count(notSeenCounter);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tinydb.putListString(SAVED_LIST, likesList);
        tinydb.putListString(Constants.SEEN_LIST, seenItemsLIst);
        tinydb.putInt(Constants.SEEN_ITEMS_COUNTER, notSeenCounter);
        if(isAdded()){
            ShortcutBadger.with(getActivity()).count(notSeenCounter);
        }
    }

    public void getQuerySize() {

        ParseQuery query = new ParseQuery("picture");
        query.whereNotContainedIn("objectId", seenItemsLIst);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                querySize = count;
                seenItemsLIst = tinydb.getListString(Constants.SEEN_LIST);
                notSeenCounter = querySize - seenItemsLIst.size();
                if (notSeenCounter < 0) {
                    notSeenCounter = 0;
                }
                String pics = "/" + querySize;
                allPicNumber.setText(pics);

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            topLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            counterLayout.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            topLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            counterLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBannerShown() {

    }

    @Override
    public void onImageShown() {

    }
}
