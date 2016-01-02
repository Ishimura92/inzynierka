package com.example.luki.inzynierka.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.fragments.RefuelingHistoryFragment;
import com.example.luki.inzynierka.fragments.RepairHistoryFragment;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class RepairsListAdapter extends RecyclerView.Adapter<RepairsListAdapter.CustomViewHolder> {

    private final Context context;
    private final RepairHistoryFragment repairHistoryFragment;
    private List<Repair> repairs = new ArrayList<>();
    private Realm realm;

    public RepairsListAdapter(List<Repair> repairs, Context context, RepairHistoryFragment repairHistoryFragment) {
        this.repairs = repairs;
        this.context = context;
        this.repairHistoryFragment = repairHistoryFragment;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.repair_list_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int position) {
        final Repair repair = repairs.get(position);

        customViewHolder.textViewRepairTitle.setText(repair.getTitle());
        customViewHolder.textViewRepairCost.setText(String.valueOf(repair.getTotalCost()) + this.context.getText(R.string.zlotysShortcut));
        customViewHolder.textViewRepairDate.setText(repair.getDate());
        customViewHolder.textViewRepairOdometer.setText(String.valueOf(repair.getOdometer()) + this.context.getText(R.string.kilometersShortcut));
        customViewHolder.textViewRepairDescription.setText(repair.getDescription());

        customViewHolder.repairItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.repair))
                        .setMessage(context.getString(R.string.doYouWantToDeleteThisEntry))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                repairHistoryFragment.deleteRepair(repair.getId());
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

        customViewHolder.imageButtonExpandRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customViewHolder.expandedRepairLayout.getVisibility() == View.GONE) {
                    customViewHolder.imageButtonExpandRepair.setBackgroundResource(R.drawable.ic_arrow_drop_up_white_24dp);
                    customViewHolder.expandedRepairLayout.setVisibility(View.VISIBLE);
                }
                else{
                    customViewHolder.imageButtonExpandRepair.setBackgroundResource(R.drawable.ic_arrow_drop_down_white_24dp);
                    customViewHolder.expandedRepairLayout.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return repairs.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewRepairCost, textViewRepairDate, textViewRepairOdometer, textViewRepairTitle, textViewRepairDescription;
        protected CardView repairItemLayout;
        protected ImageButton imageButtonExpandRepair;
        protected LinearLayout expandedRepairLayout;

        public CustomViewHolder(View view) {
            super(view);
            textViewRepairCost = (TextView) view.findViewById(R.id.textViewRepairCost);
            textViewRepairDate = (TextView) view.findViewById(R.id.textViewRepairDate);
            textViewRepairOdometer = (TextView) view.findViewById(R.id.textViewRepairOdometer);
            textViewRepairTitle = (TextView) view.findViewById(R.id.textViewRepairTitle);
            textViewRepairDescription = (TextView) view.findViewById(R.id.textViewRepairDescription);
            repairItemLayout = (CardView) view.findViewById(R.id.repairItemLayout);
            imageButtonExpandRepair = (ImageButton) view.findViewById(R.id.imageButtonExpandRepair);
            expandedRepairLayout = (LinearLayout) view.findViewById(R.id.expandedRepairLayout);
        }
    }

}
