package com.example.luki.inzynierka.callbacks;

import android.support.v4.app.Fragment;

import com.example.luki.inzynierka.fragments.ServiceFragment;
import com.example.luki.inzynierka.models.Repair;

public interface ServiceCallbacks {
    void notifyServiceDatasetChanged(ServiceFragment serviceFragment);

    void notifyServiceDeleted();
}
