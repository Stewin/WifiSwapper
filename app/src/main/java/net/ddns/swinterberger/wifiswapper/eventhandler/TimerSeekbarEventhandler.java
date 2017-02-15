package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.MainActivity;
import net.ddns.swinterberger.wifiswapper.R;

/**
 * Created by Stefan on 15.02.2017.
 */
public final class TimerSeekbarEventhandler implements SeekBar.OnSeekBarChangeListener {

    private MainActivity mainActivity;
    private TextView timerValue;

    public TimerSeekbarEventhandler(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int seekBarValue = seekBar.getProgress();

        if (timerValue != null) {
            timerValue.setText(String.valueOf(seekBarValue));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Not needed
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int seekBarValue = seekBar.getProgress();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(mainActivity.getResources().getString(R.string.timerpreferencename), seekBarValue);
        editor.apply();

        mainActivity.timerSeekBarChanged();
    }

    public final void setTimerValue(TextView timerValue) {
        this.timerValue = timerValue;
    }
}
