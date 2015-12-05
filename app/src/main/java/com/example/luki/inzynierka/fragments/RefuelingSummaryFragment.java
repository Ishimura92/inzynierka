package com.example.luki.inzynierka.fragments;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.models.Refueling;

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
    private float totalFuelSpent = 0;
    private float maxFuelSpent = 0;
    private float lastFuelSpent = 0;

    @AfterViews
    void init() {
        realm = Realm.getInstance(getActivity());
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
        realm.beginTransaction();
        RealmQuery<Refueling> query = realm.where(Refueling.class);
        RealmResults<Refueling> results = query.findAll();
        realm.commitTransaction();

        processGatheredData(results);
    }

    private void processGatheredData(RealmResults<Refueling> results) {
        clearAllData();
        if (results.size() > 0) {
            for (Refueling refueling : results) {
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
