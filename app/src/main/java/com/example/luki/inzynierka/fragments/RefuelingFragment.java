package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.callbacks.RefuelingCallbacks;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector;
import com.example.luki.inzynierka.dialogs.NewRefuelingDialog;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

@EFragment(R.layout.fragment_refueling)
public class RefuelingFragment extends Fragment{

    @ViewById
    ViewPager refuelingPager;
    @ViewById
    TabLayout refuelingTabs;
    @Bean
    DatabaseConnector databaseConnector;
    @Bean
    NewRefuelingDialog newRefuelingDialog;

    private DatePickerDialog.OnDateSetListener date;
    private MainActivityCallbacks mainActivityCallbacks;
    private RefuelingCallbacks refuelingCallbacks;
    //private NewRefuelingDialog newRefuelingDialog;
    private Realm realm;
    private Vehicle currentVehicle;
    private Calendar myCalendar;

    private MenuItem burningGraphItem;
    private MenuItem priceGraphItem;

    private Fragment refuelingHistoryFragment = new RefuelingHistoryFragment_();
    private Fragment refuelingSummaryFragment = new RefuelingSummaryFragment_();
    private Fragment refuelingGraphsFragment = new RefuelingGraphsFragment_();
    private Activity activity;

    @AfterViews
    void init(){
        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        setupViewPager(refuelingPager);
        refuelingTabs.setupWithViewPager(refuelingPager);
        setHasOptionsMenu(true);

        setDatabaseConnector();
    }

    private void setDatabaseConnector() {
        databaseConnector.setRefuelingHistoryFragment((RefuelingHistoryFragment) refuelingHistoryFragment);
        databaseConnector.setRefuelingSummaryFragment((RefuelingSummaryFragment) refuelingSummaryFragment);
        databaseConnector.setDatabaseConnectorRealm(realm);
        databaseConnector.setCurrentVehicle(currentVehicle);
        databaseConnector.setRefuelingCallbacks(refuelingCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refueling_menu, menu);
        burningGraphItem = menu.findItem(R.id.chart_burning);
        priceGraphItem = menu.findItem(R.id.chart_price);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_refueling:
                showNewRefuelingDialog();
                break;
            case R.id.chart_price:
                refuelingCallbacks.showPriceChart(refuelingGraphsFragment);
                break;
            case R.id.chart_burning:
                refuelingCallbacks.showBurningChart(refuelingGraphsFragment);
                break;
            default:
                break;
        }
        return true;
    }

    public void showNewRefuelingDialog() {
        newRefuelingDialog.show();
        newRefuelingDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        mainActivityCallbacks = (MainActivityCallbacks) activity;
        refuelingCallbacks = (RefuelingCallbacks) activity;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(refuelingHistoryFragment, getActivity().getString(R.string.history));
        adapter.addFragment(refuelingSummaryFragment, getActivity().getString(R.string.summary));
        adapter.addFragment(refuelingGraphsFragment, getActivity().getString(R.string.graphs));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position != 2){
                    burningGraphItem.setVisible(false);
                    priceGraphItem.setVisible(false);
                } else {
                    burningGraphItem.setVisible(true);
                    priceGraphItem.setVisible(true);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
    }

    public Fragment getRefuelingSummaryFragment(){
        return refuelingSummaryFragment;
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

}
