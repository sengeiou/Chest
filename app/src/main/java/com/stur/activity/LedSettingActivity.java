package com.stur.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import com.stur.chest.R;
import com.stur.lib.LedUtils;

/**
 * Created by guanxuejin on 2018/12/25 0025.
 */

public class LedSettingActivity extends PreferenceActivity {

    private SwitchPreference mChargeSwitchPref;
    private SwitchPreference mRedSwitchPref;
    private SwitchPreference mBlueSwitchPref;
    private SwitchPreference mGreenSwitchPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_led_setting);
        mChargeSwitchPref = (SwitchPreference)findPreference("Charge");
        mRedSwitchPref = (SwitchPreference)findPreference("Red");
        mBlueSwitchPref = (SwitchPreference)findPreference("Blue");
        mGreenSwitchPref = (SwitchPreference)findPreference("Green");

        initPreference();
    }

    private void initPreference() {
        mChargeSwitchPref.setChecked(false);
        mRedSwitchPref.setChecked(false);
        mBlueSwitchPref.setChecked(false);
        mGreenSwitchPref.setChecked(false);

        mChargeSwitchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                LedUtils.setLedChargeEnabled((Boolean) newValue);
                return true;
            }
        });
        mRedSwitchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                LedUtils.setLedRedEnabled((Boolean) newValue);
                return true;
            }
        });
        mBlueSwitchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                LedUtils.setLedBlueEnabled((Boolean) newValue);
                return true;
            }
        });
        mGreenSwitchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                LedUtils.setLedGreenEnabled((Boolean) newValue);
                return true;
            }
        });
    }
}
