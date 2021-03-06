package com.example.luki.inzynierka.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luki.inzynierka.MainActivity;
import com.example.luki.inzynierka.VehicleChooser;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.utils.Preferences;
import com.example.luki.inzynierka.utils.Preferences_;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.CustomViewHolder> {

    private Context context;
    private List<Vehicle> vehicles = new ArrayList<>();
    private Realm realm;
    private Preferences_ preferences;

    public VehicleListAdapter(List<Vehicle> vehicles, Context context) {
        this.vehicles = vehicles;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vehicle_list_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        preferences = new Preferences_(context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        final Vehicle vehicle = vehicles.get(position);

        customViewHolder.textViewVehicleBrand.setText(vehicle.getBrand());
        customViewHolder.textViewVehicleModel.setText(vehicle.getModel());
        customViewHolder.textViewEngineType.setText(vehicle.getEngineType());
        customViewHolder.textViewEngineCapacity.setText(String.valueOf(vehicle.getEngineCapacity()));
        customViewHolder.textViewBodyType.setText(vehicle.getBodyType());

        customViewHolder.textViewVehicleBrand.setSelected(true);
        customViewHolder.textViewVehicleModel.setSelected(true);


        switch (vehicle.getBodyType()){
            case "Sedan":
                customViewHolder.imageViewVehicleIcon.setImageResource(R.drawable.ic_sedan_white);
                break;
            case "Kombi":
                customViewHolder.imageViewVehicleIcon.setImageResource(R.drawable.ic_kombi_white);
                break;
            case "Minivan/Van":
                customViewHolder.imageViewVehicleIcon.setImageResource(R.drawable.ic_van_white);
                break;
            case "Coupe/Kabrio":
                customViewHolder.imageViewVehicleIcon.setImageResource(R.drawable.ic_coupe_white);
                break;
            case "SUV":
                customViewHolder.imageViewVehicleIcon.setImageResource(R.drawable.ic_suv_white);
                break;
            case "Hatchback":
                customViewHolder.imageViewVehicleIcon.setImageResource(R.drawable.ic_hatchback_white);
                break;
        }

        customViewHolder.cardViewVehicleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.choosenVehicleID().put(vehicle.getId());
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("CHOSEN_VEHICLE_ID", vehicle.getId());
                context.startActivity(intent);
            }
        });

        customViewHolder.cardViewVehicleItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(vehicle.getBrand() + " " + vehicle.getModel())
                        .setMessage(context.getString(R.string.youCanEditOrEraseChoosenVehicle))
                        .setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((VehicleChooser) context).deleteVehicle(vehicle.getId());
                            }
                        })
                        .setNeutralButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setNegativeButton(context.getString(R.string.edit), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((VehicleChooser) context).editVehicle(vehicle.getId());
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
        return vehicles.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageViewVehicleIcon;
        protected TextView textViewBodyType;
        protected TextView textViewEngineCapacity;
        protected TextView textViewEngineType;
        protected TextView textViewVehicleBrand;
        protected TextView textViewVehicleModel;
        protected CardView cardViewVehicleItem;

        public CustomViewHolder(View view) {
            super(view);
            this.cardViewVehicleItem = (CardView) view.findViewById(R.id.vehicleItemLayout);
            this.imageViewVehicleIcon = (ImageView) view.findViewById(R.id.imageViewVehicleIcon);
            this.textViewVehicleBrand = (TextView) view.findViewById(R.id.textViewVehicleBrand);
            this.textViewVehicleModel = (TextView) view.findViewById(R.id.textViewVehicleModel);
            this.textViewBodyType = (TextView) view.findViewById(R.id.textViewBodyType);
            this.textViewEngineCapacity = (TextView) view.findViewById(R.id.textViewEngineCapacity);
            this.textViewEngineType = (TextView) view.findViewById(R.id.textViewEngineType);
        }
    }
}
