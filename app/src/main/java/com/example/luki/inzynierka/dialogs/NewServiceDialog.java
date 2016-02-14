package com.example.luki.inzynierka.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector;
import com.example.luki.inzynierka.databaseUtils.Variables;
import com.example.luki.inzynierka.fragments.ServiceFragment;
import com.example.luki.inzynierka.models.Notification;
import com.example.luki.inzynierka.models.Service;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@EBean
public class NewServiceDialog extends Dialog {

    @Pref
    Preferences_ preferences;

    @Bean
    DatabaseConnector databaseConnector;

    @Bean
    Variables variables;

    private static final float DIALOG_WIDTH_TO_SCREEN_WIDTH_RATIO = 0.85f;
    private static final float DIALOG_HEIGHT_TO_SCREEN_HEIGHT_RATIO = 0.8f;
    private static final int TAKE_PHOTO_REQUEST = 200;

    private int serviceOdometer;
    private Float servicePrice;
    private Float notificationKilometers;
    private String serviceTitle;
    private String tempPrice;
    private String tempOdometer;
    private String tempDescription;
    private String tempTitle;
    private String tempNotificationKilometers;
    private DateTime serviceDate;

    private DateTimeFormatter formatter;
    private TextView textViewServiceDate;
    private EditText editTextServiceTitle;
    private EditText editTextServicePrice;
    private EditText editTextOdometerServiceValue;
    private Button buttonSaveService;
    private Button buttonCancelService;

    private Button buttonNotificationDate;
    private EditText editTextNotificationKilometers;
    private RadioButton radioButtonNotificationKilometers;
    private RadioButton radioButtonNotificationDate;
    private LinearLayout serviceDateLayout;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    private Fragment callingFragment;
    private ServiceFragment callingServiceFragment;
    private boolean isDateSetting;
    private DateTime notificationDate;

    public NewServiceDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_service);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setLayoutParams();
        setupDatepicker();
    }

    @AfterViews
    public void configViews() {
        textViewServiceDate = (TextView) this.findViewById(R.id.textViewServiceDate);
        editTextServiceTitle = (EditText) this.findViewById(R.id.editTextServiceTitle);
        editTextServicePrice = (EditText) this.findViewById(R.id.editTextServicePrice);
        editTextOdometerServiceValue = (EditText) this.findViewById(R.id.editTextOdometerServiceValue);
        buttonSaveService = (Button) this.findViewById(R.id.buttonSaveService);
        buttonCancelService = (Button) this.findViewById(R.id.buttonCancelService);

        buttonNotificationDate = (Button) this.findViewById(R.id.buttonNotificationDate);
        editTextNotificationKilometers = (EditText) this.findViewById(R.id.editTextNotificationKilometers);
        radioButtonNotificationDate = (RadioButton) this.findViewById(R.id.radioButtonNotificationDate);
        radioButtonNotificationKilometers = (RadioButton) this.findViewById(R.id.radioButtonNotificationKilometers);
        serviceDateLayout = (LinearLayout) this.findViewById(R.id.serviceDateLayout);

        textViewServiceDate.setText(serviceDate.toString("dd/MM/yyyy"));

        formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        setupOnTextChangeListeners();
        setDialogOnClickListeners();

        clearAllErrors();
    }

    private void saveService(){
        clearAllErrors();
        getServiceDataFromDialogs();
        if(validate()){
            prepareServiceToAddToRealm();
            callingServiceFragment.showSavedServiceSnackbar();
            this.dismiss();
        }
        clearData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void clearAllErrors() {
        editTextServicePrice.setError(null);
        editTextServiceTitle.setError(null);
        editTextOdometerServiceValue.setError(null);
        editTextNotificationKilometers.setError(null);
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
        if(editTextServicePrice.length() == 0){
            editTextServicePrice.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!tempPrice.matches(getContext().getString(R.string.numberAndDotMatcher))){
            editTextServicePrice.setError(getContext().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
            return false;
        }
        return true;
    }

    private boolean validateNotificationKilometers() {
        if(editTextNotificationKilometers.length() == 0 && radioButtonNotificationKilometers.isChecked()){
            editTextNotificationKilometers.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!editTextNotificationKilometers.getText().toString().matches(getContext().getString(R.string.numberAndDotMatcher))){
            editTextNotificationKilometers.setError(getContext().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
            return false;
        }
        return true;
    }

    private boolean validateTitle() {
        if(editTextServiceTitle.length() == 0){
            editTextServiceTitle.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!editTextServiceTitle.getText().toString().matches(getContext().getString(R.string.lettersAndNumbersMatcher))){
            editTextServiceTitle.setError(getContext().getString(R.string.youCanEnterHereOnlyLettersAndNumbers));
            return false;
        }
        return true;
    }

    private boolean validateDescription() {
        if(!editTextServiceTitle.getText().toString().matches(getContext().getString(R.string.lettersAndNumbersMatcher))){
            editTextServiceTitle.setError(getContext().getString(R.string.youCanEnterHereOnlyLettersAndNumbers));
            return false;
        }
        return true;
    }

    private boolean validateOdometer() {
        if(editTextOdometerServiceValue.length() == 0){
            editTextOdometerServiceValue.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!tempOdometer.matches(getContext().getString(R.string.numberAndDotMatcher))){
            editTextOdometerServiceValue.setError(getContext().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
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

                if(isDateSetting) {
                    updateLabel();
                } else {
                    updateNotificationLabel();
                }
            }
        };

        serviceDate = DateTime.now();
    }

    private void updateNotificationLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        buttonNotificationDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textViewServiceDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void getServiceDataFromDialogs() {
        tempPrice = editTextServicePrice.getText().toString();
        tempOdometer = editTextOdometerServiceValue.getText().toString();
        tempTitle = editTextServiceTitle.getText().toString();
        if(radioButtonNotificationKilometers.isChecked())
            tempNotificationKilometers = editTextNotificationKilometers.getText().toString();
        if(radioButtonNotificationDate.isChecked())
            notificationDate = formatter.parseDateTime(buttonNotificationDate.getText().toString());
        serviceDate = formatter.parseDateTime(textViewServiceDate.getText().toString());
    }


    private void prepareServiceToAddToRealm() {
        final int serviceID = preferences.lastServiceID().get() + 1;
        preferences.lastServiceID().put(serviceID);

        final int notificationID = preferences.lastNotificationID().get() + 1;
        preferences.lastRefuelingID().put(notificationID);

        serviceTitle = tempTitle;
        servicePrice = Float.valueOf(tempPrice);
        serviceOdometer = Integer.valueOf(tempOdometer);

        Notification notification = new Notification();
        notification = setupNotification(notificationID, notification);

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
        String date = dtfOut.print(serviceDate);

        final Service service = new Service(serviceID, serviceTitle, serviceOdometer, servicePrice, date, notification);
        callingServiceFragment.notifyNewService(service);

        databaseConnector.addNewServiceToRealm(service);
    }

    @Nullable
    private Notification setupNotification(int notificationID, Notification notification) {
        notification.setId(notificationID);
        notification.setName(serviceTitle);

        if(radioButtonNotificationKilometers.isChecked()){
            notificationKilometers = Float.valueOf(tempNotificationKilometers);
            notification.setIsDateNotification(false);
            notification.setKilometers(serviceOdometer + notificationKilometers);
        } else if (radioButtonNotificationDate.isChecked()) {
            notification.setIsDateNotification(true);
            final DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
            final String date = dtfOut.print(notificationDate);
            notification.setDate(date);
        } else {
            notification = null;
        }
        return notification;
    }

    private void setDialogOnClickListeners() {
        serviceDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDateSetting = true;
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        findViewById(R.id.buttonSaveService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveService();
            }
        });


        findViewById(R.id.buttonCancelService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonNotificationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDateSetting = false;
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        radioButtonNotificationKilometers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonNotificationDate.setChecked(false);
            }
        });

        radioButtonNotificationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonNotificationKilometers.setChecked(false);
            }
        });
    }

    private void clearData() {
        editTextOdometerServiceValue.setText("");
        editTextServicePrice.setText("");
        editTextServiceTitle.setText("");
        textViewServiceDate.setText(DateTime.now().toString("dd/MM/yyyy"));
        editTextNotificationKilometers.setText("");
        buttonNotificationDate.setText("Wybierz datę");
    }

    private void setupOnTextChangeListeners() {
        editTextServiceTitle.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempTitle = editTextServiceTitle.getText().toString();
                if (validateTitle()) editTextServiceTitle.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempTitle = editTextServiceTitle.getText().toString();
                if (validateTitle()) editTextServiceTitle.setError(null);
            }
        });

        editTextNotificationKilometers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tempNotificationKilometers = editTextNotificationKilometers.getText().toString();
                if (validateNotificationKilometers()) editTextNotificationKilometers.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tempNotificationKilometers = editTextNotificationKilometers.getText().toString();
                if (validateNotificationKilometers()) editTextNotificationKilometers.setError(null);
            }
        });

        editTextServicePrice.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempPrice = editTextServicePrice.getText().toString();
                if (validatePrice()) editTextServicePrice.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempPrice = editTextServicePrice.getText().toString();
                if (validatePrice()) editTextServicePrice.setError(null);
            }
        });

        editTextOdometerServiceValue.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempOdometer = editTextOdometerServiceValue.getText().toString();
                if (validateOdometer()) editTextOdometerServiceValue.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempOdometer = editTextOdometerServiceValue.getText().toString();
                if (validateOdometer()) editTextOdometerServiceValue.setError(null);
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

    public Fragment getCallingFragment() {
        return callingFragment;
    }

    public void setCallingFragment(Fragment callingFragment) {
        this.callingFragment = callingFragment;
        this.callingServiceFragment = (ServiceFragment) callingFragment;
    }

    public void setCallingServiceFragment(ServiceFragment serviceFragment) {
        this.callingServiceFragment = serviceFragment;
    }
}
