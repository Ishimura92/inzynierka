package com.example.luki.inzynierka;

import android.os.Bundle;
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

import com.example.luki.inzynierka.Adapters.DrawerListAdapter;
import com.example.luki.inzynierka.Callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.Fragments.MainFragment;
import com.example.luki.inzynierka.Models.Vehicle;
import com.example.luki.inzynierka.Utils.NavItem;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements MainActivityCallbacks {

    public ListView mDrawerList;
    public RelativeLayout mDrawerPane;
    private int vehicleID;
    private Realm realm;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private MainFragment mainFragment;
    private ImageView imageViewAvatar;
    private TextView textViewCarBrand, textViewCarModel;
    private Vehicle currentVehicle;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getExtras();

        initView();
        initFragments();

        getVehicleFromRealm();

        setupNavigationDrawer();
    }

    private void initView() {
        imageViewAvatar = (ImageView) findViewById(R.id.avatar);
        textViewCarBrand = (TextView) findViewById(R.id.carBrand);
        textViewCarModel = (TextView) findViewById(R.id.carModel);
    }

    private void getVehicleFromRealm(){
        RealmQuery<Vehicle> query = realm.where(Vehicle.class);
        query.equalTo("id", vehicleID);
        RealmResults<Vehicle> results = query.findAll();
        currentVehicle = results.first();
    }

    private void initFragments() {
        mainFragment = new MainFragment();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    private void setupNavigationDrawer() {
        mNavItems.add(new NavItem("Naprawy", "Przeglądaj swoje naprawy", R.drawable.ic_repair_black_small));
        mNavItems.add(new NavItem("Tankowania", "Zarządzaj tankowaniem", R.drawable.ic_fuel_black_small));
        mNavItems.add(new NavItem("Serwisy", "Spis dokonanych serwisów", R.drawable.ic_service_black_small));
        mNavItems.add(new NavItem("Warsztaty", "Twoi mechanicy", R.drawable.ic_mechanic_black_small));

        imageViewAvatar.setImageResource(currentVehicle.getImage());
        textViewCarModel.setText(currentVehicle.getModel());
        textViewCarBrand.setText(currentVehicle.getBrand());

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
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

        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
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
        transaction.replace(R.id.fragment_main, mainFragment);
        transaction.addToBackStack(fragmentTitle);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        this.finishAffinity();
        //TODO dodać info, że jeśli się kliknie dwa razy to dopiero wyjść
    }
}
