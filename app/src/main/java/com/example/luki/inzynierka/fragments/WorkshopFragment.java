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
import com.example.luki.inzynierka.adapters.WorkshopsListAdapter;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector;
import com.example.luki.inzynierka.dialogs.NewWorkshopDialog;
import com.example.luki.inzynierka.models.Workshop;
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

@EFragment(R.layout.fragment_workshop)
public class WorkshopFragment extends Fragment {

    @Pref
    Preferences_ preferences;

    @ViewById
    TextView textViewNoWorkshop;

    @Bean
    NewWorkshopDialog newWorkshopDialog;

    @Bean
    DatabaseConnector databaseConnector;

    private MainActivityCallbacks mainActivityCallbacks;
    private View view;
    private WorkshopsListAdapter adapter;
    private List<Workshop> workshopList;
    private RecyclerView recyclerViewWorkshop;
    private Realm realm;
    private Vehicle currentVehicle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workshop, container, false);

        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        workshopList = new ArrayList<>();
        setHasOptionsMenu(true);
        initViews();
        getWorkshopListFromRealm();
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
        recyclerViewWorkshop = (RecyclerView) view.findViewById(R.id.recyclerViewWorkshop);
        textViewNoWorkshop = (TextView) view.findViewById(R.id.textViewNoWorkshop);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.workshop_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_workshop:
                newWorkshopDialog.setCallingFragment(this);
                newWorkshopDialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void setAdapter() {
        if (!workshopList.isEmpty()) textViewNoWorkshop.setVisibility(View.GONE);
        sortWorkshopListByName();
        adapter = new WorkshopsListAdapter(workshopList, getContext());
        recyclerViewWorkshop.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewWorkshop.setBackgroundColor(getResources().getColor(R.color.colorCardViewBackground));
        recyclerViewWorkshop.setAdapter(adapter);
    }

    public void notifyNewWorkshop(Workshop workshop) {
        workshopList.add(workshop);
        sortWorkshopListByName();
        adapter.notifyDataSetChanged();
        textViewNoWorkshop.setVisibility(View.GONE);
    }

    private void sortWorkshopListByName() {
        Collections.sort(workshopList, new Comparator<Workshop>() {
            @Override
            public int compare(Workshop lhs, Workshop rhs) {
                final String lhsName = lhs.getName();
                final String rhsName = rhs.getName();
                return lhsName.compareTo(rhsName);
            }
        });
    }

    public void deleteWorkshop(int ID) {
        realm.beginTransaction();
        final RealmQuery<Workshop> query = realm.where(Workshop.class);
        query.equalTo("id", ID);
        final RealmResults<Workshop> results = query.findAll();
        results.removeLast();
        realm.commitTransaction();

        textViewNoWorkshop.setVisibility(View.VISIBLE);
        getWorkshopListFromRealm();
        sortWorkshopListByName();
        adapter.notifyDataSetChanged();
    }

    private void getWorkshopListFromRealm() {
        workshopList.clear();
        realm.beginTransaction();
        final RealmQuery<Workshop> query = realm.where(Workshop.class);
        final RealmResults<Workshop> results = query.findAll();
        realm.commitTransaction();

        for (Workshop workshop : results) {
            workshopList.add(workshop);
        }

        if (!workshopList.isEmpty()) {
            textViewNoWorkshop.setVisibility(View.GONE);
        }
    }

    public void showSavedWorkshopSnackbar() {
        Snackbar.make(view, R.string.workshop_saved, Snackbar.LENGTH_SHORT).show();

    }
}

