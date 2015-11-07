package com.example.luki.inzynierka.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luki.inzynierka.MainActivity;
import com.example.luki.inzynierka.Models.Vehicle;
import com.example.luki.inzynierka.R;

import java.util.ArrayList;
import java.util.List;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.CustomViewHolder> {

    private Context context;
    private List<Vehicle> vehicles = new ArrayList<>();

    public VehicleListAdapter(List<Vehicle> vehicles, Context context) {
        this.vehicles = vehicles;
        this.context = context;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vehicle_list_row, parent, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        final Vehicle vehicle = vehicles.get(position);

        customViewHolder.textViewVehicleTitle.setText(vehicle.getBrand() + " " + vehicle.getModel());
        customViewHolder.textViewVehicleDescription.setText(vehicle.getEngineType());
        customViewHolder.imageViewVehicleIcon.setImageResource(R.drawable.car_placeholder);
        //TODO dodać ustawianie ikonki samochodu na podstawe nadwozia


        customViewHolder.cardViewVehicleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("CHOSEN_VEHICLE_ID", vehicle.getId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageViewVehicleIcon;
        protected TextView textViewVehicleTitle;
        protected TextView textViewVehicleDescription;
        protected CardView cardViewVehicleItem;

        public CustomViewHolder(View view) {
            super(view);
            this.cardViewVehicleItem = (CardView) view.findViewById(R.id.vehicleItemLayout);
            this.imageViewVehicleIcon = (ImageView) view.findViewById(R.id.imageViewVehicleIcon);
            this.textViewVehicleTitle = (TextView) view.findViewById(R.id.textViewVehicleTitle);
            this.textViewVehicleDescription = (TextView) view.findViewById(R.id.textViewVehicleDescription);
        }
    }
}
