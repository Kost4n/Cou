package com.kost.cou.ui.reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProvider;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.kost.cou.R;
import com.kost.cou.base.DataBaseHelper;
import com.kost.cou.databinding.FragmentReportsBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private Cursor cursor;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;

    public ArrayList<String> dates = new ArrayList<>();
    public List<Entry> upPressure = new ArrayList<>();
    public List<Entry> downPressure = new ArrayList<>();
    public ArrayList<Integer> puls = new ArrayList<>();


    @SuppressLint("NewApi")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReportsViewModel reportsViewModel =
                new ViewModelProvider(this).get(ReportsViewModel.class);

        binding = FragmentReportsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dataBaseHelper = new DataBaseHelper(getActivity(), "records", null, 1);

        db = dataBaseHelper.getWritableDatabase();
        //Query();

        LineChart lineChartUp = binding.LineChartUp;
        LineChart lineChartDown = binding.LineChartDown;

        selectForGraph(LocalDate.now().toString(), 2);
        setOptions(lineChartUp,2, true);
        setOptions(lineChartDown,2, false);

        binding.timeButtonFor3Days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectForGraph(LocalDate.now().toString(), 2);
                setOptions(lineChartUp,2, true);
                setOptions(lineChartDown,2, false);
            }
        });

        binding.timeButtonForWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectForGraph(LocalDate.now().toString(), 6);
                setOptions(lineChartUp,6, true);
                setOptions(lineChartDown,6, false);
            }
        });

        binding.timeButtonFor2Week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectForGraph(LocalDate.now().toString(), 13);
                setOptions(lineChartUp,13, true);
                setOptions(lineChartDown,13, false);
            }
        });
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setOptions(LineChart lineChart, int minisDays, boolean isChartUp) {
        String label;

        if (isChartUp) {
            label = "Верхнее давление за ";
        } else {
            label = "Нижнее давление за ";
        }

        LineDataSet lineDataSet; // добавление данных на график
        if (isChartUp) {
            lineDataSet = new LineDataSet(upPressure, label + LocalDate.now().minusDays(minisDays) +
                    " - " + LocalDate.now());
        } else {
            lineDataSet = new LineDataSet(downPressure, label + LocalDate.now().minusDays(minisDays) +
                    " - " + LocalDate.now());
        }

        lineDataSet.setValueTextSize(12f);  // настройка красоты
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.GREEN);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleHoleRadius(1);
        lineDataSet.setValueTextColor(Color.DKGRAY);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                    return (int) entry.getY() + "";
            }
        });
        LineData lineData = new LineData(lineDataSet); // настройка красоты
        lineChart.setData(lineData);
        lineChart.getDescription().setTextSize(12);
        lineChart.setDrawMarkers(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        //        lineChart.getXAxis().setGranularityEnabled(true);
        //lineChart.getXAxis().setGranularity(1.0f);
        //lineChart.getXAxis().setLabelCount(lineDataSet.getEntryCount());
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
        xAxis.setAxisMinimum(-0.1f);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));  // Даты внизу

        if (isChartUp) {
            LimitLine limitLineUp = new LimitLine(135f); // линия высокого давления
            limitLineUp.setLineColor(Color.RED);
            limitLineUp.setLineWidth(2f);
            limitLineUp.setLabel("Верхняя граница давления 135");
            limitLineUp.setTextSize(6f);
            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.addLimitLine(limitLineUp);

            LimitLine limitLineDown = new LimitLine(90f); // линия нижнего давления
            limitLineDown.setLineColor(Color.RED);
            limitLineDown.setLineWidth(2f);
            limitLineDown.setLabel("Нижняя граница давления 90");
            limitLineDown.setTextSize(6f);
            yAxis.addLimitLine(limitLineDown);
        } else {
            LimitLine limitLineUp = new LimitLine(100f); // линия высокого давления
            limitLineUp.setLineColor(Color.RED);
            limitLineUp.setLineWidth(2f);
            limitLineUp.setLabel("Верхняя граница давления 100");
            limitLineUp.setTextSize(6f);
            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.addLimitLine(limitLineUp);

            LimitLine limitLineDown = new LimitLine(60f); // линия нижнего давления
            limitLineDown.setLineColor(Color.RED);
            limitLineDown.setLineWidth(2f);
            limitLineDown.setLabel("Нижняя граница давления 60");
            limitLineDown.setTextSize(6f);
            yAxis.addLimitLine(limitLineDown);
        }

        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setAxisLineWidth(2f);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setAxisLineWidth(2f);

        lineChart.setTouchEnabled(true);

        MyMarkerView myMarkerView = new MyMarkerView(getLayoutInflater().getContext(),
                R.layout.touch_graph_content);
        lineChart.setMarker(myMarkerView);
//        lineChart.setVisibleXRangeMaximum(30);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    public Integer getUpPressure(String date) {
        cursor = db.query("records", null, "date = ?", new String[]{date}, null, null, "date");
        if (cursor.moveToFirst()) {
             @SuppressLint("Range") int x = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_UP_PRES));
             return x;
        }
        return 0;
    }

    private ArrayList<Integer> getArrayUpPres(String date) {
        ArrayList<Integer> array = new ArrayList<>();
        cursor = db.query("records",null, "date = ?",
                new String[]{date},null,null,null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int up_pres_dp = cursor.getInt(cursor.getColumnIndex("up_pres"));
                array.add(up_pres_dp);
            }while(cursor.moveToNext());
        }
        return array;
    }

    private ArrayList<Integer> getArrayDownPres(String date) {
        ArrayList<Integer> array = new ArrayList<>();
        cursor = db.query("records",null, "date = ?",
                new String[]{date},null,null,null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int down_pres_dp = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_DOWN_PRES));
                array.add(down_pres_dp);
            }while(cursor.moveToNext());
        }
        return array;
    }

    private ArrayList<Integer> getArrayPuls(String date) {
        ArrayList<Integer> array = new ArrayList<>();
        cursor = db.query("records",null, "date = ?",
                new String[]{date},null,null,null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int puls = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_PULS));
                array.add(puls);
            }while(cursor.moveToNext());
        }
        return array;
    }

    private ArrayList<String> getArrayDates(String date) {
        ArrayList<String> array = new ArrayList<>();
        cursor = db.query("records",null, "date = ?",
                new String[]{date},null,null,null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") String dates= cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DATE));
                array.add(dates);
            }while(cursor.moveToNext());
        }
        return array;
    }
    public String getDate(String date) {
        cursor = db.query("records", null, "date = ?", new String[]{date}, null, null, "date");
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String x = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DATE));
            return x;
        }
        return "";
    }

    public Integer getDownPressure(String date) {
        cursor = db.query("records", null, "date = ?", new String[]{date}, null, null, "date");
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int x = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_DOWN_PRES));
            return x;
        }
        return 0;
    }

    private Integer getPuls(String date) {
        cursor = db.query("records", null, "date = ?", new String[]{date}, null, null, "date");
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int x = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_PULS));
            return x;
        }
        return 0;
    }

    @SuppressLint({"NewApi", "LocalSuppress"})
    public void selectForGraph(String dateEnd, int daysBefore) {
        ArrayList<String> localDates = new ArrayList<>();
        List<Entry> upPres = new ArrayList<>(),
                downPres = new ArrayList<>();
        ArrayList<Integer> localPuls = new ArrayList<>();
        LocalDate start = LocalDate.parse(dateEnd).minusDays(daysBefore);
        LocalDate end = LocalDate.parse(dateEnd);

        float i = 0.0F;
        while (!start.isAfter(end)) {
            Log.i(null, "--------------------------------------------------------------------------- " + start);
            if (getArrayUpPres(start.toString()).size() > 1) {
                ArrayList<String> localDt = getArrayDates(start.toString());
                ArrayList<Integer> localUp = getArrayUpPres(start.toString());
                ArrayList<Integer> localDown = getArrayDownPres(start.toString());
                ArrayList<Integer> locPls = getArrayPuls(start.toString());
                for (int j = 0; j < localUp.size(); j++) {
                    upPres.add(new Entry(i, localUp.get(j)));
                    downPres.add(new Entry(i, localDown.get(j)));
                    localDates.add(localDt.get(j));
                    localPuls.add(locPls.get(j));
                    i++;
                }
                start = start.plusDays(1);
            } else {
                if (getDate(start.toString()) != null && getDate(start.toString()) != "") {
                    localDates.add(getDate(start.toString()));
                    upPres.add(new Entry(i, getUpPressure(start.toString())));
                    downPres.add(new Entry(i, getDownPressure(start.toString())));
                    localPuls.add(getPuls(start.toString()));
                    i++;
                }
                start = start.plusDays(1);
            }
        }

        dates = localDates;
        upPressure = upPres;
        downPressure = downPres;
        puls = localPuls;
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