/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.starter.fragments.PicturesMainFragment;
import com.parse.starter.fragments.SettingsFragment;
import com.parse.starter.fragments.SplashScreenFragment;
import com.parse.starter.managers.ActionbarManager;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.ShortcutBadger;
import com.parse.starter.utils.Utils;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {
  private ShareActionProvider mShareActionProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

      ParseAnalytics.trackAppOpenedInBackground(getIntent());
     // ActionbarManager.getInstance().init(this, getSupportActionBar());


      ParseInstallation installation = ParseInstallation.getCurrentInstallation();
      installation.addAllUnique("channels", Arrays.asList("photos"));
      installation.saveInBackground();

      Fabric.with(this, new Crashlytics());

      SplashScreenFragment splashScreenFragment = new SplashScreenFragment();
      Utils.replaceFragment(getFragmentManager(), android.R.id.content, splashScreenFragment, false);
  }



  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate menu resource file.
   // getMenuInflater().inflate(R.menu.menu_main, menu);

      // Return true to display menu
      return true;
  }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                Utils.replaceFragment(getFragmentManager(), android.R.id.content, settingsFragment, true);
                return  true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        if(!Constants.FROM_SETTINGS){
            super.onBackPressed();
        }else {
            getFragmentManager().popBackStackImmediate();
            Constants.FROM_SETTINGS = false;
        }

    }
}
