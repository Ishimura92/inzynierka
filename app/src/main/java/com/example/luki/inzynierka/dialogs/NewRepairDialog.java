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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@EBean
public class NewRepairDialog extends Dialog {

    @Pref
    Preferences_ preferences;

    @Bean
    DatabaseConnector databaseConnector;

    private static final float DIALOG_WIDTH_TO_SCREEN_WIDTH_RATIO = 0.85f;
    private static final float DIALOG_HEIGHT_TO_SCREEN_HEIGHT_RATIO = 0.8f;

    private int repairOdometer;
    private Float repairPrice;
    private String repairTitle;
    private String fuelType;
    private String tempPrice;
    private String tempOdometer;
    private String tempDescription;
    private String tempTitle;
    private DateTime repairDate;

    private DateTimeFormatter formatter;
    private TextView textViewRepairDate;
    private EditText editTextRepairTitle;
    private EditText editTextRepairDescription;
    private EditText editTextRepairPrice;
    private EditText editTextOdometerRepairValue;
    private Button buttonSaveRepair;
    private Button buttonCancelRepair;
    private Button buttonNewPart;
    private LinearLayout repairDateLayout;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    public NewRepairDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_repair);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setLayoutParams();
        setupDatepicker();
    }

    @AfterViews
    public void configViews() {
        textViewRepairDate = (TextView) this.findViewById(R.id.textViewRepairDate);
        editTextRepairTitle = (EditText) this.findViewById(R.id.editTextRepairTitle);
        editTextRepairDescription = (EditText) this.findViewById(R.id.editTextRepairDescription);
        editTextRepairPrice = (EditText) this.findViewById(R.id.editTextRepairPrice);
        editTextOdometerRepairValue = (EditText) this.findViewById(R.id.editTextOdometerRepairValue);
        buttonSaveRepair = (Button) this.findViewById(R.id.buttonSaveRepair);
        buttonCancelRepair = (Button) this.findViewById(R.id.buttonCancelRepair);
        buttonNewPart = (Button) this.findViewById(R.id.buttonNewPart);
        repairDateLayout = (LinearLayout) this.findViewById(R.id.repairDateLayout);

        textViewRepairDate.setText(repairDate.toString("dd/MM/yyyy"));

        formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        setupOnTextChangeListeners();
        setDialogOnClickListeners();
    }

    private void saveRepair(){
        clearAllErrors();
        getRepairDataFromDialogs();
        if(validate()){
            prepareRefuelingToAddToRealm();
            Snackbar.make(repairDateLayout, R.string.repair_saved, Snackbar.LENGTH_SHORT).show();
            this.dismiss();
        }
        clearData();
    }

    private void clearAllErrors() {
        editTextRepairPrice.setError(null);
        editTextRepairTitle.setError(null);
        editTextOdometerRepairValue.setError(null);
    }

    private boolean validate(){
        int counter = 0;
        if (!validateTitle()) counter++;
        if (!validatePrice()) counter++;
        if (!validateOdometer()) counter++;
        if (!validateDescription()) counter++;

        return counter == 0;
    }

    private boolean validatePrice() {
        if(editTextRepairPrice.length() == 0){
            editTextRepairPrice.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!tempPrice.matches(getContext().getString(R.string.numberAndDotMatcher))){
            editTextRepairPrice.setError(getContext().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
            return false;
        }
        return true;
    }

    private boolean validateTitle() {
        if(editTextRepairTitle.length() == 0){
            editTextRepairTitle.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!editTextRepairTitle.getText().toString().matches(getContext().getString(R.string.lettersAndNumbersMatcher))){
            editTextRepairTitle.setError(getContext().getString(R.string.youCanEnterHereOnlyLettersAndNumbers));
            return false;
        }
        return true;
    }

    private boolean validateDescription() {
        if(!editTextRepairTitle.getText().toString().matches(getContext().getString(R.string.lettersAndNumbersMatcher))){
            editTextRepairTitle.setError(getContext().getString(R.string.youCanEnterHereOnlyLettersAndNumbers));
            return false;
        }
        return true;
    }

    private boolean validateOdometer() {
        if(editTextOdometerRepairValue.length() == 0){
            editTextOdometerRepairValue.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!tempOdometer.matches(getContext().getString(R.string.numberAndDotMatcher))){
            editTextOdometerRepairValue.setError(getContext().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
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

        repairDate = DateTime.now();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textViewRepairDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void getRepairDataFromDialogs() {
        tempPrice = editTextRepairPrice.getText().toString();
        tempOdometer = editTextOdometerRepairValue.getText().toString();
        tempTitle = editTextRepairTitle.getText().toString();
        repairDate = formatter.parseDateTime(textViewRepairDate.getText().toString());
        tempDescription = editTextRepairDescription.getText().toString();
    }


    private void prepareRefuelingToAddToRealm() {
        final int repairID = preferences.lastRepairID().get() + 1;
        preferences.lastRepairID().put(repairID);

        repairTitle = tempTitle;
        repairPrice = Float.valueOf(tempPrice);
        repairOdometer = Integer.valueOf(tempOdometer);

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
        String date = dtfOut.print(repairDate);

        final Repair repair = new Repair(repairID, repairTitle, tempDescription, repairPrice, repairOdometer, null, date, null);

        databaseConnector.addNewRepairToRealm(repair);
    }

    private void setDialogOnClickListeners() {
        repairDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        findViewById(R.id.buttonSaveRepair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRepair();
            }
        });


        findViewById(R.id.buttonCancelRepair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void clearData() {
        editTextOdometerRepairValue.setText("");
        editTextRepairPrice.setText("");
        editTextRepairTitle.setText("");
        textViewRepairDate.setText(DateTime.now().toString("dd/MM/yyyy"));
    }

    private void setupOnTextChangeListeners() {
        editTextRepairTitle.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempTitle = editTextRepairTitle.getText().toString();
                if(validateTitle()) editTextRepairTitle.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempTitle = editTextRepairTitle.getText().toString();
                if(validateTitle()) editTextRepairTitle.setError(null);
            }
        });

        editTextRepairDescription.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempDescription = editTextRepairDescription.getText().toString();
                if(validateTitle()) editTextRepairDescription.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempDescription = editTextRepairDescription.getText().toString();
                if(validateTitle()) editTextRepairDescription.setError(null);
            }
        });

        editTextRepairPrice.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempPrice = editTextRepairPrice.getText().toString();
                if(validatePrice()) editTextRepairPrice.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempPrice = editTextRepairPrice.getText().toString();
                if(validatePrice()) editTextRepairPrice.setError(null);
            }
        });

        editTextOdometerRepairValue.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempOdometer = editTextOdometerRepairValue.getText().toString();
                if (validateOdometer()) editTextOdometerRepairValue.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempOdometer = editTextOdometerRepairValue.getText().toString();
                if (validateOdometer()) editTextOdometerRepairValue.setError(null);
            }
        });
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
