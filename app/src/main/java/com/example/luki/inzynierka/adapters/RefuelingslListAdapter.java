package com.example.luki.inzynierka.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.VehicleChooser;
import com.example.luki.inzynierka.fragments.RefuelingHistoryFragment;
import com.example.luki.inzynierka.models.Refueling;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class RefuelingslListAdapter extends RecyclerView.Adapter<RefuelingslListAdapter.CustomViewHolder> {

    private final Context context;
    private final RefuelingHistoryFragment refuelingHistoryFragment;
    private List<Refueling> refuelings = new ArrayList<>();
    private Realm realm;

    public RefuelingslListAdapter(List<Refueling> refuelings, Context context, RefuelingHistoryFragment refuelingHistoryFragment) {
        this.refuelings = refuelings;
        this.context = context;
        this.refuelingHistoryFragment = refuelingHistoryFragment;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.refueling_list_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        final Refueling refueling = refuelings.get(position);

        customViewHolder.textViewCostPerLiter.setText(String.format("%.2f",
                refueling.getPrice() / refueling.getLiters()) + " zł/l");
        customViewHolder.textViewFuelType.setText(refueling.getType());
        customViewHolder.textViewRefuelingAmount.setText(String.valueOf(refueling.getLiters()) + " l");
        customViewHolder.textViewRefuelingCost.setText(String.valueOf(refueling.getPrice()) + " zł");
        customViewHolder.textViewRefuelingOdometer.setText(String.valueOf(refueling.getOdometer()) + " km");
        customViewHolder.textViewRefuelingDate.setText(refueling.getDate());

        if (position > 0) {
            final Refueling previousRefueling = refuelings.get(position - 1);
            customViewHolder.textViewRefuelingOdometerDifference.
                    setText("+ " + String.valueOf(refueling.getOdometer() - previousRefueling.getOdometer()) + " km");
        } else {
            customViewHolder.textViewRefuelingOdometerDifference.setText("b/d");
        }

        customViewHolder.refuelingItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Tankowanie")
                        .setMessage("Chcesz usunąć ten wpis?")
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refuelingHistoryFragment.deleteRefueling(refueling.getId());
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.car_placeholder)
                        .show();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return refuelings.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewRefuelingCost;
        protected TextView textViewCostPerLiter;
        protected TextView textViewRefuelingOdometer;
        protected TextView textViewRefuelingOdometerDifference;
        protected TextView textViewRefuelingAmount;
        protected TextView textViewFuelType;
        protected TextView textViewRefuelingDate;
        protected CardView refuelingItemLayout;

        public CustomViewHolder(View view) {
            super(view);
            this.refuelingItemLayout = (CardView) view.findViewById(R.id.refuelingItemLayout);
            this.textViewCostPerLiter = (TextView) view.findViewById(R.id.textViewCostPerLiter);
            this.textViewRefuelingDate = (TextView) view.findViewById(R.id.textViewRefuelingDate);
            this.textViewRefuelingCost = (TextView) view.findViewById(R.id.textViewRefuelingCost);
            this.textViewRefuelingOdometer = (TextView) view.findViewById(R.id.textViewRefuelingOdometer);
            this.textViewRefuelingOdometerDifference = (TextView) view.findViewById(R.id.textViewRefuelingOdometerDifference);
            this.textViewRefuelingAmount = (TextView) view.findViewById(R.id.textViewRefuelingAmount);
            this.textViewFuelType = (TextView) view.findViewById(R.id.textViewFuelType);
        }
    }

}
