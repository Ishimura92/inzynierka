package com.example.luki.inzynierka;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.luki.inzynierka.adapters.DrawerListAdapter;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.callbacks.RefuelingCallbacks;
import com.example.luki.inzynierka.callbacks.RepairCallbacks;
import com.example.luki.inzynierka.fragments.MainFragment;
import com.example.luki.inzynierka.fragments.RefuelingFragment_;
import com.example.luki.inzynierka.fragments.RefuelingGraphsFragment_;
import com.example.luki.inzynierka.fragments.RefuelingHistoryFragment_;
import com.example.luki.inzynierka.fragments.RefuelingSummaryFragment_;
import com.example.luki.inzynierka.fragments.RepairFragment_;
import com.example.luki.inzynierka.fragments.RepairHistoryFragment_;
import com.example.luki.inzynierka.fragments.RepairSummaryFragment_;
import com.example.luki.inzynierka.fragments.ServiceFragment_;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.NavItem;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements MainActivityCallbacks, RefuelingCallbacks, RepairCallbacks {

    public ListView mDrawerList;
    public RelativeLayout mDrawerPane;

    private int vehicleID;

    private Realm realm;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ImageView imageViewAvatar;
    private TextView textViewCarBrand, textViewCarModel;
    private Vehicle currentVehicle;
    private RelativeLayout profileBox;

    private MainFragment mainFragment;
    private RefuelingHistoryFragment_ refuelingHistoryFragment;
    private RefuelingSummaryFragment_ refuelingSummaryFragment;
    private RefuelingGraphsFragment_ refuelingGraphsFragment;
    private RefuelingFragment_ refuelingFragment;
    private ServiceFragment_ serviceFragment;
    private RepairFragment_ repairFragment;
    private RepairHistoryFragment_ repairHistoryFragment;
    private RepairSummaryFragment_ repairSummaryFragment;

    ArrayList<NavItem> mNavItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        getExtras();

        initView();
        initFragments();

        getVehicleFromRealm();
        setupNavigationDrawer();

        changeToMainFragment("Aktualności");
    }

    private void initView() {
        imageViewAvatar = (ImageView) findViewById(R.id.avatar);
        textViewCarBrand = (TextView) findViewById(R.id.carBrand);
        textViewCarModel = (TextView) findViewById(R.id.carModel);
        profileBox = (RelativeLayout) findViewById(R.id.profileBox);
    }

    private void getVehicleFromRealm(){
        RealmQuery<Vehicle> query = realm.where(Vehicle.class);
        query.equalTo("id", vehicleID);
        RealmResults<Vehicle> results = query.findAll();
        currentVehicle = results.first();
    }

    private void initFragments() {
        mainFragment = new MainFragment();
        refuelingFragment = new RefuelingFragment_();
        refuelingHistoryFragment = new RefuelingHistoryFragment_();
        refuelingSummaryFragment = new RefuelingSummaryFragment_();
        refuelingGraphsFragment = new RefuelingGraphsFragment_();
        serviceFragment = new ServiceFragment_();
        repairFragment = new RepairFragment_();
        repairHistoryFragment = new RepairHistoryFragment_();
        repairSummaryFragment = new RepairSummaryFragment_();
    }

    @Override
    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    @Override
    public void setTitle(CharSequence title) {
       if(getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    private void setupNavigationDrawer() {
        mNavItems.add(new NavItem("Naprawy", "Przeglądaj swoje naprawy", R.drawable.ic_repair_black_small));
        mNavItems.add(new NavItem("Tankowania", "Zarządzaj tankowaniem", R.drawable.ic_fuel_black_small));
        mNavItems.add(new NavItem("Serwisy", "Spis dokonanych serwisów", R.drawable.ic_service_black_small));
        mNavItems.add(new NavItem("Warsztaty", "Twoi mechanicy", R.drawable.ic_mechanic_black_small));

        mNavItems.add(new NavItem("Garaż", "Widok wyboru pojazdów", R.drawable.car_placeholder_small));

        imageViewAvatar.setImageResource(currentVehicle.getImage());
        textViewCarModel.setText(currentVehicle.getModel());
        textViewCarBrand.setText(currentVehicle.getBrand());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        setDrawerListeners();
    }

    private void setDrawerListeners() {
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        profileBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToMainFragment("Aktualności");
                mDrawerLayout.closeDrawer(mDrawerPane);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItemFromDrawer(int position) {
        mDrawerList.setItemChecked(position, true);

        switch (position){
            case 0:
                changeToRepairFragment("Naprawy");
                mDrawerLayout.closeDrawer(mDrawerPane);
                break;

            case 1:
                changeToRefuelingFragment("Tankowania");
                mDrawerLayout.closeDrawer(mDrawerPane);
                break;

            case 2:
                changeToServiceFragment("Serwisy");
                mDrawerLayout.closeDrawer(mDrawerPane);
                break;

            case 4:
                Intent intent = new Intent(this, VehicleChooser_.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                this.startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            vehicleID = extras.getInt("CHOSEN_VEHICLE_ID");
        }
    }

    @Override
    public void changeToMainFragment(String fragmentTitle) {
        setTitle(fragmentTitle);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, mainFragment);
        transaction.addToBackStack(fragmentTitle);
        transaction.commit();
    }


    @Override
    public void changeToServiceFragment(String fragmentTitle) {
        setTitle(fragmentTitle);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, serviceFragment);
        transaction.addToBackStack(fragmentTitle);
        transaction.commit();
    }

    @Override
    public void changeToRefuelingFragment(String fragmentTitle) {
        setTitle(fragmentTitle);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, refuelingFragment);
        transaction.addToBackStack(fragmentTitle);
        transaction.commit();
    }

    @Override
    public void changeToRepairFragment(String fragmentTitle) {
        setTitle(fragmentTitle);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, repairFragment);
        transaction.addToBackStack(fragmentTitle);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        this.finishAffinity();
        //TODO dodać info, że jeśli się kliknie dwa razy to dopiero wyjść
    }

    @Override
    public void notifyRefuelingDatasetChanged(Fragment historyFragment, Fragment summaryFragment, Refueling refueling) {
        refuelingHistoryFragment = (RefuelingHistoryFragment_) historyFragment;
        refuelingSummaryFragment = (RefuelingSummaryFragment_) summaryFragment;
        refuelingHistoryFragment.notifyNewRefueling(refueling);
        refuelingSummaryFragment.notifyNewRefueling();
    }

    @Override
    public void notifyRefuelingDeleted() {
        refuelingSummaryFragment = (RefuelingSummaryFragment_) refuelingFragment.getRefuelingSummaryFragment();
        refuelingSummaryFragment.notifyRefuelingDeleted();
    }

    @Override
    public void showBurningChart(Fragment graphsFragment) {
        refuelingGraphsFragment = (RefuelingGraphsFragment_) graphsFragment;
        refuelingGraphsFragment.showBurningChart();
    }

    @Override
    public void showPriceChart(Fragment graphsFragment) {
        refuelingGraphsFragment = (RefuelingGraphsFragment_) graphsFragment;
        refuelingGraphsFragment.showPriceChart();
    }

    @Override
    public void notifyRepairDatasetChanged(Fragment historyFragment, Fragment summaryFragment, Repair repair) {
        repairHistoryFragment = (RepairHistoryFragment_) historyFragment;
        repairSummaryFragment = (RepairSummaryFragment_) summaryFragment;
        repairHistoryFragment.notifyNewRepair(repair);
        repairSummaryFragment.notifyNewRepair();
    }

    @Override
    public void notifyRepairDeleted() {
        repairSummaryFragment = (RepairSummaryFragment_) repairFragment.getRepairSummaryFragment();
        repairSummaryFragment.notifyRepairDeleted();
    }
}
