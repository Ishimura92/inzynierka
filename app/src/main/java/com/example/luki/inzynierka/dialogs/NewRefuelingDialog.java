package com.example.luki.inzynierka.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector_;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@EBean
public class NewRefuelingDialog extends Dialog {

    @Pref
    Preferences_ preferences;

    @Bean
    DatabaseConnector databaseConnector;

    private static final float DIALOG_WIDTH_TO_SCREEN_WIDTH_RATIO = 0.85f;
    private static final float DIALOG_HEIGHT_TO_SCREEN_HEIGHT_RATIO = 0.5f;

    private TextView textViewRefuelingDate;
    private LinearLayout refuelingDateLayout;
    private EditText editTextfuelQuantity, editTextfuelTotalCost, editTextodometerValue;
    private Spinner spinnerFuelType;
    private ImageView imageViewRefuelingDate;
    private String[] spinnerFuelTypeItems = new String[]{"PB95", "PB98", "ON", "LPG"};

    private Float liters;
    private Float price;
    private int odometer;
    private String fuelType;
    private DateTime refuelingDate;
    private DateTimeFormatter formatter;
    private String tempPrice;
    private String tempLiters;
    private String tempOdometer;
    private Calendar myCalendar;

    private DatePickerDialog.OnDateSetListener date;

    public NewRefuelingDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_refueling);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setLayoutParams();
        setupDatepicker();
        configViews();
    }

    private void configViews() {
        editTextfuelQuantity = (EditText) this.findViewById(R.id.editTextFuelQuantity);
        editTextfuelTotalCost = (EditText) this.findViewById(R.id.editTextFuelPrice);
        editTextodometerValue = (EditText) this.findViewById(R.id.editTextOdometerValue);
        textViewRefuelingDate = (TextView) this.findViewById(R.id.textViewRefuelingDate);
        spinnerFuelType = (Spinner) this.findViewById(R.id.spinnerFuelType);
        imageViewRefuelingDate = (ImageView) this.findViewById(R.id.imageViewRefuelingDate);
        refuelingDateLayout = (LinearLayout) this.findViewById(R.id.refuelingDateLayout);

        textViewRefuelingDate.setText(refuelingDate.toString("dd/MM/yyyy HH:mm"));

        formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        setSpinner();
        setOnClickListeners();
        setupOnTextChangeListeners();
    }

    private void saveRefueling(){
        clearAllErrors();
        getRefuelingDataFromDialogs();
        if(validate()){
            prepareRefuelingToAddToRealm();
            Snackbar.make(refuelingDateLayout, R.string.refuelingSaved, Snackbar.LENGTH_SHORT).show();
            this.dismiss();
        }
        clearData();
    }

    private void clearAllErrors() {
        editTextfuelQuantity.setError(null);
        editTextodometerValue.setError(null);
        editTextfuelTotalCost.setError(null);
    }

    private boolean validate(){
        int counter = 0;
        if (!validateLiters()) counter++;
        if (!validatePrice()) counter++;
        if (!validateOdometer()) counter++;

        return counter == 0;
    }

    private boolean validatePrice() {
        if(editTextfuelTotalCost.length() == 0){
            editTextfuelTotalCost.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!tempPrice.matches(getContext().getString(R.string.numberAndDotMatcher))){
            editTextfuelTotalCost.setError(getContext().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
            return false;
        }
        return true;
    }

    private boolean validateLiters() {
        if(editTextfuelQuantity.length() == 0){
            editTextfuelQuantity.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!tempLiters.matches(getContext().getString(R.string.numberAndDotMatcher))){
            editTextfuelQuantity.setError(getContext().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
            return false;
        }
        return true;
    }

    private boolean validateOdometer() {
        if(editTextodometerValue.length() == 0){
            editTextodometerValue.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!tempOdometer.matches(getContext().getString(R.string.numberAndDotMatcher))){
            editTextodometerValue.setError(getContext().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
            return false;
        }
        return true;
    }

    private void setupDatepicker() {
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        refuelingDate = DateTime.now();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textViewRefuelingDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void getRefuelingDataFromDialogs() {
        tempLiters = editTextfuelQuantity.getText().toString();
        tempPrice = editTextfuelTotalCost.getText().toString();
        tempOdometer = editTextodometerValue.getText().toString();
        fuelType = spinnerFuelType.getSelectedItem().toString();
        //TODO naprawić datę?
        refuelingDate = formatter.parseDateTime(textViewRefuelingDate.getText().toString());
    }


    private void prepareRefuelingToAddToRealm() {
        final int refuelingID = preferences.lastRefuelingID().get() + 1;
        preferences.lastRefuelingID().put(refuelingID);

        liters = Float.valueOf(tempLiters);
        price = Float.valueOf(tempPrice);
        odometer = Integer.valueOf(tempOdometer);

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        String date = dtfOut.print(refuelingDate);

        final Refueling refueling = new Refueling(refuelingID, liters, price, odometer, date, fuelType);

        databaseConnector.addNewRefuelingToRealm(refueling);
    }

    private void setOnClickListeners() {
        refuelingDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        findViewById(R.id.buttonSaveRefueling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRefueling();
            }
        });


        findViewById(R.id.buttonCancelRefueling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void clearData() {
        editTextfuelQuantity.setText("");
        editTextfuelTotalCost.setText("");
        editTextodometerValue.setText("");
        textViewRefuelingDate.setText(DateTime.now().toString("dd/MM/yyyy HH:mm"));
    }

    private void setupOnTextChangeListeners() {
        editTextfuelQuantity.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempLiters = editTextfuelQuantity.getText().toString();
                if(validateLiters()) editTextfuelQuantity.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempLiters = editTextfuelQuantity.getText().toString();
                if(validateLiters()) editTextfuelQuantity.setError(null);
            }
        });

        editTextfuelTotalCost.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempPrice = editTextfuelTotalCost.getText().toString();
                if(validatePrice()) editTextfuelTotalCost.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempPrice = editTextfuelTotalCost.getText().toString();
                if(validatePrice()) editTextfuelTotalCost.setError(null);
            }
        });

        editTextodometerValue.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempOdometer = editTextodometerValue.getText().toString();
                if(validateOdometer()) editTextodometerValue.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempOdometer = editTextodometerValue.getText().toString();
                if(validateOdometer()) editTextodometerValue.setError(null);
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
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        lp.width = (int) (metrics.widthPixels * DIALOG_WIDTH_TO_SCREEN_WIDTH_RATIO);
        lp.height = (int) (metrics.heightPixels * DIALOG_HEIGHT_TO_SCREEN_HEIGHT_RATIO);
        getWindow().setAttributes(lp);
    }
}
