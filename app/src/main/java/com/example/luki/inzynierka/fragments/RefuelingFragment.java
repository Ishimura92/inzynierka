package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.callbacks.RefuelingCallbacks;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

@EFragment(R.layout.fragment_refueling)
public class RefuelingFragment extends Fragment{

    @ViewById
    ViewPager refuelingPager;
    @ViewById
    TabLayout refuelingTabs;
    @Pref
    Preferences_ preferences;

    private DatePickerDialog.OnDateSetListener date;
    private MainActivityCallbacks mainActivityCallbacks;
    private RefuelingCallbacks refuelingCallbacks;
    private TextView textViewRefuelingDate;
    private LinearLayout refuelingDateLayout;
    private EditText editTextfuelQuantity, editTextfuelTotalCost, editTextodometerValue;
    private NewRefuelingDialog newRefuelingDialog;
    private Realm realm;
    private Vehicle currentVehicle;
    private Spinner spinnerFuelType;
    private Calendar myCalendar;

    private String[] spinnerFuelTypeItems = new String[]{"PB95", "PB98", "ON", "LPG"};
    private Fragment refuelingHistoryFragment = new RefuelingHistoryFragment_();
    private Fragment refuelingSummaryFragment = new RefuelingSummaryFragment_();
    private Fragment refuelingGraphsFragment = new RefuelingGraphsFragment_();
    private Float liters;
    private Float price;
    private int odometer;
    private String fuelType;
    private DateTime refuelingDate;
    private DateTimeFormatter formatter;
    private String tempPrice;
    private String tempLiters;
    private String tempOdometer;
    private ImageView imageViewRefuelingDate;

    @AfterViews
    void init(){
        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        setupViewPager(refuelingPager);
        refuelingTabs.setupWithViewPager(refuelingPager);
        setHasOptionsMenu(true);
        formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        setupDatepicker();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refueling_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_refueling:
                newRefuelingDialog = new NewRefuelingDialog(getContext());
                newRefuelingDialog.show();
                newRefuelingDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
        refuelingCallbacks = (RefuelingCallbacks) activity;
    }

    private void addRefuelingToRealm() {
        final int refuelingID = preferences.lastRefuelingID().get() + 1;
        preferences.lastRefuelingID().put(refuelingID);

        liters = Float.valueOf(tempLiters);
        price = Float.valueOf(tempPrice);
        odometer = Integer.valueOf(tempOdometer);

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
        String date = dtfOut.print(refuelingDate);

        final Refueling refueling = new Refueling(refuelingID, liters, price, odometer, date, fuelType);

        realm.beginTransaction();
        currentVehicle.getRefuelings().add(refueling);
        realm.commitTransaction();

        refuelingCallbacks.notifyRefuelingDatasetChanged(refuelingHistoryFragment, refuelingSummaryFragment, refueling);
    }

    private void saveRefueling(){
        clearAllErrors();
        getRefuelingDataFromDialogs();
        if(validate()){
            addRefuelingToRealm();
            Snackbar.make(refuelingTabs, R.string.refuelingSaved, Snackbar.LENGTH_SHORT).show();
            newRefuelingDialog.dismiss();
        }
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
            editTextfuelTotalCost.setError(getActivity().getString(R.string.fillField));
            return false;
        }else if(!tempPrice.matches(getActivity().getString(R.string.numberAndDotMatcher))){
            editTextfuelTotalCost.setError(getActivity().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
            return false;
        }
        return true;
    }

    private boolean validateLiters() {
        if(editTextfuelQuantity.length() == 0){
            editTextfuelQuantity.setError(getActivity().getString(R.string.fillField));
            return false;
        }else if(!tempLiters.matches(getActivity().getString(R.string.numberAndDotMatcher))){
            editTextfuelQuantity.setError(getActivity().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
            return false;
        }
        return true;
    }

    private boolean validateOdometer() {
        if(editTextodometerValue.length() == 0){
            editTextodometerValue.setError(getActivity().getString(R.string.fillField));
            return false;
        }else if(!tempOdometer.matches(getActivity().getString(R.string.numberAndDotMatcher))){
            editTextodometerValue.setError(getActivity().getString(R.string.youCanEnterHereOnlyNumbersOrDot));
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
        String myFormat = "dd/MM/yyyy"; //In which you need put here
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(refuelingHistoryFragment, getActivity().getString(R.string.history));
        adapter.addFragment(refuelingSummaryFragment, getActivity().getString(R.string.summary));
        adapter.addFragment(refuelingGraphsFragment, getActivity().getString(R.string.graphs));
        viewPager.setAdapter(adapter);
    }

    public Fragment getRefuelingSummaryFragment(){
        return refuelingSummaryFragment;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
            textViewRefuelingDate = (TextView) this.findViewById(R.id.textViewRefuelingDate);
            spinnerFuelType = (Spinner) this.findViewById(R.id.spinnerFuelType);
            imageViewRefuelingDate = (ImageView) this.findViewById(R.id.imageViewRefuelingDate);
            refuelingDateLayout = (LinearLayout) this.findViewById(R.id.refuelingDateLayout);

            textViewRefuelingDate.setText(refuelingDate.toString("dd/MM/yyyy"));

            setSpinner();
            setOnClickListeners();
            setupOnTextChangeListeners();
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
            final DisplayMetrics metrics = getResources().getDisplayMetrics();
            lp.width = (int) (metrics.widthPixels * DIALOG_WIDTH_TO_SCREEN_WIDTH_RATIO);
            lp.height = (int) (metrics.heightPixels * DIALOG_HEIGHT_TO_SCREEN_HEIGHT_RATIO);
            getWindow().setAttributes(lp);
        }
    }

}
