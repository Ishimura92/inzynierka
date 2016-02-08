package com.example.luki.inzynierka.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.callbacks.RefuelingCallbacks;
import com.example.luki.inzynierka.callbacks.RepairCallbacks;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector;
import com.example.luki.inzynierka.databaseUtils.Variables;
import com.example.luki.inzynierka.dialogs.NewRepairDialog;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

@EFragment(R.layout.fragment_repair)
public class RepairFragment extends Fragment{

    private static final int TAKE_PHOTO_REQUEST = 200;
    @ViewById
    ViewPager repairPager;
    @ViewById
    TabLayout repairTabs;
    @Pref
    Preferences_ preferences;
    @Bean
    NewRepairDialog newRepairDialog;
    @Bean
    DatabaseConnector databaseConnector;
    @Bean
    Variables variables;

    private MainActivityCallbacks mainActivityCallbacks;
    private RepairCallbacks repairCallbacks;
    private Realm realm;
    private Vehicle currentVehicle;

    private Fragment repairHistoryFragment = new RepairHistoryFragment_();
    private Fragment repairSummaryFragment = new RepairSummaryFragment_();
    private DateTimeFormatter formatter;

    @AfterViews
    void init(){
        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        setupViewPager(repairPager);
        repairTabs.setupWithViewPager(repairPager);
        setHasOptionsMenu(true);
        formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        setDatabaseConnector();
        newRepairDialog.setCallingFragment(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
        repairCallbacks = (RepairCallbacks) activity;
    }

    private void setDatabaseConnector() {
        databaseConnector.setRepairHistoryFragment((RepairHistoryFragment) repairHistoryFragment);
        databaseConnector.setRepairSummaryFragment((RepairSummaryFragment) repairSummaryFragment);
        databaseConnector.setDatabaseConnectorRealm(realm);
        databaseConnector.setCurrentVehicle(currentVehicle);
        databaseConnector.setRepairCallbacks(repairCallbacks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_repair:
                newRepairDialog.show();
                newRepairDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.repair_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(repairHistoryFragment, getActivity().getString(R.string.history));
        adapter.addFragment(repairSummaryFragment, getActivity().getString(R.string.summary));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            final Uri uri = variables.getPhotoUri();
            final String path = prepareProperFilePath(uri.getPath());
            variables.setProperPhotoPath(path);
            newRepairDialog.receivePhotoData(path, uri);
        }
    }

    @NonNull
    public String prepareProperFilePath(String path) {
        String properFilePath = "";
        if (path.split(":").length > 1) {
            final String[] fileNameArray = path.split(":");
            for (int i = 1; i < fileNameArray.length; i++) {
                properFilePath += fileNameArray[i] + ":";
            }
            properFilePath = properFilePath.substring(0, properFilePath.length() - 1);
        } else {
            properFilePath = path;
        }
        return properFilePath;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public Fragment getRepairSummaryFragment(){
        return repairSummaryFragment;
    }

}
