package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.adapters.RefuelingsListAdapter;
import com.example.luki.inzynierka.adapters.RepairsListAdapter;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.callbacks.RepairCallbacks;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
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

@EFragment(R.layout.fragment_repair_history)
public class RepairHistoryFragment extends Fragment {

    @Pref
    Preferences_ preferences;

    @ViewById
    TextView textViewNoRepairs;

    private MainActivityCallbacks mainActivityCallbacks;
    private RepairCallbacks repairCallbacks;
    private View view;
    private RepairsListAdapter adapter;
    private List<Repair> repairList;
    private RecyclerView recyclerViewRepair;
    private Realm realm;
    private Vehicle currentVehicle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_repair_history, container, false);

        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        repairList = new ArrayList<>();
        initViews();
        getRepairListFromRealm();
        setAdapter();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
        repairCallbacks = (RepairCallbacks) activity;
    }

    private void initViews() {
        recyclerViewRepair = (RecyclerView) view.findViewById(R.id.recyclerViewRepair);
        textViewNoRepairs = (TextView) view.findViewById(R.id.textViewNoRefuelings);
    }

    private void setAdapter() {
        if (!repairList.isEmpty()) textViewNoRepairs.setVisibility(View.GONE);
        sortRepairListByDate();
        adapter = new RepairsListAdapter(repairList, getContext(), this);
        recyclerViewRepair.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewRepair.setBackgroundColor(getResources().getColor(R.color.colorCardViewBackground));
        recyclerViewRepair.setAdapter(adapter);
    }

    public void notifyNewRepair(Repair repair) {
        repairList.add(repair);
        sortRepairListByDate();
        adapter.notifyDataSetChanged();
        textViewNoRepairs.setVisibility(View.GONE);
    }

    private void sortRepairListByDate() {
//        Collections.sort(repairList, new Comparator<Refueling>() {
//            @Override
//            public int compare(Refueling lhs, Refueling rhs) {
//                final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
//                final DateTime lhsDate = dtf.parseDateTime(lhs.getDate());
//                final DateTime rhsDate = dtf.parseDateTime(rhs.getDate());
//                return rhsDate.compareTo(lhsDate);
//            }
//        });
    }

    public void deleteRepair(int ID) {
        realm.beginTransaction();
        final RealmQuery<Refueling> query = realm.where(Refueling.class);
        query.equalTo("id", ID);
        final RealmResults<Refueling> results = query.findAll();
        results.removeLast();
        realm.commitTransaction();

        repairCallbacks.notifyRepairgDeleted();
        textViewNoRepairs.setVisibility(View.VISIBLE);
        getRepairListFromRealm();
        sortRepairListByDate();
        adapter.notifyDataSetChanged();
    }

    private void getRepairListFromRealm() {
        repairList.clear();
        realm.beginTransaction();
        final RealmQuery<Vehicle> query = realm.where(Vehicle.class).equalTo("id", currentVehicle.getId());
        final RealmResults<Vehicle> results = query.findAll();
        realm.commitTransaction();

        for (Repair repair : results.first().getRepairs()) {
            repairList.add(repair);
        }

        if (!repairList.isEmpty()) {
            textViewNoRepairs.setVisibility(View.GONE);
        }
    }
}

