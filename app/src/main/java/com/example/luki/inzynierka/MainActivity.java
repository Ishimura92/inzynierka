package com.example.luki.inzynierka;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.luki.inzynierka.Models.Vehicle;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private int vehicleID;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();

        getExtras();

        RealmQuery<Vehicle> query = realm.where(Vehicle.class);
        query.equalTo("id", vehicleID);
        RealmResults<Vehicle> results = query.findAll();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            vehicleID = extras.getInt("CHOSEN_VEHICLE_ID");
        }
    }
}
