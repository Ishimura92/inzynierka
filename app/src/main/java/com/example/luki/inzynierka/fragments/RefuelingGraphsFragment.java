package com.example.luki.inzynierka.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.example.luki.inzynierka.R;
import com.example.luki.inzynierka.callbacks.MainActivityCallbacks;
import com.example.luki.inzynierka.callbacks.RefuelingCallbacks;
import com.example.luki.inzynierka.models.Refueling;
import com.example.luki.inzynierka.models.Vehicle;
import com.example.luki.inzynierka.utils.Preferences_;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


@EFragment(R.layout.fragment_refueling_graphs)
public class RefuelingGraphsFragment extends Fragment {

    @ViewById
    BarChart chart;

    @ViewById
    LineChart burningChart;

    @ViewById
    TextView textViewNoCharts;

    @Pref
    Preferences_ preferences;

    private Realm realm;
    private Vehicle currentVehicle;
    private MainActivityCallbacks mainActivityCallbacks;
    private RefuelingCallbacks refuelingCallbacks;
    private List<Refueling> refuelingList;

    @AfterViews
    void init(){
        realm = Realm.getInstance(getActivity());
        currentVehicle = mainActivityCallbacks.getCurrentVehicle();
        refuelingList = new ArrayList<>();

        getRefuelingListFromRealm();
        initChart();
        initBurningChart();
    }

    private void initBurningChart() {
        ArrayList<Entry> burningVals= new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        for(int i = 0; i < refuelingList.size(); i++){
            float previousOdometer = refuelingList.get(i).getOdometer();
            float odometer = refuelingList.get(i).getOdometer();
            float burning;
            if(i > 0) {
                previousOdometer = refuelingList.get(i - 1).getOdometer();
            } else {
                previousOdometer = currentVehicle.getOdometer();
            }

            xVals.add(String.valueOf(odometer + "km"));
            burning = (refuelingList.get(i).getLiters()/(odometer - previousOdometer)) * 100;

            if(burning > 100 || burning < 0)
                burning = 0;

            Entry burningEntry = new Entry(burning, i);

            burningVals.add(burningEntry);
        }

        LineDataSet burningLineDataSet = new LineDataSet(burningVals, "Spalanie");

        burningLineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        burningLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(burningLineDataSet);

        LineData data = new LineData(xVals, dataSets);
        burningChart.setData(data);
        burningChart.invalidate(); // refresh
    }

    private void initChart() {
        ArrayList<BarEntry> literVals= new ArrayList<>();
        ArrayList<BarEntry> priceVals= new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        for(int i = 0; i < refuelingList.size(); i++){
            BarEntry literEntry = new BarEntry(refuelingList.get(i).getLiters(), i);
            xVals.add(String.valueOf(refuelingList.get(i).getOdometer()) + "km");
            literVals.add(literEntry);

            BarEntry priceEntry = new BarEntry(refuelingList.get(i).getPrice(), i);
            priceVals.add(priceEntry);
        }

        BarDataSet litersBarDataSet = new BarDataSet(literVals, "Litry");
        BarDataSet priceBarDataSet = new BarDataSet(priceVals, "Cena");

        litersBarDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        priceBarDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        litersBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        priceBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(litersBarDataSet);
        dataSets.add(priceBarDataSet);

        BarData data = new BarData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityCallbacks = (MainActivityCallbacks) activity;
        refuelingCallbacks = (RefuelingCallbacks) activity;
    }

    public void showBurningChart(){
        chart.setVisibility(View.GONE);
        burningChart.setVisibility(View.VISIBLE);
    }

    public void showPriceChart(){
        burningChart.setVisibility(View.GONE);
        chart.setVisibility(View.VISIBLE);
    }

    private void getRefuelingListFromRealm() {
        refuelingList.clear();
        realm.beginTransaction();
        final RealmQuery<Vehicle> query = realm.where(Vehicle.class).equalTo("id", currentVehicle.getId());
        final RealmResults<Vehicle> results = query.findAll();
        realm.commitTransaction();

        for (Refueling refueling : results.first().getRefuelings()) {
            refuelingList.add(refueling);
        }

        if (!refuelingList.isEmpty()) {
            textViewNoCharts.setVisibility(View.GONE);
        }
    }

}
