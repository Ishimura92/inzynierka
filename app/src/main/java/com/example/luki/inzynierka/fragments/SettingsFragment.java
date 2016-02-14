package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.nononsenseapps.filepicker.FilePickerActivity;

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

    private static final int FILE_PICKER_CODE = 200;
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

    @Click(R.id.buttonImport)
    void onImportClicked(){
        importVehicle();
    }

    private void importVehicle(){
        Intent i = new Intent(getContext(), FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath() + File.separator + "Serwisant_files");

        startActivityForResult(i, FILE_PICKER_CODE);
    }

    @Click(R.id.checkBoxEnableNotifications)
    void onCheckboxClicked(){
        preferences.areNotificationsTurned().put(checkBoxEnableNotifications.isChecked());
    }

    private void exportVehicle(){
        final String timeStamp = new SimpleDateFormat(FILE_DATE_FORMAT).format(new Date());

        final File vehiclesFolder = new File(Environment.getExternalStorageDirectory(), "Serwisant_files");
        vehiclesFolder.mkdirs();
        final File exportedFile = new File(vehiclesFolder, currentVehicle.getBrand() + currentVehicle.getModel() + timeStamp + ".txt");

        Gson gson = new Gson();
//        String vehicleToExport = gson.toJson(currentVehicle);
        String vehicleToExport = currentVehicle.toString();

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
