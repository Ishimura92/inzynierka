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
import com.example.luki.inzynierka.adapters.RefuelingslListAdapter;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.callbacks.RefuelingCallbacks;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.EFragment;
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

@EFragment(R.layout.fragment_refueling_history)
public class RefuelingHistoryFragment extends Fragment {

    @Pref
    Preferences_ preferences;

    private MainActivityCallbacks mainActivityCallbacks;
    private RefuelingCallbacks refuelingCallbacks;
    private View view;
    private TextView textViewNoRefuelings;
    private RefuelingslListAdapter adapter;
    private List<Refueling> refuelingList;
    private RecyclerView recyclerViewFuel;
    private Realm realm;
    private Vehicle currentVehicle;
    private String[] spinnerFuelTypeItems = new String[]{"PB95", "PB98", "ON", "LPG"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_refueling_history, container, false);

        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        refuelingList = new ArrayList<>();
        initViews();
        getRefuelingListFromRealm();
        setAdapter();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
        refuelingCallbacks = (RefuelingCallbacks) activity;
    }

    private void initViews() {
        recyclerViewFuel = (RecyclerView) view.findViewById(R.id.recyclerViewFuel);
        textViewNoRefuelings = (TextView) view.findViewById(R.id.textViewNoRefuelings);
    }

    private void setAdapter() {
        if (!refuelingList.isEmpty()) textViewNoRefuelings.setVisibility(View.GONE);
        sortRefuelingListByDate();
        adapter = new RefuelingslListAdapter(refuelingList, getContext(), this);
        recyclerViewFuel.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewFuel.setBackgroundColor(getResources().getColor(R.color.colorCardViewBackground));
        recyclerViewFuel.setAdapter(adapter);
    }

    public void notifyNewRefueling(Refueling refueling) {
        refuelingList.add(refueling);
        sortRefuelingListByDate();
        adapter.notifyDataSetChanged();
        textViewNoRefuelings.setVisibility(View.GONE);
    }

    private void sortRefuelingListByDate() {
        Collections.sort(refuelingList, new Comparator<Refueling>() {
            @Override
            public int compare(Refueling lhs, Refueling rhs) {
                final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                final DateTime lhsDate = dtf.parseDateTime(lhs.getDate());
                final DateTime rhsDate = dtf.parseDateTime(rhs.getDate());
                return rhsDate.compareTo(lhsDate);
            }
        });
    }

    public void deleteRefueling(int ID) {
        realm.beginTransaction();
        final RealmQuery<Refueling> query = realm.where(Refueling.class);
        query.equalTo("id", ID);
        final RealmResults<Refueling> results = query.findAll();
        results.removeLast();
        realm.commitTransaction();

        refuelingCallbacks.notifyRefuelingDeleted();
        textViewNoRefuelings.setVisibility(View.VISIBLE);
        getRefuelingListFromRealm();
        sortRefuelingListByDate();
        adapter.notifyDataSetChanged();
    }

    private void getRefuelingListFromRealm() {
        refuelingList.clear();
        realm.beginTransaction();
        final RealmQuery<Vehicle> query = realm.where(Vehicle.class).equalTo("id", currentVehicle.getId());
        final RealmResults<Vehicle> results = query.findAll();
        realm.commitTransaction();

        for (Refueling refueling : results.first().getRefuelings()) {
            refuelingList.add(refueling);
        }

        if (!refuelingList.isEmpty()) {
            textViewNoRefuelings.setVisibility(View.GONE);
        }
    }
}

