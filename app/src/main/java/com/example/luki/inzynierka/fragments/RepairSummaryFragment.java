package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;
import com.example.luki.inzynierka.models.Vehicle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@EFragment(R.layout.fragment_refueling_summary)
public class RepairSummaryFragment extends Fragment{

    private Realm realm;
    private List<Repair> repairList = new ArrayList<>();
    private MainActivityCallbacks mainActivityCallbacks;
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
        getRepairDataFromRealm();
        viewData();
    }

    private void viewData() {
//        if (maxFuelSpent == 0) {
//            textViewMaxFuelSpent.setText(getActivity().getString(R.string.noDataSlash));
//        } else {
//            textViewMaxFuelSpent.setText(String.valueOf(maxFuelSpent) + getActivity().getString(R.string.zlotysShortcut));
//        }
//
//        if (totalFuelSpent == 0) {
//            textViewTotalFuelSpent.setText(getActivity().getString(R.string.noDataSlash));
//        } else {
//            textViewTotalFuelSpent.setText(String.valueOf(totalFuelSpent) + getActivity().getString(R.string.zlotysShortcut));
//        }
//
//        if (lastFuelSpent == 0) {
//            textViewLastRefuelingSpent.setText(getActivity().getString(R.string.noDataSlash));
//        } else {
//            textViewLastRefuelingSpent.setText(String.valueOf(lastFuelSpent) + getActivity().getString(R.string.zlotysShortcut));
//        }
    }

    private void getRepairDataFromRealm() {
        repairList.clear();
        realm.beginTransaction();
        RealmQuery<Vehicle> query = realm.where(Vehicle.class).equalTo("id", currentVehicle.getId());
        RealmResults<Vehicle> results = query.findAll();
        realm.commitTransaction();

        processGatheredData(results);
    }

    private void processGatheredData(RealmResults<Vehicle> results) {
//        clearAllData();
//        if (results.first().getRepairs().size() > 0) {
//            for (Repair repair : results.first().getRepairs()) {
//                repairList.add(repair);
//                totalFuelSpent += repair.getTotalCost();
//                if (repair.getTotalCost() > maxFuelSpent) {
//                    maxFuelSpent = repair.getTotalCost();
//                }
//            }
//            sortRefuelingListByDate();
//            lastFuelSpent = repairList.get(0).getTotalCost();
//        } else {
//            repairList.clear();
//        }
    }

    private void sortRefuelingListByDate() {
        Collections.sort(repairList, new Comparator<Repair>() {
            @Override
            public int compare(Repair lhs, Repair rhs) {
                final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
                final DateTime lhsDate = dtf.parseDateTime(lhs.getDate());
                final DateTime rhsDate = dtf.parseDateTime(rhs.getDate());
                return rhsDate.compareTo(lhsDate);
            }
        });
    }

    private void clearAllData() {
//        totalFuelSpent = 0;
//        maxFuelSpent = 0;
//        lastFuelSpent = 0;
    }

    public void notifyNewRepair(){
        getRepairDataFromRealm();
        viewData();
    }

    public void notifyRepairDeleted(){
        notifyNewRepair();
    }
}
