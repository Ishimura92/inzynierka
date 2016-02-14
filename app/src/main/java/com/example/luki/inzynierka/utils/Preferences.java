package com.example.luki.inzynierka.utils;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface Preferences {

    @DefaultInt(0)
    int lastVehicleID();

    @DefaultInt(0)
    int lastRefuelingID();

    @DefaultInt(0)
    int lastPartID();

    @DefaultInt(0)
    int lastRepairID();

    @DefaultInt(0)
    int lastServiceID();

    @DefaultInt(0)
    int lastWorkshopID();

    @DefaultInt(0)
    int lastNotificationID();

    @DefaultBoolean(true)
    boolean areNotificationsTurned();
}