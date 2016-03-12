package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.R;

/**
 * Created by Stefan on 05.03.2016.
 */
public class ThresholdSeekbarEventhandler implements SeekBar.OnSeekBarChangeListener {

    private final Context context;
    private TextView thresholdValue;

    public ThresholdSeekbarEventhandler(Context context) {
        this.context = context;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int seekBarValue = seekBar.getProgress();

        if (thresholdValue != null) {
            thresholdValue.setText(String.valueOf(seekBarValue));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getResources().getString(R.string.thresholdpreferencename), seekBarValue);
        editor.apply();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public TextView getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(TextView thresholdValue) {
        this.thresholdValue = thresholdValue;
    }
}
