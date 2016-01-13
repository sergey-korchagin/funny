package com.nafunny.app.bager;

import android.content.Context;
import android.content.Intent;

import com.nafunny.app.utils.ShortcutBadgeException;
import com.nafunny.app.utils.ShortcutBadger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 13/12/2015.
 */
public class SolidHomeBadger extends ShortcutBadger {
    private static final String INTENT_UPDATE_COUNTER = "com.majeur.launcher.intent.action.UPDATE_BADGE";
    private static final String PACKAGENAME = "com.majeur.launcher.intent.extra.BADGE_PACKAGE";
    private static final String COUNT = "com.majeur.launcher.intent.extra.BADGE_COUNT";
    private static final String CLASS = "com.majeur.launcher.intent.extra.BADGE_CLASS";

    public SolidHomeBadger(Context context) {
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
        return Arrays.asList("com.majeur.launcher");
    }
}

