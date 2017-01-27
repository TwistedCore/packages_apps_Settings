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
package com.android.settings.twisted.statusbar;

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

import com.android.settings.preference.CustomSeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.MetricsProto.MetricsEvent;

public class StatusbarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String SHOW_TICKER = "status_bar_show_ticker";
    private static final String CAT_COLORS = "ticker_colors";
    private static final String TEXT_COLOR = "status_bar_ticker_text_color";
    private static final String ICON_COLOR ="status_bar_ticker_icon_color";
    private static final String STATUS_BAR_TICKER_FONT_STYLE = "status_bar_ticker_font_style";
    private static final String STATUS_BAR_TICKER_FONT_SIZE  = "status_bar_ticker_font_size";

    private SwitchPreference mEnableNC;
    private SwitchPreference mShowTicker;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mIconColor;
    private CustomSeekBarPreference mTickerFontSize;
    private ListPreference mTickerFontStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.twisted_statusbar);

        final PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        mEnableNC = (SwitchPreference) findPreference(STATUS_BAR_NOTIF_COUNT);
        mEnableNC.setOnPreferenceChangeListener(this);
        int EnableNC = Settings.System.getInt(getContentResolver(),
                STATUS_BAR_NOTIF_COUNT, 0);
        mEnableNC.setChecked(EnableNC != 0);

        int intColor;
        String hexColor;

        mShowTicker = (SwitchPreference) findPreference(SHOW_TICKER);
        mShowTicker.setChecked(Settings.System.getInt(resolver,
                "status_bar_show_ticker", 0) == 1);
        mShowTicker.setOnPreferenceChangeListener(this);

        mTextColor = (ColorPickerPreference) findPreference(TEXT_COLOR);
        mTextColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                "status_bar_ticker_text_color", 0xffffffff);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setNewPreviewColor(intColor);

        mIconColor = (ColorPickerPreference) findPreference(ICON_COLOR);
        mIconColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                "status_bar_ticker_icon_color", 0xffffffff);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setNewPreviewColor(intColor);

        mTickerFontSize = (CustomSeekBarPreference) findPreference(STATUS_BAR_TICKER_FONT_SIZE);
        mTickerFontSize.setValue(Settings.System.getInt(resolver,
                      Settings.System.STATUS_BAR_TICKER_FONT_SIZE, 14));
        mTickerFontSize.setOnPreferenceChangeListener(this);

        mTickerFontStyle = (ListPreference) findPreference(STATUS_BAR_TICKER_FONT_STYLE);
        mTickerFontStyle.setOnPreferenceChangeListener(this);
        mTickerFontStyle.setValue(Integer.toString(Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_TICKER_FONT_STYLE, 0)));
        mTickerFontStyle.setSummary(mTickerFontStyle.getEntry());
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
            ContentResolver resolver = getActivity().getContentResolver();
        if  (preference == mEnableNC) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, STATUS_BAR_NOTIF_COUNT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowTicker) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                   Settings.System.STATUS_BAR_SHOW_TICKER, value ? 1 : 0);
            return true;
        } else if (preference == mTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    "status_bar_ticker_text_color", intHex);
            return true;
        } else if (preference == mIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    "status_bar_ticker_icon_color", intHex);
            return true;
        } else if (preference == mTickerFontSize) {
             int width = ((Integer)newValue).intValue();
              Settings.System.putInt(resolver,
                      Settings.System.STATUS_BAR_TICKER_FONT_SIZE, width);
              return true;
        } else if (preference == mTickerFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mTickerFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_TICKER_FONT_STYLE, val);
            mTickerFontStyle.setSummary(mTickerFontStyle.getEntries()[index]);
            return true;
        }
        return false;
    }
}
