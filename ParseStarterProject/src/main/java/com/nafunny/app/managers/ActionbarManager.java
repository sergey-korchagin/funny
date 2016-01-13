package com.nafunny.app.managers;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;

import com.nafunny.app.R;


/**
 * Created by use on 13/08/2015.
 */
public class ActionbarManager {




    private static ActionbarManager ourInstance = new ActionbarManager();

    public static ActionbarManager getInstance() {
        return ourInstance;
    }

    private ActionbarManager() {
    }


    public void setButtonsListener(View.OnClickListener listener) {
    }




    public void init(Context context, ActionBar actionBar) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(
                R.layout.action_bar_layout,
                null);

        // Set up ActionBar
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);


        actionBar.setCustomView(layout);
        int actionBarColor = context.getResources().getColor(R.color.violet);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
    }
}
