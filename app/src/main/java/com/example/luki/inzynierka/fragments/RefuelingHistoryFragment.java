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
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@EFragment(R.layout.fragment_refueling_history)
public class RefuelingHistoryFragment extends Fragment{

    @Pref
    Preferences_ preferences;

    private MainActivityCallbacks mainActivityCallbacks;
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
    }

    private void initViews(){
        recyclerViewFuel = (RecyclerView) view.findViewById(R.id.recyclerViewFuel);
        textViewNoRefuelings = (TextView) view.findViewById(R.id.textViewNoRefuelings);
    }

    private void setAdapter() {
        if(!refuelingList.isEmpty()) textViewNoRefuelings.setVisibility(View.GONE);
        adapter = new RefuelingslListAdapter(refuelingList);
        recyclerViewFuel.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewFuel.setBackgroundColor(getResources().getColor(R.color.colorCardViewBackground));
        recyclerViewFuel.setAdapter(adapter);
    }

    public void notifyNewRefueling(Refueling refueling){
        refuelingList.add(refueling);
        adapter.notifyDataSetChanged();
        textViewNoRefuelings.setVisibility(View.GONE);
    }

    private void getRefuelingListFromRealm(){
        refuelingList.clear();
        realm.beginTransaction();
        RealmQuery<Vehicle> query = realm.where(Vehicle.class).equalTo("id", currentVehicle.getId());
        RealmResults<Vehicle> results = query.findAll();
        realm.commitTransaction();

        for(Refueling refueling : results.first().getRefuelings()){
            refuelingList.add(refueling);
        }
    }
}

