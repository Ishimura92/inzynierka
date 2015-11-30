package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luki.inzynierka.adapters.RefuelingslListAdapter;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@EFragment(R.layout.fragment_refueling)
public class RefuelingFragment extends Fragment{

    @Pref
    Preferences_ preferences;

    private MainActivityCallbacks mainActivityCallbacks;
    private Realm realm;
    private NewRefuelingDialog newRefuelingDialog;
    private View view;
    private TextView textViewNoRefuelings;
    private EditText editTextfuelQuantity, editTextfuelTotalCost, editTextodometerValue;
    private FloatingActionButton buttonNewRefueling;
    private Spinner spinnerFuelType;
    private RefuelingslListAdapter adapter;
    private List<Refueling> refuelingList;
    private RecyclerView recyclerViewFuel;
    private Vehicle currentVehicle;
    private String[] spinnerFuelTypeItems = new String[]{"PB95", "PB98", "ON", "LPG"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_refueling, container, false);

        realm = Realm.getInstance(getActivity());
        refuelingList = new ArrayList<>();
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        initViews();
        initOnClickListeners();
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
        buttonNewRefueling = (FloatingActionButton) view.findViewById(R.id.fabRefueling);
        recyclerViewFuel = (RecyclerView) view.findViewById(R.id.recyclerViewFuel);
        textViewNoRefuelings = (TextView) view.findViewById(R.id.textViewNoRefuelings);
    }

    private void initOnClickListeners(){
        buttonNewRefueling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRefuelingDialog = new NewRefuelingDialog(getContext());
                newRefuelingDialog.show();
                newRefuelingDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
    }

    private void setAdapter() {
        if(!refuelingList.isEmpty()) textViewNoRefuelings.setVisibility(View.GONE);
        adapter = new RefuelingslListAdapter(refuelingList);
        recyclerViewFuel.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewFuel.setBackgroundColor(getResources().getColor(R.color.colorCardViewBackground));
        recyclerViewFuel.setAdapter(adapter);
    }

    private void saveRefueling(){
        //TODO dodać kurwa walidacje

        addRefuelingToRealm();
    }

    private void addRefuelingToRealm() {
        final int refuelingID = preferences.lastRefuelingID().get() + 1;
        preferences.lastRefuelingID().put(refuelingID);
        final float liters = Float.valueOf(editTextfuelQuantity.getText().toString());
        final float price = Float.valueOf(editTextfuelTotalCost.getText().toString());
        final int odometer = Integer.valueOf(editTextodometerValue.getText().toString());
        Calendar c = Calendar.getInstance();
        final Date date = new Date(c.get(Calendar.MILLISECOND));
        final String type = spinnerFuelType.getSelectedItem().toString();

        final Refueling refueling = new Refueling(refuelingID, liters, price, odometer, date, type);

        realm.beginTransaction();
        currentVehicle.getRefuelings().add(refueling);
        realm.commitTransaction();

        refuelingList.add(refueling);
        adapter.notifyDataSetChanged();
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

    private class NewRefuelingDialog extends Dialog {

        private static final float DIALOG_WIDTH_TO_SCREEN_WIDTH_RATIO = 0.85f;
        private static final float DIALOG_HEIGHT_TO_SCREEN_HEIGHT_RATIO = 0.5f;

        NewRefuelingDialog(Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_new_refueling);
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            setLayoutParams();
            configViews();
        }

        private void configViews() {
            editTextfuelQuantity = (EditText) this.findViewById(R.id.editTextFuelQuantity);
            editTextfuelTotalCost = (EditText) this.findViewById(R.id.editTextFuelPrice);
            editTextodometerValue = (EditText) this.findViewById(R.id.editTextOdometerValue);
            spinnerFuelType = (Spinner) this.findViewById(R.id.spinnerFuelType);

            setSpinner();

            findViewById(R.id.buttonSaveRefueling).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveRefueling();
                    Snackbar.make(view, "Tankowanie zapisane", Snackbar.LENGTH_SHORT).show();
                    dismiss();
                }
            });


            findViewById(R.id.buttonCancelRefueling).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        private void setSpinner() {
            ArrayAdapter<String> fuelAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerFuelTypeItems);
            spinnerFuelType.setAdapter(fuelAdapter);
        }

        private void setLayoutParams() {
            final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            final DisplayMetrics metrics = getResources().getDisplayMetrics();
            lp.width = (int) (metrics.widthPixels * DIALOG_WIDTH_TO_SCREEN_WIDTH_RATIO);
            lp.height = (int) (metrics.heightPixels * DIALOG_HEIGHT_TO_SCREEN_HEIGHT_RATIO);
            getWindow().setAttributes(lp);
        }
    }
}

