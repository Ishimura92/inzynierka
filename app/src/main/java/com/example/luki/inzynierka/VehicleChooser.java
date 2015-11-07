package com.example.luki.inzynierka;

import android.app.DatePickerDialog;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luki.inzynierka.Adapters.VehicleListAdapter;
import com.example.luki.inzynierka.Models.Refueling;
import com.example.luki.inzynierka.Models.Repair;
import com.example.luki.inzynierka.Models.Service;
import com.example.luki.inzynierka.Models.Vehicle;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@EActivity(R.layout.activity_vehicle_chooser)
public class VehicleChooser extends AppCompatActivity{

    @ViewById
    ScrollView vehicleAddLayout;
    @ViewById
    RecyclerView recyclerView;
    @ViewById
    FloatingActionButton fab;

    @ViewById
    EditText textDialogProductionDate;
    @ViewById
    EditText textDialogVehicleBrand;
    @ViewById
    EditText textDialogVehicleModel;
    @ViewById
    EditText textDialogEngineCapacity;
    @ViewById
    EditText textDialogOdometer;
    @ViewById
    Spinner spinnerTop;
    @ViewById
    Spinner spinnerEngine;

    @ViewById
    Button buttonCancelAdd;
    @ViewById
    Button buttonConfirmAdd;
    @ViewById
    TextView textViewNoVehicles;

    @ViewById
    CoordinatorLayout coordinatorLayout;
    @ViewById
    ImageView imageViewCalendar;

    private Realm realm;
    private Calendar myCalendar;
    private List<Vehicle> vehicleList;
    private VehicleListAdapter adapter;
    private DatePickerDialog.OnDateSetListener date;
    private String[] spinnerTopItems = new String[]{"Kombi", "Sedan", "Minivan/Van", "Coupe/Kabrio", "SUV", "Hatchback"};
    private String[] spinnerEngineItems = new String[]{"Benzyna", "Benzyna + gaz", "Diesel"};
    private String vehicleBrand;
    private String vehicleModel;
    private Float engineCapacity;
    private Float odometer;
    private Date productionDate;
    private String top;
    private String engine;
    private String tempOdometer, tempEngineCapacity, tempProductionDate;

    private RealmList<Refueling> refuelings = new RealmList<>();
    private RealmList<Repair> repairs = new RealmList<>();
    private RealmList<Service> services = new RealmList<>();

    @AfterViews
    void init(){
        setRealm();
        vehicleList = new ArrayList<>();
        //generateVehiclesToTest();
        setSpinners();
        getVehicleListFromRealm();
        setAdapter();
        setupDatepicker();
    }

    private void setSpinners() {
        ArrayAdapter<String> topAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerTopItems);
        ArrayAdapter<String> engineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerEngineItems);
        spinnerTop.setAdapter(topAdapter);
        spinnerEngine.setAdapter(engineAdapter);
    }

    private void setupDatepicker() {
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
    }
    @Click(R.id.vehicleAddLayout)
    void onLayoutClick(){
        textDialogEngineCapacity.clearFocus();
        textDialogOdometer.clearFocus();
        textDialogProductionDate.clearFocus();
        textDialogVehicleBrand.clearFocus();
        textDialogVehicleModel.clearFocus();
    }

    @Click(R.id.buttonConfirmAdd)
    void onConfirmClick(){
        disableAllErrors();

        getVehicleDataFromDialogs();

        if(validateVehicleData()) {
            odometer = Float.valueOf(tempOdometer);
            engineCapacity = Float.valueOf(tempEngineCapacity);
            productionDate = new Date(tempProductionDate);

            Vehicle newVehicle = new Vehicle(3, vehicleBrand, vehicleModel, productionDate, "0xffffff",
                    engine, engineCapacity, odometer, top, refuelings, repairs, services);

            realm.beginTransaction();
            realm.copyToRealm(newVehicle);
            realm.commitTransaction();

            vehicleList.add(newVehicle);
            adapter.notifyDataSetChanged();
            if(!vehicleList.isEmpty()) textViewNoVehicles.setVisibility(View.GONE);
            onCancelClick();
            clearDialogs();
            disableAllErrors();
            Snackbar.make(recyclerView, "Dodano pojazd", Snackbar.LENGTH_LONG).show();
        }
    }

    private void clearDialogs() {
        textDialogVehicleBrand.setText("");
        textDialogVehicleModel.setText("");
        textDialogProductionDate.setText("");
        textDialogOdometer.setText("");
        textDialogEngineCapacity.setText("");
        spinnerEngine.setSelected(false);
        spinnerTop.setSelected(false);
    }

    @AfterTextChange(R.id.textDialogProductionDate)
    void afterProductionDateTextChange(Editable text, TextView hello){
        tempProductionDate = textDialogProductionDate.getText().toString();
        if(validateProductionDate()) textDialogProductionDate.setError(null);
    }

    @AfterTextChange(R.id.textDialogEngineCapacity)
    void afterEngineCapacityTextChange(Editable text, TextView hello){
        tempEngineCapacity = textDialogEngineCapacity.getText().toString();
        if(validateEngineCapacity()) textDialogEngineCapacity.setError(null);
    }

    @AfterTextChange(R.id.textDialogOdometer)
    void afterOdometerTextChange(Editable text, TextView hello){
        tempOdometer = textDialogOdometer.getText().toString();
        if(validateOdometer()) textDialogOdometer.setError(null);
    }

    @AfterTextChange(R.id.textDialogVehicleBrand)
    void afterVehicleBrandTextChange(Editable text, TextView hello){
        vehicleBrand = textDialogVehicleBrand.getText().toString();
        if(validateBrand()) textDialogVehicleBrand.setError(null);
    }

    @AfterTextChange(R.id.textDialogVehicleModel)
    void afterVehicleModelTextChange(Editable text, TextView hello){
        vehicleModel = textDialogVehicleModel.getText().toString();
        if(validateModel()) textDialogVehicleModel.setError(null);
    }

    private void getVehicleDataFromDialogs() {
        vehicleBrand = textDialogVehicleBrand.getText().toString();
        vehicleModel = textDialogVehicleModel.getText().toString();
        tempProductionDate = textDialogProductionDate.getText().toString();
        tempOdometer = textDialogOdometer.getText().toString();
        tempEngineCapacity = textDialogEngineCapacity.getText().toString();
        engine = spinnerEngineItems[spinnerEngine.getSelectedItemPosition()];
        top = spinnerTopItems[spinnerTop.getSelectedItemPosition()];
    }

    private void disableAllErrors(){
        textDialogVehicleBrand.setError(null);
        textDialogVehicleModel.setError(null);
        textDialogOdometer.setError(null);
        textDialogEngineCapacity.setError(null);
        textDialogProductionDate.setError(null);
    }

    private boolean validateVehicleData() {
        int counter = 0;
        if (!validateBrand()) counter++;
        if (!validateModel()) counter++;
        if (!validateOdometer()) counter++;
        if (!validateEngineCapacity()) counter++;
        if (!validateProductionDate()) counter++;

        return counter == 0;
    }

    private boolean validateProductionDate() {
        if(tempProductionDate.length() == 0){
            textDialogProductionDate.setError("Wybierz datę");
            return false;
        }
        return true;
    }

    private boolean validateEngineCapacity() {
        if(tempEngineCapacity.length() == 0){
            textDialogEngineCapacity.setError("Wypełnij pole");
            return false;
        }else if(!tempEngineCapacity.matches("\\d+(\\.\\d+)*")){
            textDialogEngineCapacity.setError("Możesz tu wpisać tylko cyfry");
            return false;
        }
        return true;
    }

    private boolean validateOdometer() {
        if(tempOdometer.length() == 0){
            textDialogOdometer.setError("Wypełnij pole");
            return false;
        }else if(!tempOdometer.matches("[0-9]+")){
            textDialogOdometer.setError("Możesz tu wpisać tylko cyfry");
            return false;
        }
        return true;
    }

    private boolean validateModel() {
        if(vehicleModel.length() == 0){
            textDialogVehicleModel.setError("Wypełnij pole");
            return false;
        }else if(!vehicleModel.matches("[a-zA-Z0-9]+")){
            textDialogVehicleModel.setError("Możesz tu wpisać tylko litery i cyfry");
            return false;
        }
        return true;
    }

    @Nullable
    private Boolean validateBrand() {
        if(vehicleBrand.length() == 0){
            textDialogVehicleBrand.setError("Wypełnij pole");
            return false;
        }else if(!vehicleBrand.matches("[a-zA-Z]+")){
            textDialogVehicleBrand.setError("Możesz tu wpisać tylko litery");
            return false;
        }
        return true;
    }

    @Click(R.id.buttonCancelAdd)
    void onCancelClick() {
        vehicleAddLayout.setVisibility(View.INVISIBLE);
        coordinatorLayout.setEnabled(true);
    }

    @Click(R.id.imageViewCalendar)
    void onDateClick(){
        new DatePickerDialog(VehicleChooser.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textDialogProductionDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void setAdapter() {
        if(!vehicleList.isEmpty()) textViewNoVehicles.setVisibility(View.GONE);
        adapter = new VehicleListAdapter(vehicleList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    @Click(R.id.fab)
    void onFabClick(){
        vehicleAddLayout.setVisibility(View.VISIBLE);
        coordinatorLayout.setEnabled(false);
    }

    private void getVehicleListFromRealm(){
        realm.beginTransaction();
        RealmQuery<Vehicle> query = realm.where(Vehicle.class);
        RealmResults<Vehicle> results = query.findAll();
        realm.commitTransaction();

        for(Vehicle vehicle : results){
            vehicleList.add(vehicle);
        }

    }

    private void generateVehiclesToTest(){

        Vehicle pierwszy = new Vehicle(1, "Audi", "Coupe", new Date(1994,11,11), "0xffff", "Benzyna + gaz", 2.0f, 22345, "Coupe", refuelings, repairs, services);
        Vehicle drugi = new Vehicle(2, "Opel", "Zafira", new Date(2006,1,19), "0xffff", "Diesel", 1.9f, 31901, "Van", refuelings, repairs, services);
//        vehicleList.add(pierwszy);
//        vehicleList.add(drugi);
//
//        Vehicle pierwszy1 = new Vehicle(1, "Audi", "Coupe", new Date(1994,11,11), "0xffff", "Benzyna + gaz", 2.0f, 22345, "Coupe", refuelings, repairs, services);
//        Vehicle drugi1 = new Vehicle(2, "Opel", "Zafira", new Date(2006,1,19), "0xffff", "Diesel", 1.9f, 31901, "Van", refuelings, repairs, services);
//        Vehicle pierwszy2 = new Vehicle(1, "Audi", "Coupe", new Date(1994,11,11), "0xffff", "Benzyna + gaz", 2.0f, 22345, "Coupe", refuelings, repairs, services);
//        Vehicle drugi2 = new Vehicle(2, "Opel", "Zafira", new Date(2006,1,19), "0xffff", "Diesel", 1.9f, 31901, "Van", refuelings, repairs, services);
//        Vehicle pierwszy3 = new Vehicle(1, "Audi", "Coupe", new Date(1994,11,11), "0xffff", "Benzyna + gaz", 2.0f, 22345, "Coupe", refuelings, repairs, services);
//        Vehicle drugi3 = new Vehicle(2, "Opel", "Zafira", new Date(2006,1,19), "0xffff", "Diesel", 1.9f, 31901, "Van", refuelings, repairs, services);
//        vehicleList.add(pierwszy1);
//        vehicleList.add(drugi1);
//        vehicleList.add(pierwszy2);
//        vehicleList.add(drugi2);
//        vehicleList.add(pierwszy3);
//        vehicleList.add(drugi3);

        realm.beginTransaction();
        realm.copyToRealm(pierwszy);
        realm.copyToRealm(drugi);
        realm.commitTransaction();
    }
}
