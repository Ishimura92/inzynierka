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
        View view = inflater.inflate(R.layout.refueling_list_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        final Repair repair = repairs.get(position);

        //cala obsluga

//        customViewHolder.repairItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                new AlertDialog.Builder(context)
//                        .setTitle(context.getString(R.string.refueling))
//                        .setMessage(context.getString(R.string.doYouWantToDeleteThisEntry))
//                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                repairHistoryFragment.deleteRepair(repair.getId());
//                            }
//                        })
//                        .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setIcon(R.drawable.car_placeholder)
//                        .show();
//                return false;
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return repairs.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public CustomViewHolder(View view) {
            super(view);
        }
    }

}
