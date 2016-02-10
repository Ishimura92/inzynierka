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
import com.example.luki.inzynierka.models.Workshop;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class WorkshopsListAdapter extends RecyclerView.Adapter<WorkshopsListAdapter.CustomViewHolder> {

    private final Context context;
    private List<Workshop> workshops = new ArrayList<>();
    private Realm realm;

    public WorkshopsListAdapter(List<Workshop> workshops, Context context) {
        this.workshops = workshops;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.workshop_list_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int position) {
        final Workshop workshop = workshops.get(position);

        customViewHolder.textViewWorkshopTitle.setText(workshop.getName());
        customViewHolder.textViewWorkshopNameAndSurname.setText(workshop.getFirstName() + " " + workshop.getLastName());
        customViewHolder.textViewWorkshopAddress.setText(workshop.getAddress());
        customViewHolder.textViewWorkshopPhone.setText(workshop.getPhoneNumber());

        customViewHolder.workshopItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.workshop))
                        .setMessage(context.getString(R.string.doYouWantToDeleteThisEntry))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //todo delete workshop
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
        return workshops.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewWorkshopNameAndSurname, textViewWorkshopAddress, textViewWorkshopPhone, textViewWorkshopTitle;
        protected CardView workshopItemLayout;

        public CustomViewHolder(View view) {
            super(view);
            textViewWorkshopNameAndSurname = (TextView) view.findViewById(R.id.textViewWorkshopNameAndSurname);
            textViewWorkshopAddress = (TextView) view.findViewById(R.id.textViewWorkshopAddress);
            textViewWorkshopPhone = (TextView) view.findViewById(R.id.textViewWorkshopPhone);
            textViewWorkshopTitle = (TextView) view.findViewById(R.id.textViewWorkshopTitle);

            workshopItemLayout = (CardView) view.findViewById(R.id.workshopItemLayout);
        }
    }

}
