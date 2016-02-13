package com.example.luki.inzynierka.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.models.Part;

import io.realm.RealmList;

public class PartsListAdapter extends ArrayAdapter<Part> {

    private RealmList<Part> parts;
    private int layoutResID;

    public PartsListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
    }

    public PartsListAdapter(Context context, int resource, RealmList<Part> items) {
        super(context, resource, items);
        this.parts = items;
        this.layoutResID = resource;
    }

@Override
public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        TextView textViewPartName, textViewPartPrice, textViewPartBrand;
        LinearLayout partItemLayout;

        if (view == null) {
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
            view = vi.inflate(layoutResID, null);
        }

        Part part = getItem(position);

        if (part != null) {
            textViewPartName = (TextView) view.findViewById(R.id.textViewPartName);
            textViewPartBrand = (TextView) view.findViewById(R.id.textViewPartBrand);
            textViewPartPrice = (TextView) view.findViewById(R.id.textViewPartPrice);
            partItemLayout = (LinearLayout) view.findViewById(R.id.partItemLayout);

            textViewPartName.setText(part.getName());
            textViewPartPrice.setText(part.getPrice() + "zł.");
            textViewPartBrand.setText(part.getBrand());

            textViewPartName.setSelected(true);
            textViewPartPrice.setSelected(true);
            textViewPartBrand.setSelected(true);

            partItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Część")
                            .setMessage(getContext().getString(R.string.doYouWantToDeleteThisEntry))
                            .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //todo delete part
                                }
                            })
                            .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
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

        return view;
    }
}

