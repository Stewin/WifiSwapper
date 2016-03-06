package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Stefan on 05.03.2016.
 */
public class ThresholdSeekbarEventhandler implements SeekBar.OnSeekBarChangeListener {

    private final Context context;
    private String THRESOLD_PREFERENCE_NAME;
    private TextView thresholdValue;

    public ThresholdSeekbarEventhandler(Context context) {
        this.context = context;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int seekBarValue = seekBar.getProgress();

        if (thresholdValue != null) {
            thresholdValue.setText(String.valueOf(seekBarValue / 10));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(this.THRESOLD_PREFERENCE_NAME, seekBarValue);
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

    public String getTHRESOLD_PREFERENCE_NAME() {
        return THRESOLD_PREFERENCE_NAME;
    }

    public void setTHRESOLD_PREFERENCE_NAME(String THRESOLD_PREFERENCE_NAME) {
        this.THRESOLD_PREFERENCE_NAME = THRESOLD_PREFERENCE_NAME;
    }
}
