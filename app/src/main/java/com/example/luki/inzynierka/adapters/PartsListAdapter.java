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
import com.example.luki.inzynierka.models.Part;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class PartsListAdapter extends RecyclerView.Adapter<PartsListAdapter.CustomViewHolder> {

    private final Context context;
    private RealmList<Part> parts = new RealmList<>();

    public PartsListAdapter(RealmList<Part> parts, Context context) {
        this.parts = parts;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.part_list_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int position) {
        final Part part = parts.get(position);

        customViewHolder.textViewPartName.setText(part.getName());
        customViewHolder.textViewPartPrice.setText(part.getPrice() + "zł.");
        customViewHolder.textViewPartBrand.setText(part.getBrand());

        customViewHolder.partItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Część")
                        .setMessage(context.getString(R.string.doYouWantToDeleteThisEntry))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //todo delete part
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
        return parts.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewPartName, textViewPartPrice, textViewPartBrand;
        protected CardView partItemLayout;

        public CustomViewHolder(View view) {
            super(view);
            textViewPartName = (TextView) view.findViewById(R.id.textViewPartName);
            textViewPartBrand = (TextView) view.findViewById(R.id.textViewPartBrand);
            textViewPartPrice = (TextView) view.findViewById(R.id.textViewPartPrice);

            partItemLayout = (CardView) view.findViewById(R.id.partItemLayout);
        }
    }

}
