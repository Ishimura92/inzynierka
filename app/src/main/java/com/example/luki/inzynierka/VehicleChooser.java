package com.example.luki.inzynierka;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.luki.inzynierka.Adapters.VehicleListAdapter;
import com.example.luki.inzynierka.Models.Vehicle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.activity_vehicle_chooser)
public class VehicleChooser extends AppCompatActivity{

    @ViewById
    RecyclerView recyclerView;

    private List<Vehicle> vehicleList;
    private VehicleListAdapter adapter;

    @AfterViews
    void init(){
        vehicleList = new ArrayList<>();
        generateVehiclesToTest();
        adapter = new VehicleListAdapter(vehicleList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void generateVehiclesToTest(){
        vehicleList.add(new Vehicle("Audi", "Coupe", new Date(1994,11,11), 12345, "Benzyna + gaz", 2.0f, 22345, "Coupe"));
        vehicleList.add(new Vehicle("Opel", "Zafira", new Date(2006,01,19), 12345, "Diesel", 1.9f, 31901, "Van"));
    }

}
