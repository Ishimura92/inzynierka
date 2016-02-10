package com.example.luki.inzynierka.databaseUtils;

import com.example.luki.inzynierka.callbacks.RefuelingCallbacks;
import com.example.luki.inzynierka.callbacks.RepairCallbacks;
import com.example.luki.inzynierka.fragments.RefuelingHistoryFragment;
import com.example.luki.inzynierka.fragments.RefuelingSummaryFragment;
import com.example.luki.inzynierka.fragments.RepairHistoryFragment;
import com.example.luki.inzynierka.fragments.RepairSummaryFragment;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Repair;
import com.example.luki.inzynierka.models.Service;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.models.Workshop;

import org.androidannotations.annotations.EBean;

import io.realm.Realm;

@EBean(scope = EBean.Scope.Singleton)
public class DatabaseConnector {

    private Realm realm;
    private Vehicle currentVehicle;

    private RefuelingCallbacks refuelingCallbacks;
    private RepairCallbacks repairCallbacks;

    private RefuelingHistoryFragment refuelingHistoryFragment;
    private RefuelingSummaryFragment refuelingSummaryFragment;

    private RepairSummaryFragment repairSummaryFragment;
    private RepairHistoryFragment repairHistoryFragment;


    public void setRepairCallbacks(RepairCallbacks repairCallbacks) {
        this.repairCallbacks = repairCallbacks;
    }

    public void setRepairSummaryFragment(RepairSummaryFragment repairSummaryFragment) {
        this.repairSummaryFragment = repairSummaryFragment;
    }

    public void setRepairHistoryFragment(RepairHistoryFragment repairHistoryFragment) {
        this.repairHistoryFragment = repairHistoryFragment;
    }

    public void setRefuelingCallbacks(RefuelingCallbacks refuelingCallbacks) {
        this.refuelingCallbacks = refuelingCallbacks;
    }

    public void setRefuelingHistoryFragment(RefuelingHistoryFragment refuelingHistoryFragment) {
        this.refuelingHistoryFragment = refuelingHistoryFragment;
    }

    public void setRefuelingSummaryFragment(RefuelingSummaryFragment refuelingSummaryFragment) {
        this.refuelingSummaryFragment = refuelingSummaryFragment;
    }

    public void setCurrentVehicle(Vehicle currentVehicle) {
        this.currentVehicle = currentVehicle;
    }

    public void setDatabaseConnectorRealm(Realm realm) {
        this.realm = realm;
    }

    public void addNewRefuelingToRealm(Refueling refueling) {
        realm.beginTransaction();
        currentVehicle.getRefuelings().add(refueling);
        realm.commitTransaction();

        refuelingCallbacks.notifyRefuelingDatasetChanged(refuelingHistoryFragment, refuelingSummaryFragment, refueling);
    }

    public void addNewServiceToRealm(Service service) {
        realm.beginTransaction();
        currentVehicle.getServices().add(service);
        realm.commitTransaction();
    }

    public void addNewWorkshopToRealm(Workshop workshop) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(workshop);
        realm.commitTransaction();
    }

    public void addNewRepairToRealm(Repair repair) {
        realm.beginTransaction();
        currentVehicle.getRepairs().add(repair);
        realm.commitTransaction();

        repairCallbacks.notifyRepairDatasetChanged(repairHistoryFragment, repairSummaryFragment, repair);
    }
}
