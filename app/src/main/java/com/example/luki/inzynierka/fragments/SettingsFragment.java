package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

@EFragment(R.layout.fragment_settings)
public class SettingsFragment extends Fragment{

    @Pref
    Preferences_ preferences;

    @ViewById
    CheckBox checkBoxEnableNotifications;

    @ViewById
    Button buttonImport;

    @ViewById
    Button buttonExport;

    private static final String FILE_DATE_FORMAT = "yyyyMMdd_HHmmss";

    private MainActivityCallbacks mainActivityCallbacks;
    private Realm realm;
    private Vehicle currentVehicle;

    @AfterViews
    void init(){
        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
    }

    @Click(R.id.buttonExport)
    void onExportClicked(){
        exportVehicle();
    }

    private void importVehicle(){

        Gson gson = new Gson();
//       odbieranie final FormListObject tempObject = gson.fromJson(preferences.storedListForms().get(), FormListObject.class);
    }

    private void exportVehicle(){
        final String timeStamp = new SimpleDateFormat(FILE_DATE_FORMAT).format(new Date());

        final File vehiclesFolder = new File(Environment.getExternalStorageDirectory(), "Serwisant_files");
        vehiclesFolder.mkdirs();
        final File exportedFile = new File(vehiclesFolder, currentVehicle.getBrand() + currentVehicle.getModel() + timeStamp + ".txt");

        Gson gson = new Gson();
        String vehicleToExport = gson.toJson(currentVehicle);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(exportedFile, true);

            fos.write(vehicleToExport.getBytes());
            fos.close();
            Snackbar.make(checkBoxEnableNotifications, "Zapisano dane do pliku", Snackbar.LENGTH_LONG).show();
        } catch (IOException e) {
            Snackbar.make(checkBoxEnableNotifications, "Nie udało się zapisać danych", Snackbar.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
