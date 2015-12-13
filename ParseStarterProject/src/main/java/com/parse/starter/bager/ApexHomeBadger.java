package com.parse.starter.bager;

import android.content.Context;
import android.content.Intent;

import com.parse.starter.utils.ShortcutBadgeException;
import com.parse.starter.utils.ShortcutBadger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 13/12/2015.
 */
public class ApexHomeBadger extends ShortcutBadger {

    private static final String INTENT_UPDATE_COUNTER = "com.anddoes.launcher.COUNTER_CHANGED";
    private static final String PACKAGENAME = "package";
    private static final String COUNT = "count";
    private static final String CLASS = "class";

    public ApexHomeBadger(Context context) {
        super(context);
    }

    @Override
    protected void executeBadge(int badgeCount) throws ShortcutBadgeException {

        Intent intent = new Intent(INTENT_UPDATE_COUNTER);
        intent.putExtra(PACKAGENAME, getContextPackageName());
        intent.putExtra(COUNT, badgeCount);
        intent.putExtra(CLASS, getEntryActivityName());
        mContext.sendBroadcast(intent);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList("com.anddoes.launcher");
    }
}
