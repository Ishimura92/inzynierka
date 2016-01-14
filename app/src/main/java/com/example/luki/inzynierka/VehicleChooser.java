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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luki.inzynierka.adapters.VehicleListAdapter;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;
import com.example.luki.inzynierka.models.Service;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

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
public class VehicleChooser extends AppCompatActivity {

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
    LinearLayout addVehicleDateLayout;
    @ViewById
    CoordinatorLayout coordinatorLayout;
    @ViewById
    ImageView imageViewCalendar;

    @Pref
    Preferences_ preferences;

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
    private boolean inEditMode;

    private RealmList<Refueling> refuelings = new RealmList<>();
    private RealmList<Repair> repairs = new RealmList<>();
    private RealmList<Service> services = new RealmList<>();
    private Vehicle editedVehicle;

    @AfterViews
    void init(){
        setRealm();
        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.garage);
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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getVehicleDataFromDialogs();

        if(validateVehicleData()) {
            if (!inEditMode) {
                addNewVehicle();
            } else {
                updateVehicle();
            }
        }
    }

    private void updateVehicle() {
        odometer = Float.valueOf(tempOdometer);
        engineCapacity = Float.valueOf(tempEngineCapacity);
        productionDate = new Date(tempProductionDate);

        realm.beginTransaction();
        editedVehicle = new Vehicle(editedVehicle.getId(), vehicleBrand, vehicleModel, productionDate, "0xffffff",
                engine, engineCapacity, odometer, top, refuelings, repairs, services);
        setVehicleImage(editedVehicle);
        realm.copyToRealmOrUpdate(editedVehicle);
        realm.commitTransaction();

        getVehicleListFromRealm();
        adapter.notifyDataSetChanged();

        buttonConfirmAdd.setText(R.string.add);
        onCancelClick();
        clearDialogs();
        disableAllErrors();
        inEditMode = false;
        Snackbar.make(recyclerView, R.string.changesSaved, Snackbar.LENGTH_LONG).show();
    }

    private void addNewVehicle() {
        odometer = Float.valueOf(tempOdometer);
        engineCapacity = Float.valueOf(tempEngineCapacity);
        productionDate = new Date(tempProductionDate);
        final int vehicleID = preferences.lastVehicleID().get() + 1;
        preferences.lastVehicleID().put(vehicleID);

        Vehicle newVehicle = new Vehicle(vehicleID, vehicleBrand, vehicleModel, productionDate, "0xffffff",
                engine, engineCapacity, odometer, top, refuelings, repairs, services);

        setVehicleImage(newVehicle);

        realm.beginTransaction();
        realm.copyToRealm(newVehicle);
        realm.commitTransaction();

        vehicleList.add(newVehicle);
        adapter.notifyDataSetChanged();
        if(!vehicleList.isEmpty()) textViewNoVehicles.setVisibility(View.GONE);
        onCancelClick();
        clearDialogs();
        disableAllErrors();
        Snackbar.make(recyclerView, R.string.vehicleAdded, Snackbar.LENGTH_LONG).show();
    }

    private void setVehicleImage(Vehicle newVehicle) {
        switch (newVehicle.getBodyType()){
            case "Sedan":
                newVehicle.setImage(R.drawable.ic_sedan_dark);
                break;
            case "Kombi":
                newVehicle.setImage(R.drawable.ic_kombi_dark);
                break;
            case "Minivan/Van":
                newVehicle.setImage(R.drawable.ic_van_dark);
                break;
            case "Coupe/Kabrio":
                newVehicle.setImage(R.drawable.ic_coupe_dark);
                break;
            case "SUV":
                newVehicle.setImage(R.drawable.ic_suv_dark);
                break;
            case "Hatchback":
                newVehicle.setImage(R.drawable.ic_hatchback_dark);
                break;
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
            textDialogProductionDate.setError(getString(R.string.chooseDate));
            return false;
        }
        return true;
    }

    private boolean validateEngineCapacity() {
        if(tempEngineCapacity.length() == 0){
            textDialogEngineCapacity.setError(getString(R.string.fillField));
            return false;
        }else if(!tempEngineCapacity.matches(getString(R.string.numberAndDotMatcher))){
            textDialogEngineCapacity.setError("Możesz tu wpisać tylko cyfry i kropkę");
            return false;
        }
        return true;
    }

    private boolean validateOdometer() {
        if(tempOdometer.length() == 0){
            textDialogOdometer.setError(getString(R.string.fillField));
            return false;
        }else if(!tempOdometer.matches(getString(R.string.numberAndDotMatcher))){
            textDialogOdometer.setError("Możesz tu wpisać tylko cyfry i kropkę");
            return false;
        }
        return true;
    }

    private boolean validateModel() {
        if(vehicleModel.length() == 0){
            textDialogVehicleModel.setError(getString(R.string.fillField));
            return false;
        }else if(!vehicleModel.matches(getString(R.string.lettersAndNumbersMatcher))){
            textDialogVehicleModel.setError(getString(R.string.youCanEnterHereOnlyLettersAndNumbers));
            return false;
        }
        return true;
    }

    @Nullable
    private Boolean validateBrand() {
        if(vehicleBrand.length() == 0){
            textDialogVehicleBrand.setError(getString(R.string.fillField));
            return false;
        }else if(!vehicleBrand.matches(getString(R.string.lettersMatcher))){
            textDialogVehicleBrand.setError(getString(R.string.youCanEnterHereOnlyLetters));
            return false;
        }
        return true;
    }

    @Click(R.id.buttonCancelAdd)
    void onCancelClick() {
        vehicleAddLayout.setVisibility(View.INVISIBLE);
        coordinatorLayout.setEnabled(true);
        if(inEditMode) {
            buttonConfirmAdd.setText(getString(R.string.add));
            clearDialogs();
            disableAllErrors();
            inEditMode = false;
        }
    }

    @Click({R.id.addVehicleDateLayout, R.id.imageViewCalendar})
    void onDateClick(){
        new DatePickerDialog(VehicleChooser.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onBackPressed() {
        if(vehicleAddLayout.isShown()){
            onCancelClick();
        }
        else this.finishAffinity();
        //TODO dodać info, że jeśli się kliknie dwa razy to dopiero wyjść
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
        recyclerView.setBackgroundColor(getResources().getColor(R.color.colorCardViewBackground));
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
        vehicleList.clear();
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
        vehicleList.add(pierwszy);
        vehicleList.add(drugi);

        realm.beginTransaction();
        realm.copyToRealm(pierwszy);
        realm.copyToRealm(drugi);
        realm.commitTransaction();
    }

    public void deleteVehicle(int id) {
        realm.beginTransaction();
        RealmQuery<Vehicle> query = realm.where(Vehicle.class);
        query.equalTo("id", id);
        RealmResults<Vehicle> results = query.findAll();
        results.removeLast();
        realm.commitTransaction();

        getVehicleListFromRealm();
        if(vehicleList.isEmpty()) textViewNoVehicles.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();

    }

    public void editVehicle(int id) {
        inEditMode = true;
        vehicleAddLayout.setVisibility(View.VISIBLE);
        coordinatorLayout.setEnabled(false);

        buttonConfirmAdd.setText(R.string.save);

        realm.beginTransaction();
        RealmQuery<Vehicle> query = realm.where(Vehicle.class);
        query.equalTo("id", id);
        RealmResults<Vehicle> results = query.findAll();
        editedVehicle = results.first();
        realm.commitTransaction();

        textDialogVehicleBrand.setText(editedVehicle.getBrand());
        textDialogVehicleModel.setText(editedVehicle.getModel());
        textDialogProductionDate.setText(editedVehicle.getProductionDate().toString());
        //TODO pattern na date!
        textDialogOdometer.setText(String.valueOf(editedVehicle.getOdometer()));
        textDialogEngineCapacity.setText(String.valueOf(editedVehicle.getEngineCapacity()));
        //TODO ustawić spinnery
    }
}
