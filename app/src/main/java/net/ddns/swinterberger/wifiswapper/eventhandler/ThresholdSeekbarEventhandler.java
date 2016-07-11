package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.MainActivity;
import net.ddns.swinterberger.wifiswapper.R;

/**
 * Eventhandler for the Seekbar to set the Threshold.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class ThresholdSeekbarEventhandler implements SeekBar.OnSeekBarChangeListener {

    private MainActivity mainActivity;
    private TextView thresholdValue;

    /**
     * Constructor with MainActivity of the App.
     *
     * @param mainActivity MainActivity of the App.
     */
    public ThresholdSeekbarEventhandler(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public final void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        int seekBarValue = seekBar.getProgress();

        if (thresholdValue != null) {
            thresholdValue.setText(String.valueOf(seekBarValue));
        }
    }

    @Override
    public final void onStartTrackingTouch(SeekBar seekBar) {
        //Not needed
    }

    @Override
    public final void onStopTrackingTouch(SeekBar seekBar) {
        int seekBarValue = seekBar.getProgress();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.mainActivity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(mainActivity.getResources().getString(R.string.thresholdpreferencename), seekBarValue);
        editor.apply();

        mainActivity.thresholdSeekbarChanged();
    }

    public final void setThresholdValue(TextView thresholdValue) {
        this.thresholdValue = thresholdValue;
    }
}
