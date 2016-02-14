package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;
import com.google.gson.Gson;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

@EFragment(R.layout.fragment_stats)
public class StatsFragment extends Fragment{

    @Pref
    Preferences_ preferences;

    @ViewById
    TextView textViewStatsBrand, textViewStatsBody, textViewStatsModel, textViewStatsEngine, textViewStatsOdometer,
            textViewStatsDate, textViewStatsRefuelingsTotal, textViewStatsRepairsTotal, textViewStatsServicesTotal;

    private static final String FILE_DATE_FORMAT = "yyyyMMdd_HHmmss";

    private MainActivityCallbacks mainActivityCallbacks;
    private Realm realm;
    private Vehicle currentVehicle;

    @AfterViews
    void init(){
        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();

        putDataToViews();
    }

    private void putDataToViews() {
        textViewStatsBody.setText(currentVehicle.getBodyType());
        textViewStatsBrand.setText(currentVehicle.getBrand());
        textViewStatsModel.setText(currentVehicle.getModel());
        textViewStatsEngine.setText(currentVehicle.getEngineCapacity() + " " + currentVehicle.getEngineType());
        textViewStatsOdometer.setText(String.valueOf(currentVehicle.getOdometer()));

        final DateTime dateTime = new DateTime(currentVehicle.getProductionDate());
        textViewStatsDate.setText(String.valueOf(dateTime.getYear()));

        if(currentVehicle.getRefuelings() != null) {
            textViewStatsRefuelingsTotal.setText(String.valueOf(currentVehicle.getRefuelings().size()));
        } else {
            textViewStatsRefuelingsTotal.setText("0");
        }

        if(currentVehicle.getRepairs() != null) {
            textViewStatsRepairsTotal.setText(String.valueOf(currentVehicle.getRepairs().size()));
        } else {
            textViewStatsRepairsTotal.setText("0");
        }

        if(currentVehicle.getServices() != null) {
            textViewStatsServicesTotal.setText(String.valueOf(currentVehicle.getServices().size()));
        } else {
            textViewStatsServicesTotal.setText("0");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
    }

}
