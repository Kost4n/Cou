package com.kost.cou.ui.reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.kost.cou.R;
import com.kost.cou.base.DataBaseHelper;
import com.kost.cou.databinding.FragmentReportsBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private Cursor cursor;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;

    public ArrayList<Integer> downPressure;
    public ArrayList<Integer> puls;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReportsViewModel reportsViewModel =
                new ViewModelProvider(this).get(ReportsViewModel.class);

        binding = FragmentReportsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dataBaseHelper = new DataBaseHelper(getActivity(), "records", null, 1);

        db = dataBaseHelper.getWritableDatabase();
        //Query();

        LineChart lineChart = binding.LineChart;

        LineDataSet lineDataSet = new LineDataSet(getArrayEnrtries(), "Давление за "); // добавление данных на график
        lineDataSet.setValueTextSize(12f);  // настройка красоты
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.GREEN);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleHoleRadius(1);
        //lineDataSet.setDrawHighlightIndicators(true);
        //lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setValueTextColor(Color.DKGRAY);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        downPressure = getArrayDownPressure();
        puls = getArrayPuls();
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return (int) entry.getY() + " / " + downPressure.get((int) entry.getX());   // Форматирует то что на точках пересечения
            }
        });



        LineData lineData = new LineData(lineDataSet); // настройка красоты
        lineChart.setData(lineData);


        lineChart.getDescription().setTextSize(12);
        lineChart.setDrawMarkers(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);
        lineChart.getXAxis().setLabelCount(lineDataSet.getEntryCount());
        lineChart.getAxisRight().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return "";
            }
        });

        XAxis xAxis = lineChart.getXAxis();         // настройка значений x внизу
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(10f);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(-0.1f);
        //xAxis.setAxisMaximum(5.1f);

        List<String> dates = getArrayDate();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));  // Даты внизу

        LimitLine limitLine = new LimitLine(135f); // линия высокого давления
        limitLine.setLineColor(Color.RED);
        limitLine.setLineWidth(2f);
        limitLine.setLabel("Верхняя граница давления 135");
        limitLine.setTextSize(6f);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.addLimitLine(limitLine);

        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setAxisLineWidth(2f);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setAxisLineWidth(2f);

        lineChart.setTouchEnabled(true);

        MyMarkerView myMarkerView = new MyMarkerView(getLayoutInflater().getContext(),
                R.layout.touch_graph_content);
        lineChart.setMarker(myMarkerView);
        lineChart.setVisibleXRangeMaximum(30);
//        CustomMarkerView customMarkerView = new CustomMarkerView(getLayoutInflater().getContext(),
//                R.layout.touch_graph_content);
//        lineChart.setMarker(customMarkerView);

        lineChart.invalidate();
        lineChart.notifyDataSetChanged();

        return root;
    }

    public List<Entry> getArrayEnrtries() {
        List<Entry> entryList = new ArrayList<>();
        cursor = db.query("records", null, null, null, null, null, "date");
        float i = 0.0F;
        if (cursor.moveToFirst()) {
            do {
                //@SuppressLint("Range") String date_db = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") int up_pres_dp = cursor.getInt(cursor.getColumnIndex("up_pres"));
                // @SuppressLint("Range") int dw_pres_dp = cursor.getInt(cursor.getColumnIndex("dw_pres"));
                Log.i(null, up_pres_dp + " - Значения давдени ---------------------------------------------------------------");
                entryList.add(new Entry(i, (float) up_pres_dp));
                i++;
            } while (cursor.moveToNext());
        }
        return entryList;
    }

    public List<String> getArrayDate() {
        List<String> arrayDate = new ArrayList<>();
        cursor = db.query("records", null, null, null, null, null, "date");
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String date_db = cursor.getString(cursor.getColumnIndex("date"));
                arrayDate.add(date_db);
                i++;
            } while (cursor.moveToNext());
        }
        return arrayDate;
    }

    public ArrayList<Integer> getArrayDownPressure() {
        ArrayList<Integer> arrayDate = new ArrayList<>();
        cursor = db.query("records", null, null, null, null, null, "date");
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int dw_pres = cursor.getInt(cursor.getColumnIndex("dw_pres"));
                arrayDate.add(dw_pres);
                i++;
            } while (cursor.moveToNext());
        }
        return arrayDate;
    }

    private ArrayList<Integer> getArrayPuls() {
        ArrayList<Integer> arrayDate = new ArrayList<>();
        cursor = db.query("records", null, null, null, null, null, "date");
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int puls = cursor.getInt(cursor.getColumnIndex("puls"));
                arrayDate.add(puls);
                i++;
            } while (cursor.moveToNext());
        }
        return arrayDate;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cursor.close();
        binding = null;
    }

    public class MyMarkerView extends MarkerView {

        private TextView tvContent;

        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            if (e instanceof CandleEntry) {

                CandleEntry ce = (CandleEntry) e;

                tvContent.setText("С.А.Д: " + Utils.formatNumber(ce.getHigh(), 0, true) +
                        "\nД.А.Д: " + downPressure.get((int) ce.getX()) +
                        "\nПульс: " + puls.get((int) ce.getX()));
            } else {

                tvContent.setText("С.А.Д " + Utils.formatNumber(e.getY(), 0, true) +
                        "\n Д.А.Д " + downPressure.get((int) e.getX()) +
                        "\n Пульс: " + puls.get((int) e.getX()));
            }

            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -(getHeight() + 20));
        }
    }

}