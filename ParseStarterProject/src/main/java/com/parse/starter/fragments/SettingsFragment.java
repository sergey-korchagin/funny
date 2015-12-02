package com.parse.starter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.starter.R;
import com.parse.starter.utils.Constants;

/**
 * Created by User on 02/12/2015.
 */
public class SettingsFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.settings_fragment, container, false);

        Constants.FROM_SETTINGS = true;

        return root;
    }
}
