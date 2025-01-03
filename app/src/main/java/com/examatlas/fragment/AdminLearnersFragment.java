package com.examatlas.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.examatlas.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;

public class AdminLearnersFragment extends Fragment {
    private BarChart barChart;
    ArrayList<BarEntry> entries;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_learner, container, false);
        barChart = view.findViewById(R.id.barChart);
        setupBarChart();

        return view;
    }
    private void setupBarChart() {
        entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 2f));
        entries.add(new BarEntry(1f, 4f));
        entries.add(new BarEntry(2f, 1f));
        entries.add(new BarEntry(3f, 6f));

        BarDataSet barDataSet = new BarDataSet(entries, "My Data");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barDataSet.setColor(getResources().getColor(R.color.seed));
        barDataSet.setValueTextColors(Collections.singletonList(getResources().getColor(R.color.mat_yellow)));
        barDataSet.setValueTextSize(10f);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);

        // Set up X-axis
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"January", "February", "March", "April"}));
        barChart.getXAxis().setGranularity(1f); // Show one label for each bar
        barChart.getXAxis().setLabelCount(entries.size(), false); // Fixed count of labels
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Ensure labels are at the bottom
        barChart.getXAxis().setDrawGridLines(false); // Disable vertical grid lines

        // Set up Y-axis
        barChart.getAxisLeft().setDrawGridLines(true); // Enable horizontal grid lines
        barChart.getAxisRight().setEnabled(false); // Disable right Y-axis

        barChart.invalidate(); // Refresh the chart
    }
}
