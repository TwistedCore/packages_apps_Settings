/*
 * Copyright (C) 2015-2016 The Dirty Unicorns Project
 * Copyright (C) 2016 The Pure Nexus Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.twisted;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.Vibrator;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.MetricsProto.MetricsEvent;

public class StatusBarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String KEY_SYSUI_QQS_COUNT = "sysui_qqs_count_key";

    private SwitchPreference mEnableNC;
    private ListPreference mSysuiQqsCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.statusbar_settings);

        final PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        mEnableNC = (SwitchPreference) findPreference(STATUS_BAR_NOTIF_COUNT);
        mEnableNC.setOnPreferenceChangeListener(this);
        int EnableNC = Settings.System.getInt(getContentResolver(),
                STATUS_BAR_NOTIF_COUNT, 0);
        mEnableNC.setChecked(EnableNC != 0);

        mSysuiQqsCount = (ListPreference) findPreference(KEY_SYSUI_QQS_COUNT);
        if (mSysuiQqsCount != null) {
           mSysuiQqsCount.setOnPreferenceChangeListener(this);
           int SysuiQqsCount = Settings.Secure.getInt(resolver,
                    Settings.Secure.QQS_COUNT, 5);
           mSysuiQqsCount.setValue(Integer.toString(SysuiQqsCount));
           mSysuiQqsCount.setSummary(mSysuiQqsCount.getEntry());
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.TWISTED;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if  (preference == mEnableNC) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), STATUS_BAR_NOTIF_COUNT,
                    value ? 1 : 0);
            return true;
       } else if (preference == mSysuiQqsCount) {
            String SysuiQqsCount = (String) newValue;
            int SysuiQqsCountValue = Integer.parseInt(SysuiQqsCount);
            Settings.Secure.putInt(resolver, Settings.Secure.QQS_COUNT, SysuiQqsCountValue);
            int SysuiQqsCountIndex = mSysuiQqsCount
                    .findIndexOfValue(SysuiQqsCount);
            mSysuiQqsCount
                    .setSummary(mSysuiQqsCount.getEntries()[SysuiQqsCountIndex]);
            return true;
        }
        return false;
    }
}
