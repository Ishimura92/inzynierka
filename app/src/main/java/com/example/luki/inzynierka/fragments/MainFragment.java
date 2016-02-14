package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.dialogs.NewRefuelingDialog;
import com.example.luki.inzynierka.dialogs.NewRepairDialog;
import com.example.luki.inzynierka.dialogs.NewServiceDialog;
import com.example.luki.inzynierka.models.Notification;
import com.example.luki.inzynierka.models.Vehicle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment{

    @ViewById
    TextView textViewClosestNotificationDate, textViewClosestNotificationName;

    @ViewById
    LinearLayout layoutNewService, layoutNewRepair, layoutNewRefueling;

    private MainActivityCallbacks mainActivityCallbacks;
    private Realm realm;
    private Vehicle currentVehicle;
    private RefuelingFragment refuelingFragment;
    private RepairFragment repairFragment;
    private ServiceFragment serviceFragment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
        refuelingFragment = mainActivityCallbacks.getRefuelingFragment();
        repairFragment = mainActivityCallbacks.getRepairFragment();
        serviceFragment = mainActivityCallbacks.getServiceFragment();
    }

    @AfterViews
    void init(){
        realm = Realm.getInstance(getActivity());

        setClosestNotification();
    }

    @Click(R.id.layoutNewRefueling)
    void onRefuelingClick(){
        mainActivityCallbacks.changeToRefuelingFragment("Tankowania");
    }


    @Click(R.id.layoutNewRepair)
    void onRepairClick(){
        mainActivityCallbacks.changeToRepairFragment("Naprawy");
    }


    @Click(R.id.layoutNewService)
    void onServiceClick(){
        mainActivityCallbacks.changeToServiceFragment("Serwisy");
    }

    private void setClosestNotification() {
        realm.beginTransaction();
        RealmQuery<Notification> query = realm.where(Notification.class);
        RealmResults<Notification> notifications = query.findAll();
        realm.commitTransaction();

        if(notifications.size() != 0){
            final List<Notification> dateNotifications = new ArrayList<>();

            for(Notification notification : notifications){
                if(notification.isDateNotification())
                    dateNotifications.add(notification);
            }

            if (dateNotifications.size() > 0) {
                Collections.sort(dateNotifications, new Comparator<Notification>() {
                    @Override
                    public int compare(Notification lhs, Notification rhs) {
                        final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                        final DateTime lhsDate = dtf.parseDateTime(lhs.getDate());
                        final DateTime rhsDate = dtf.parseDateTime(rhs.getDate());
                        return rhsDate.compareTo(lhsDate);
                    }
                });

                final Notification closestNotification = dateNotifications.get(dateNotifications.size() - 1);

                textViewClosestNotificationDate.setVisibility(View.VISIBLE);
                textViewClosestNotificationName.setText(closestNotification.getName());
                textViewClosestNotificationDate.setText(closestNotification.getDate());

            } else {
                textViewClosestNotificationDate.setVisibility(View.GONE);
                textViewClosestNotificationName.setText("Najbliższe wydarzenie jest związane z kilometrami");
            }

        } else {
            textViewClosestNotificationDate.setVisibility(View.GONE);
            textViewClosestNotificationName.setText("Brak nadchodzących wydarzeń");
        }
    }
}
