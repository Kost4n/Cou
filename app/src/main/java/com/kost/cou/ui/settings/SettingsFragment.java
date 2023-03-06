package com.kost.cou.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.kost.cou.MyReceiver;
import com.kost.cou.SharedPref;
import com.kost.cou.databinding.FragmentSettingsBinding;
import org.jetbrains.annotations.NotNull;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private AlarmManager alarmManager;
    private String timeMorning = "";
    private String timeEvening = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        root.setSaveEnabled(true);

        if (SharedPref.getInstance(getLayoutInflater().getContext()).getMorning() != null){
            binding.viewMorningTimeAlarm.setText(SharedPref.getInstance(getLayoutInflater().getContext())
                    .getMorning());
        } else{
            binding.viewMorningTimeAlarm.setText("new text");
        }
        if (SharedPref.getInstance(getLayoutInflater().getContext()).getEvening() != null){
            binding.viewEveningTimeAlarm.setText(SharedPref.getInstance(getLayoutInflater().getContext())
                    .getEvening());
        } else{
            binding.viewEveningTimeAlarm.setText("new text");
        }

//        SharedPref.init(getActivity().getApplicationContext());

        loadText();
//        SharedPreferences prefs = getActivity().getApplicationContext()
//                .getSharedPreferences("Preferences", Context.MODE_PRIVATE);
//        if (prefs.contains("timeMorning")) {
//            settingsViewModel.
////            binding.viewMorningTimeAlarm.setText(prefs.getString("timeMorning", ""));
//        }
//        if (prefs.contains("timeEvening")) {
////            binding.viewEveningTimeAlarm.setText(prefs.getString("timeEvening", ""));
//        }


        Intent intent = new Intent(getActivity().getApplicationContext(), MyReceiver.class);
        PendingIntent morningPending = PendingIntent.getBroadcast(getActivity().getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent eveningPending = PendingIntent.getBroadcast(getActivity().getApplicationContext(),
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        binding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setTitleText("Выберите время для буильника")
                        .setHour(date.getHours())
                        .setMinute(date.getMinutes())
                        .build();


                materialTimePicker.addOnPositiveButtonClickListener(view -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());
                    calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());

                    alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    if (calendar.get(Calendar.HOUR_OF_DAY) <= 12) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, morningPending);
                        timeMorning = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                        binding.viewMorningTimeAlarm.setText(timeMorning);
                    }

                    if (calendar.get(Calendar.HOUR_OF_DAY) <= 24 && calendar.get(Calendar.HOUR_OF_DAY) > 12){
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, eveningPending);
                        timeEvening = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                        binding.viewEveningTimeAlarm.setText(timeEvening);
                    }

                    Toast.makeText(getLayoutInflater().getContext(), "Будильник установлен на "
                            + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
                });

                materialTimePicker.show(getActivity().getSupportFragmentManager(), "tag_picker");
            }
        });

        binding.viewMorningTimeAlarm.setOnClickListener(v -> {
            morningPending.cancel();
            //alarmManager.cancel(morningPending);
            Toast.makeText(getLayoutInflater().getContext(), "Будильник на утро удалён", Toast.LENGTH_SHORT).show();
            binding.viewMorningTimeAlarm.setText("Нет будильника.");
            timeMorning = "Нет будильника.";
        });

        binding.viewEveningTimeAlarm.setOnClickListener(v ->  {
            eveningPending.cancel();
            //alarmManager.cancel(eveningPending);
            Toast.makeText(getLayoutInflater().getContext(), "Будильник на вечер удалён", Toast.LENGTH_SHORT).show();
            binding.viewEveningTimeAlarm.setText("Нет будильника.");
            timeEvening = "Нет будильника.";

        });

        return root;
    }

    void saveText() {
//        SharedPref.write(SharedPref.TIME_MORNING, timeMorning);
//        SharedPref.write(SharedPref.TIME_EVENING, timeEvening);
    }



    void loadText() {
////        String morning = SharedPref.read(SharedPref.TIME_MORNING, "");
////        String evening = SharedPref.read(SharedPref.TIME_EVENING, "");
//        binding.viewMorningTimeAlarm.setText(morning);
//        binding.viewEveningTimeAlarm.setText(evening);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPref.getInstance(getLayoutInflater().getContext()).setMorning(timeMorning);
        SharedPref.getInstance(getLayoutInflater().getContext()).setEvening(timeEvening);

//        saveText();
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPref.getInstance(getLayoutInflater().getContext()).setMorning(timeMorning);
        SharedPref.getInstance(getLayoutInflater().getContext()).setEvening(timeEvening);

//        saveText();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}