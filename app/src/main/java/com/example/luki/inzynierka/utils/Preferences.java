package com.example.luki.inzynierka.utils;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface Preferences {

    @DefaultInt(0)
    int lastVehicleID();
}