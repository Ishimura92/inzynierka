package com.example.luki.inzynierka.callbacks;

import com.example.luki.inzynierka.fragments.RefuelingFragment;
import com.example.luki.inzynierka.fragments.RepairFragment;
import com.example.luki.inzynierka.fragments.ServiceFragment;
import com.example.luki.inzynierka.models.Vehicle;

public interface MainActivityCallbacks {
    void changeToMainFragment(String fragmentTitle);

    void changeToRefuelingFragment(String fragmentTitle);

    void changeToRepairFragment(String fragmentTitle);

    void changeToServiceFragment(String fragmentTitle);

    void changeToWorkshopFragment(String fragmentTitle);

    void changeToSettingsFragment(String fragmentTitle);

    void changeToStatsFragment(String fragmentTitle);

    RefuelingFragment getRefuelingFragment();

    RepairFragment getRepairFragment();

    ServiceFragment getServiceFragment();

    Vehicle getCurrentVehicle();
}
