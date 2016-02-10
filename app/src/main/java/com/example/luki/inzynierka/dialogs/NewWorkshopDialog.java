package com.example.luki.inzynierka.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.databaseUtils.DatabaseConnector;
import com.example.luki.inzynierka.databaseUtils.Variables;
import com.example.luki.inzynierka.fragments.WorkshopFragment;
import com.example.luki.inzynierka.models.Workshop;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@EBean
public class NewWorkshopDialog extends Dialog {

    @Pref
    Preferences_ preferences;

    @Bean
    DatabaseConnector databaseConnector;

    @Bean
    Variables variables;

    private static final float DIALOG_WIDTH_TO_SCREEN_WIDTH_RATIO = 0.85f;
    private static final float DIALOG_HEIGHT_TO_SCREEN_HEIGHT_RATIO = 0.8f;

    private String tempTitle;
    private String tempFirstName;
    private String tempSurname;
    private String tempAddress;
    private String tempPhone;

    private DateTimeFormatter formatter;
    private EditText editTextWorkshopFirstName;
    private EditText editTextWorkshopTitle;
    private EditText editTextWorkshopSurname;
    private EditText editTextWorkshopAddress;
    private EditText editTextWorkshopPhone;

    private Button buttonSaveWorkshop;
    private Button buttonCancelWorkshop;

    private Fragment callingFragment;
    private WorkshopFragment callingWorkshopFragment;

    public NewWorkshopDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_workshop);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        configViews();
        setLayoutParams();
    }

    @AfterViews
    public void configViews() {
        editTextWorkshopTitle = (EditText) this.findViewById(R.id.editTextWorkshopTitle);
        editTextWorkshopAddress = (EditText) this.findViewById(R.id.editTextWorkshopAddress);
        editTextWorkshopFirstName = (EditText) this.findViewById(R.id.editTextWorkshopFirstName);
        editTextWorkshopSurname = (EditText) this.findViewById(R.id.editTextWorkshopSurname);
        editTextWorkshopPhone = (EditText) this.findViewById(R.id.editTextWorkshopPhone);

        buttonSaveWorkshop = (Button) this.findViewById(R.id.buttonSaveWorkshop);
        buttonCancelWorkshop = (Button) this.findViewById(R.id.buttonCancelWorkshop);

        formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        setupOnTextChangeListeners();
        setDialogOnClickListeners();
    }

    private void saveWorkshop(){
        clearAllErrors();
        getWorkshopDataFromDialogs();
        if(validate()){
            prepareWorkshopToAddToRealm();
            callingWorkshopFragment.showSavedWorkshopSnackbar();
            this.dismiss();
        }
        clearData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        clearAllErrors();
    }

    private void clearAllErrors() {
        editTextWorkshopTitle.setError(null);
    }

    private boolean validate(){
        int counter = 0;
        if (!validateText(editTextWorkshopTitle)) counter++;
        if (!validateText(editTextWorkshopAddress)) counter++;
        if (!validateText(editTextWorkshopFirstName)) counter++;
        if (!validateText(editTextWorkshopSurname)) counter++;
        if (!validatePhone()) counter++;

        return counter == 0;
    }


    private boolean validateText(EditText editText) {
        if(editText.length() == 0){
            editText.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!editText.getText().toString().matches(getContext().getString(R.string.lettersMatcher))){
            editText.setError(getContext().getString(R.string.youCanEnterHereOnlyLetters));
            return false;
        }
        return true;
    }

    private boolean validatePhone() {
        if(editTextWorkshopPhone.length() == 0){
            editTextWorkshopPhone.setError(getContext().getString(R.string.fillField));
            return false;
        }else if(!editTextWorkshopPhone.getText().toString().matches("([- 0-9]){7,15}")){
            editTextWorkshopPhone.setError(getContext().getString(R.string.youCanEnterHereOnlyLettersAndNumbers));
            return false;
        }
        return true;
    }

    private void getWorkshopDataFromDialogs() {
        tempTitle = editTextWorkshopTitle.getText().toString();
    }


    private void prepareWorkshopToAddToRealm() {
        final int workshopID = preferences.lastWorkshopID().get() + 1;
        preferences.lastWorkshopID().put(workshopID);

        final Workshop workshop = new Workshop(workshopID, tempTitle, tempFirstName, tempSurname, tempPhone, tempAddress);
        callingWorkshopFragment.notifyNewWorkshop(workshop);

        databaseConnector.addNewWorkshopToRealm(workshop);
    }

    private void setDialogOnClickListeners() {
        findViewById(R.id.buttonSaveWorkshop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkshop();
            }
        });


        findViewById(R.id.buttonCancelWorkshop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void clearData() {
        editTextWorkshopAddress.setText("");
        editTextWorkshopPhone.setText("");
        editTextWorkshopTitle.setText("");
        editTextWorkshopSurname.setText("");
        editTextWorkshopFirstName.setText("");
    }

    private void setupOnTextChangeListeners() {
        editTextWorkshopTitle.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempTitle = editTextWorkshopTitle.getText().toString();
                if(validateText(editTextWorkshopTitle)) editTextWorkshopTitle.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempTitle = editTextWorkshopTitle.getText().toString();
                if(validateText(editTextWorkshopTitle)) editTextWorkshopTitle.setError(null);
            }
        });

        editTextWorkshopFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tempFirstName = editTextWorkshopFirstName.getText().toString();
                if(validateText(editTextWorkshopFirstName)) editTextWorkshopFirstName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tempFirstName = editTextWorkshopFirstName.getText().toString();
                if(validateText(editTextWorkshopFirstName)) editTextWorkshopFirstName.setError(null);
            }
        });

        editTextWorkshopSurname.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempSurname = editTextWorkshopSurname.getText().toString();
                if(validateText(editTextWorkshopSurname)) editTextWorkshopSurname.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempSurname = editTextWorkshopSurname.getText().toString();
                if(validateText(editTextWorkshopSurname)) editTextWorkshopSurname.setError(null);
            }
        });

        editTextWorkshopPhone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempPhone = editTextWorkshopPhone.getText().toString();
                if (validatePhone()) editTextWorkshopPhone.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempPhone = editTextWorkshopPhone.getText().toString();
                if (validatePhone()) editTextWorkshopPhone.setError(null);
            }
        });


        editTextWorkshopAddress.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                tempAddress = editTextWorkshopAddress.getText().toString();
                if (validateText(editTextWorkshopAddress)) editTextWorkshopAddress.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempAddress = editTextWorkshopAddress.getText().toString();
                if (validateText(editTextWorkshopAddress)) editTextWorkshopAddress.setError(null);
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
        this.callingWorkshopFragment = (WorkshopFragment) callingFragment;
    }
}
