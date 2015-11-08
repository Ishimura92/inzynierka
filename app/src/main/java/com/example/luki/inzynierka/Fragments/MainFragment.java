package com.example.luki.inzynierka.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.example.luki.inzynierka.Callbacks.MainActivityCallbacks;

import io.realm.Realm;

public class MainFragment extends Fragment{

    private MainActivityCallbacks mainActivityCallbacks;
    private Realm realm;


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
        realm = Realm.getInstance(getActivity());
    }
}
