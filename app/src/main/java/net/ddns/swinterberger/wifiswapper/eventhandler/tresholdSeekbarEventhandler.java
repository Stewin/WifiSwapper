package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Stefan on 05.03.2016.
 */
public class TresholdSeekbarEventhandler implements SeekBar.OnSeekBarChangeListener {

    private TextView tresholdValue;

    public TresholdSeekbarEventhandler(final TextView tresholdValue) {
        this.tresholdValue = tresholdValue;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tresholdValue.setText(String.valueOf(seekBar.getProgress() / 10));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
