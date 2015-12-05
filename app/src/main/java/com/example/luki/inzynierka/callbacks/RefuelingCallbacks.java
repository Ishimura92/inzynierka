package com.example.luki.inzynierka.callbacks;

import android.support.v4.app.Fragment;

import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Vehicle;

public interface RefuelingCallbacks {
    void notifyRefuelingDatasetChanged(Fragment historyFragment, Fragment summaryFragment, Refueling refueling);

    void notifyRefuelingDeleted();
}
