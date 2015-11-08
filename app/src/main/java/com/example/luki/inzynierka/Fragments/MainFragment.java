package com.example.luki.inzynierka.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.luki.inzynierka.Callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.R;

import io.realm.Realm;

public class MainFragment extends Fragment{

    private MainActivityCallbacks mainActivityCallbacks;
    private Realm realm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

;
        realm = Realm.getInstance(getActivity());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
    }
}
