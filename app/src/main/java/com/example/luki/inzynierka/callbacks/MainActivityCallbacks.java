package com.example.luki.inzynierka.callbacks;

import com.example.luki.inzynierka.models.Vehicle;

public interface MainActivityCallbacks {
    void changeToMainFragment(String fragmentTitle);

    void changeToRefuelingFragment(String fragmentTitle);

    void changeToRepairFragment(String fragmentTitle);

    Vehicle getCurrentVehicle();
}
