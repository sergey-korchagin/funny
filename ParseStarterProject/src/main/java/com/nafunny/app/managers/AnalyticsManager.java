package com.nafunny.app.managers;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nafunny.app.R;


/**
 * Created by dors on 9/17/15.
 * <p/>
 * A manager for all analytics operation across the application
 */
public class AnalyticsManager {

    private static final String TAG = AnalyticsManager.class.getSimpleName();

    private static AnalyticsManager sAnalyticsManager;

    private Tracker mTracker;

    // Screens
    public static final String SCREEN_ALL = "All Screen";
    public static final String SCREEN_NEW = "New Screen";
    public static final String SCREEN_TOP = "Top Screen";



    // Dimensions
    private static final int INDEX_USERNAME = 1;
    private static final int INDEX_DEMO_ = 2;
    private static final String VALUE_DEMO = "demo";
    private String mUserName;



    public static final String NAME = "name";


    private AnalyticsManager() {
    }

    public static AnalyticsManager getInstance() {
        if (sAnalyticsManager == null) {
            synchronized (AnalyticsManager.class) {
                if (sAnalyticsManager == null) {
                    sAnalyticsManager = new AnalyticsManager();
                }
            }
        }

        return sAnalyticsManager;
    }

    /**
     * Initialize the analytics manager
     *
     * @param context Application/First activity context
     */
    public void init(Context context) {
        final GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        mTracker = googleAnalytics.newTracker(R.xml.analytics);
    }

    /**
     * Send screen event
     *
     * @param screenName The screen name
     */
    public void sendScreenEvent(String screenName) {

        if (mTracker != null) {
            mTracker.setScreenName(screenName);
            mTracker.setClientId(mUserName);
            HitBuilders.ScreenViewBuilder screenBuilder = new HitBuilders.ScreenViewBuilder();

            if (!TextUtils.isEmpty(mUserName)) {
                screenBuilder.setCustomDimension(INDEX_USERNAME, mUserName);
            }


            mTracker.send(screenBuilder.build());

        }
    }

    /**
     * Calls {@link #sendEvent(String, String, String, long)} with -1 optionalValue.
     */
//    public void sendEvent(String category, String action, String label) {
//        sendEvent(category, action, label, -1);
//    }

    /**
     * Send event
     *
     * @param category      Event's category
     * @param action        Event's action
     * @param label         Event's label
     * @param optionalValue Optional extra value
     */
    public void sendEvent(String category, String action, String label, String optionalValue) {
        if (mTracker != null) {

            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
            eventBuilder.setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .set(NAME,optionalValue);

            if (!TextUtils.isEmpty(mUserName)) {
                eventBuilder.setCustomDimension(INDEX_USERNAME, mUserName);
            }

            mTracker.send(eventBuilder.build());
        }
    }

    /**
     * Set the current user name, for the dimensions
     *
     * @param userName the user name
     */
    public void setUserName(String userName) {
        this.mUserName = userName;
    }
}
