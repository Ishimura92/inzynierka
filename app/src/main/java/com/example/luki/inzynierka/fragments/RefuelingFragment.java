package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;

public class RefuelingFragment extends Fragment{

    private MainActivityCallbacks mainActivityCallbacks;
    private Realm realm;
    private NewRefuelingDialog newRefuelingDialog;
    private View view;
    private EditText editTextfuelQuantity, editTextfuelTotalCost, editTextodometerValue;
    private Button buttonNewRefueling;
    private Spinner spinnerFuelType;
    private String[] spinnerFuelTypeItems = new String[]{"ON", "PB95", "PB98", "LPG"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_refueling, container, false);

        realm = Realm.getInstance(getActivity());
        initViews();
        initOnClickListeners();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
    }

    private void initViews(){
        buttonNewRefueling = (Button) view.findViewById(R.id.buttonNewRefueling);
    }

    private void initOnClickListeners(){
        buttonNewRefueling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRefuelingDialog = new NewRefuelingDialog(getContext());
                newRefuelingDialog.show();
            }
        });
    }

    private void saveRefueling(){

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

