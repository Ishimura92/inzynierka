package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.adapters.ServicesListAdapter;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector;
import com.example.luki.inzynierka.dialogs.NewRepairDialog;
import com.example.luki.inzynierka.dialogs.NewServiceDialog;
import com.example.luki.inzynierka.models.Service;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.Bean;
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

@EFragment(R.layout.fragment_service)
public class ServiceFragment extends Fragment {

    @Pref
    Preferences_ preferences;

    @ViewById
    TextView textViewNoService;

    @Bean
    NewServiceDialog newServiceDialog;

    @Bean
    DatabaseConnector databaseConnector;

    private MainActivityCallbacks mainActivityCallbacks;
    private View view;
    private ServicesListAdapter adapter;
    private List<Service> serviceList;
    private RecyclerView recyclerViewService;
    private Realm realm;
    private Vehicle currentVehicle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_service, container, false);

        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        serviceList = new ArrayList<>();
        setHasOptionsMenu(true);
        initViews();
        getServiceListFromRealm();
        setDatabaseConnector();
        setAdapter();
        return view;
    }

    private void setDatabaseConnector() {
        databaseConnector.setDatabaseConnectorRealm(realm);
        databaseConnector.setCurrentVehicle(currentVehicle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
    }

    private void initViews() {
        recyclerViewService = (RecyclerView) view.findViewById(R.id.recyclerViewService);
        textViewNoService = (TextView) view.findViewById(R.id.textViewNoService);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.service_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_service:
                newServiceDialog.setCallingFragment(this);
                newServiceDialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void setAdapter() {
        if (!serviceList.isEmpty()) textViewNoService.setVisibility(View.GONE);
        sortServiceListByDate();
        adapter = new ServicesListAdapter(serviceList, getContext());
        recyclerViewService.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewService.setBackgroundColor(getResources().getColor(R.color.colorCardViewBackground));
        recyclerViewService.setAdapter(adapter);
    }

    public void notifyNewService(Service service) {
        serviceList.add(service);
        sortServiceListByDate();
        adapter.notifyDataSetChanged();
        textViewNoService.setVisibility(View.GONE);
    }

    private void sortServiceListByDate() {
        Collections.sort(serviceList, new Comparator<Service>() {
            @Override
            public int compare(Service lhs, Service rhs) {
                final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                final DateTime lhsDate = dtf.parseDateTime(lhs.getDate());
                final DateTime rhsDate = dtf.parseDateTime(rhs.getDate());
                return rhsDate.compareTo(lhsDate);
            }
        });
    }

    public void deleteService(int ID) {
        realm.beginTransaction();
        final RealmQuery<Service> query = realm.where(Service.class);
        query.equalTo("id", ID);
        final RealmResults<Service> results = query.findAll();
        results.removeLast();
        realm.commitTransaction();

        textViewNoService.setVisibility(View.VISIBLE);
        getServiceListFromRealm();
        sortServiceListByDate();
        adapter.notifyDataSetChanged();
    }

    private void getServiceListFromRealm() {
        serviceList.clear();
        realm.beginTransaction();
        final RealmQuery<Vehicle> query = realm.where(Vehicle.class).equalTo("id", currentVehicle.getId());
        final RealmResults<Vehicle> results = query.findAll();
        realm.commitTransaction();

        for (Service service : results.first().getServices()) {
            serviceList.add(service);
        }

        if (!serviceList.isEmpty()) {
            textViewNoService.setVisibility(View.GONE);
        }
    }

    public void showSavedServiceSnackbar() {
        Snackbar.make(view, R.string.service_saved, Snackbar.LENGTH_SHORT).show();

    }
}

