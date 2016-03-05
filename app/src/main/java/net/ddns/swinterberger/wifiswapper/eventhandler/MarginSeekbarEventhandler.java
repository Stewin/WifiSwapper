package net.ddns.swinterberger.wifiswapper.eventhandler;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Created by Stefan on 05.03.2016.
 */
public class MarginSeekbarEventhandler implements OnSeekBarChangeListener {

    private TextView marginValue;

    public MarginSeekbarEventhandler(TextView marginValue) {
        this.marginValue = marginValue;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        marginValue.setText(String.valueOf(seekBar.getProgress() / 10));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
