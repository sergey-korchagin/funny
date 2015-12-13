package com.parse.starter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parse.starter.managers.TinyDB;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.ShortcutBadger;

/**
 * Created by User on 13/12/2015.
 */
public class CustomParseBroadcastReceiver extends BroadcastReceiver

{
    public static final String ACTION = "action.add.badge";

TinyDB tinyDB;
    int counter;
    @Override
    public void onReceive(Context context, Intent intent) {
        tinyDB = new TinyDB(context);
        counter = tinyDB.getInt(Constants.SEEN_ITEMS_COUNTER);

        int badgeCount = counter+15;
        ShortcutBadger.with(context).count(badgeCount);
    }
    }