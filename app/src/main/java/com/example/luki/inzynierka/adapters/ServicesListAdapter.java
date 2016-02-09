package com.example.luki.inzynierka.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.models.Notification;
import com.example.luki.inzynierka.models.Service;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ServicesListAdapter extends RecyclerView.Adapter<ServicesListAdapter.CustomViewHolder> {

    private final Context context;
    private List<Service> services = new ArrayList<>();
    private Realm realm;

    public ServicesListAdapter(List<Service> services, Context context) {
        this.services = services;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.service_list_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int position) {
        final Service service = services.get(position);

        customViewHolder.textViewServiceTitle.setText(service.getServiceType());
        customViewHolder.textViewServiceCost.setText(String.valueOf(service.getPrice()) + this.context.getText(R.string.zlotysShortcut));
        customViewHolder.textViewServiceDate.setText(service.getDate());
        customViewHolder.textViewServiceOdometer.setText(String.valueOf(service.getOdometer()) + this.context.getText(R.string.kilometersShortcut));

        if(service.getNotification() != null){
            final Notification serviceNotification = service.getNotification();
            customViewHolder.layoutServiceNotification.setVisibility(View.VISIBLE);
            if(serviceNotification.isDateNotification()) {
                customViewHolder.textViewServiceNotification.setText(serviceNotification.getDate());
            } else {
                final float notificationKm = serviceNotification.getKilometers() + service.getOdometer();
                customViewHolder.textViewServiceNotification.setText(context.getString(R.string.at) + notificationKm + this.context.getText(R.string.kilometersShortcut));
            }
        } else {
            customViewHolder.layoutServiceNotification.setVisibility(View.GONE);
        }

        customViewHolder.serviceItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.service))
                        .setMessage(context.getString(R.string.doYouWantToDeleteThisEntry))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //todo delete service
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
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
        return services.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewServiceCost, textViewServiceDate, textViewServiceOdometer, textViewServiceNotification, textViewServiceTitle, textViewServiceDescription;
        protected CardView serviceItemLayout;
        protected LinearLayout layoutServiceNotification;

        public CustomViewHolder(View view) {
            super(view);
            textViewServiceCost = (TextView) view.findViewById(R.id.textViewServiceCost);
            textViewServiceDate = (TextView) view.findViewById(R.id.textViewServiceDate);
            textViewServiceOdometer = (TextView) view.findViewById(R.id.textViewServiceOdometer);
            textViewServiceTitle = (TextView) view.findViewById(R.id.textViewServiceTitle);
            textViewServiceNotification = (TextView) view.findViewById(R.id.textViewServiceNotification);
            serviceItemLayout = (CardView) view.findViewById(R.id.serviceItemLayout);
            layoutServiceNotification = (LinearLayout) view.findViewById(R.id.layoutServiceNotification);
        }
    }

}
