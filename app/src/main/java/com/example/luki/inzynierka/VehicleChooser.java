package com.example.luki.inzynierka;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.luki.inzynierka.Adapters.VehicleListAdapter;
import com.example.luki.inzynierka.Models.Vehicle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@EActivity(R.layout.activity_vehicle_chooser)
public class VehicleChooser extends AppCompatActivity{

    @ViewById
    RecyclerView recyclerView;
    @ViewById
    FloatingActionButton fab;

    private Realm realm;
    private List<Vehicle> vehicleList;
    private VehicleListAdapter adapter;

    @AfterViews
    void init(){
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        vehicleList = new ArrayList<>();
        generateVehiclesToTest();
        adapter = new VehicleListAdapter(vehicleList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Click(R.id.fab)
    void onFabClick(){
        Snackbar.make(recyclerView, "Plusik!", Snackbar.LENGTH_SHORT).show();
    }

    private void generateVehiclesToTest(){
        Vehicle pierwszy = new Vehicle(1, "Audi", "Coupe", new Date(1994,11,11), "0xffff", "Benzyna + gaz", 2.0f, 22345, "Coupe");
        Vehicle drugi = new Vehicle(2, "Opel", "Zafira", new Date(2006,1,19), "0xffff", "Diesel", 1.9f, 31901, "Van");
        vehicleList.add(pierwszy);
        vehicleList.add(drugi);
//
//        Vehicle pierwszy1 = new Vehicle(1, "Audi", "Coupe", new Date(1994,11,11), "0xffff", "Benzyna + gaz", 2.0f, 22345, "Coupe");
//        Vehicle drugi1 = new Vehicle(2, "Opel", "Zafira", new Date(2006,1,19), "0xffff", "Diesel", 1.9f, 31901, "Van");
//        Vehicle pierwszy2 = new Vehicle(1, "Audi", "Coupe", new Date(1994,11,11), "0xffff", "Benzyna + gaz", 2.0f, 22345, "Coupe");
//        Vehicle drugi2 = new Vehicle(2, "Opel", "Zafira", new Date(2006,1,19), "0xffff", "Diesel", 1.9f, 31901, "Van");
//        Vehicle pierwszy3 = new Vehicle(1, "Audi", "Coupe", new Date(1994,11,11), "0xffff", "Benzyna + gaz", 2.0f, 22345, "Coupe");
//        Vehicle drugi3 = new Vehicle(2, "Opel", "Zafira", new Date(2006,1,19), "0xffff", "Diesel", 1.9f, 31901, "Van");
//        vehicleList.add(pierwszy1);
//        vehicleList.add(drugi1);
//        vehicleList.add(pierwszy2);
//        vehicleList.add(drugi2);
//        vehicleList.add(pierwszy3);
//        vehicleList.add(drugi3);

        realm.beginTransaction();
        realm.copyToRealm(pierwszy);
        realm.copyToRealm(drugi);
        realm.commitTransaction();
    }
}
