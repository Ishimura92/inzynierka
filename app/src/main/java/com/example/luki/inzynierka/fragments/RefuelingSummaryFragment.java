package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Vehicle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@EFragment(R.layout.fragment_refueling_summary)
public class RefuelingSummaryFragment extends Fragment{

    @ViewById
    TextView textViewMaxFuelSpent;
    @ViewById
    TextView textViewLastRefuelingSpent;
    @ViewById
    TextView textViewTotalFuelSpent;

    private Realm realm;
    private List<Refueling> refuelingList = new ArrayList<>();
    private MainActivityCallbacks mainActivityCallbacks;
    private float totalFuelSpent = 0;
    private float maxFuelSpent = 0;
    private float lastFuelSpent = 0;
    private Vehicle currentVehicle;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
    }

    @AfterViews
    void init() {
        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        getFuelDataFromRealm();
        viewData();
    }

    private void viewData() {
        if (maxFuelSpent == 0) {
            textViewMaxFuelSpent.setText("b/d");
        } else {
            textViewMaxFuelSpent.setText(String.valueOf(maxFuelSpent) + " zł");
        }

        if (totalFuelSpent == 0) {
            textViewTotalFuelSpent.setText("b/d");
        } else {
            textViewTotalFuelSpent.setText(String.valueOf(totalFuelSpent) + " zł");
        }

        if (lastFuelSpent == 0) {
            textViewLastRefuelingSpent.setText("b/d");
        } else {
            textViewLastRefuelingSpent.setText(String.valueOf(lastFuelSpent) + " zł");
        }
    }

    private void getFuelDataFromRealm() {
        refuelingList.clear();
        realm.beginTransaction();
        RealmQuery<Vehicle> query = realm.where(Vehicle.class).equalTo("id", currentVehicle.getId());
        RealmResults<Vehicle> results = query.findAll();
        realm.commitTransaction();

        processGatheredData(results);
    }

    private void processGatheredData(RealmResults<Vehicle> results) {
        clearAllData();
        if (results.first().getRefuelings().size() > 0) {
            for (Refueling refueling : results.first().getRefuelings()) {
                refuelingList.add(refueling);
                totalFuelSpent += refueling.getPrice();
                if (refueling.getPrice() > maxFuelSpent) {
                    maxFuelSpent = refueling.getPrice();
                }
            }
            lastFuelSpent = refuelingList.get(refuelingList.size()-1).getPrice();
        } else {
            refuelingList.clear();
        }
    }

    private void clearAllData() {
        totalFuelSpent = 0;
        maxFuelSpent = 0;
        lastFuelSpent = 0;
    }

    public void notifyNewRefueling(){
        getFuelDataFromRealm();
        viewData();
    }

    public void notifyRefuelingDeleted(){
        notifyNewRefueling();
    }
}
