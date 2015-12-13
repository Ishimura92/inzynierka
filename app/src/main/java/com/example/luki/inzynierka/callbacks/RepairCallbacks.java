package com.example.luki.inzynierka.callbacks;

import android.support.v4.app.Fragment;

import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;

public interface RepairCallbacks {
    void notifyRepairDatasetChanged(Fragment historyFragment, Fragment summaryFragment, Repair repair);

    void notifyRepairgDeleted();
}
