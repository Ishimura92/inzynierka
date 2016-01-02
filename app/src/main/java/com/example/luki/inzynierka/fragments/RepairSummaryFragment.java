package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;
import com.example.luki.inzynierka.models.Vehicle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
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

@EFragment(R.layout.fragment_repair_summary)
public class RepairSummaryFragment extends Fragment{

    @ViewById
    TextView textViewTotalRepairSpent;
    @ViewById
    TextView textViewLastRepairSpent;
    @ViewById
    TextView textViewMaxRepairSpent;

    private float maxRepairSpent, totalRepairSpent, lastRepairSpent;

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
        if (maxRepairSpent == 0) {
            textViewMaxRepairSpent.setText(getActivity().getString(R.string.noDataSlash));
        } else {
            textViewMaxRepairSpent.setText(String.valueOf(maxRepairSpent) + getActivity().getString(R.string.zlotysShortcut));
        }

        if (totalRepairSpent == 0) {
            textViewTotalRepairSpent.setText(getActivity().getString(R.string.noDataSlash));
        } else {
            textViewTotalRepairSpent.setText(String.valueOf(totalRepairSpent) + getActivity().getString(R.string.zlotysShortcut));
        }

        if (lastRepairSpent == 0) {
            textViewLastRepairSpent.setText(getActivity().getString(R.string.noDataSlash));
        } else {
            textViewLastRepairSpent.setText(String.valueOf(lastRepairSpent) + getActivity().getString(R.string.zlotysShortcut));
        }
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
        clearAllData();
        if (results.first().getRepairs().size() > 0) {
            for (Repair repair : results.first().getRepairs()) {
                repairList.add(repair);
                totalRepairSpent += repair.getTotalCost();
                if (repair.getTotalCost() > maxRepairSpent) {
                    maxRepairSpent = repair.getTotalCost();
                }
            }
            sortRefuelingListByDate();
            lastRepairSpent = repairList.get(0).getTotalCost();
        } else {
            repairList.clear();
        }
    }

    private void sortRefuelingListByDate() {
        Collections.sort(repairList, new Comparator<Repair>() {
            @Override
            public int compare(Repair lhs, Repair rhs) {
                final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                final DateTime lhsDate = dtf.parseDateTime(lhs.getDate());
                final DateTime rhsDate = dtf.parseDateTime(rhs.getDate());
                return rhsDate.compareTo(lhsDate);
            }
        });
    }

    private void clearAllData() {
        totalRepairSpent = 0;
        maxRepairSpent = 0;
        lastRepairSpent = 0;
    }

    public void notifyNewRepair(){
        getRepairDataFromRealm();
        viewData();
    }

    public void notifyRepairDeleted(){
        notifyNewRepair();
    }
}
