package com.kost.cou.ui.settings;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class SettingsViewModel extends ViewModel {

    public final MutableLiveData<String> morningAlarm;
    public final MutableLiveData<String> eveningAlarm;

    public SettingsViewModel() {
        morningAlarm = new MutableLiveData<>();
        eveningAlarm = new MutableLiveData<>();
    }
    public MutableLiveData<String> getEveningAlarm() {
        return eveningAlarm;
    }

    public MutableLiveData<String> getMorningAlarm() {
        return morningAlarm;
    }
}