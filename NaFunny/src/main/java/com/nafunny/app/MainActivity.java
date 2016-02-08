/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.nafunny.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.Tracker;
import com.nafunny.app.fragments.SplashScreenFragment;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.nafunny.app.managers.AnalyticsManager;
import com.nafunny.app.utils.Constants;
import com.nafunny.app.utils.Utils;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {
  private ShareActionProvider mShareActionProvider;
    Tracker mTracker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

      ParseAnalytics.trackAppOpenedInBackground(getIntent());
     // ActionbarManager.getInstance().init(this, getSupportActionBar());


      ParseInstallation installation = ParseInstallation.getCurrentInstallation();
      installation.addAllUnique("channels", Arrays.asList("photos"));
      String user;
      user = Utils.getUsername(this);
      if(user!=null){
            installation.put("userName",user);
      }
      else{
          installation.put("userName","anonimus");
      }
      installation.saveInBackground();

      Fabric.with(this, new Crashlytics());

      AnalyticsManager.getInstance().init(getApplicationContext());
      String userName;
      userName = Utils.getUsername(this);
      if(userName!=null){
          AnalyticsManager.getInstance().setUserName(userName);
      }
      else{
          AnalyticsManager.getInstance().setUserName("no name user");
      }
      AdBuddiz.setPublisherKey("fcce50c3-58fe-4ce7-8c77-a983b1966389");
      AdBuddiz.cacheAds(this); // this = current Activity


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
                return  true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }
}
