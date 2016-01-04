package com.parse.starter.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by User on 30/11/2015.
 */
public class Utils {

    public static void replaceFragment(FragmentManager fragmentManager, int container, Fragment fragment, boolean AddToBackStack) {

        //Enter Animations Later
        if(fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (AddToBackStack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(container, fragment).commitAllowingStateLoss();

        }
    }

    public static void showAlert(Context context, String title,  String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();
    }
    public static String addLikes(String string){
        int t = Integer.valueOf(string);
        t = t+1;
        return String.valueOf(t);
    }

    public static String removeLikes(String string){
        int t = Integer.valueOf(string);
        t = t-1;
        return String.valueOf(t);
    }

    public static String getUsername(Activity activity) {
        AccountManager manager = AccountManager.get(activity);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type
            // values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return null;
        } else
            return null;
    }
}
