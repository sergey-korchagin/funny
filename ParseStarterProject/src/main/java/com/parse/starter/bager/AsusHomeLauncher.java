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
public class AsusHomeLauncher extends ShortcutBadger {
    private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";

    public AsusHomeLauncher(Context context) {
        super(context);
    }

    @Override
    protected void executeBadge(int badgeCount) throws ShortcutBadgeException {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, getContextPackageName());
        intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, getEntryActivityName());
        intent.putExtra("badge_vip_count", 0);
        mContext.sendBroadcast(intent);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList("com.asus.launcher");
    }
}
