package net.ddns.swinterberger.wifiswapper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.eventhandler.MarginSeekbarEventhandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.TresholdSeekbarEventhandler;

public class MainActivity extends AppCompatActivity {

    private TextView debugInfos;
    private SeekBar tresholdSeekbar;
    private SeekBar marginSeekbar;
    private TextView tresholdValue;
    private TextView marginValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewElements();

    }

    private void setupViewElements() {
        debugInfos = (TextView) findViewById(R.id.tvDebugInfo);

        tresholdValue = (TextView) findViewById(R.id.tvThresoldValue);
        marginValue = (TextView) findViewById(R.id.tvMarginValue);

        tresholdSeekbar = (SeekBar) findViewById(R.id.seekBar_Treshold);
        tresholdSeekbar.setOnSeekBarChangeListener(new TresholdSeekbarEventhandler(this.tresholdValue));

        marginSeekbar = (SeekBar) findViewById(R.id.seekBar_Margin);
        marginSeekbar.setOnSeekBarChangeListener(new MarginSeekbarEventhandler(this.marginValue));
    }
}
