package net.ddns.swinterberger.wifiswapper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.eventhandler.MarginSeekbarEventhandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.ThresholdSeekbarEventhandler;

public class MainActivity extends AppCompatActivity {

    private TextView debugInfos;
    private SeekBar thresholdSeekbar;
    private SeekBar marginSeekbar;
    private TextView tresholdValue;
    private TextView marginValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewElements();
        loadPreferences();
        startSwapperService();
    }

    private void setupViewElements() {
        debugInfos = (TextView) findViewById(R.id.tvDebugInfo);

        tresholdValue = (TextView) findViewById(R.id.tvThresoldValue);
        marginValue = (TextView) findViewById(R.id.tvMarginValue);

        thresholdSeekbar = (SeekBar) findViewById(R.id.seekBar_Treshold);
        ThresholdSeekbarEventhandler thresholdSeekbarEventhandler = new ThresholdSeekbarEventhandler(this);
        thresholdSeekbarEventhandler.setThresholdValue(this.tresholdValue);
        thresholdSeekbar.setOnSeekBarChangeListener(thresholdSeekbarEventhandler);

        marginSeekbar = (SeekBar) findViewById(R.id.seekBar_Margin);
        MarginSeekbarEventhandler marginSeekbarEventhandler = new MarginSeekbarEventhandler(this);
        marginSeekbarEventhandler.setMarginValue(this.marginValue);
        marginSeekbar.setOnSeekBarChangeListener(marginSeekbarEventhandler);
    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int currentMarginValue = preferences.getInt(getResources().getString(R.string.marginpreferencename), 0);
        int currentThresholdValue = preferences.getInt(getResources().getString(R.string.thresholdpreferencename), 0);

        this.marginSeekbar.setProgress(currentMarginValue);
        this.thresholdSeekbar.setProgress(currentThresholdValue);
    }

    private void startSwapperService() {
        Intent wifiSwapServiceIntent = new Intent(this, WifiSwapService.class);
        wifiSwapServiceIntent.putExtra(getResources().getString(R.string.intentextrathreshold), thresholdSeekbar.getProgress());
        wifiSwapServiceIntent.putExtra(getResources().getString(R.string.intentextramargin), marginSeekbar.getProgress());

        try {
            startService(wifiSwapServiceIntent);
            debugInfos.append("WifiSwapService gestartet");
        } catch (SecurityException secEx) {
            Log.e("WifiSwapperMain: ", "Error while Starting de WifiSwapperService: " + secEx.getMessage());
            debugInfos.append("Error while Starting de WifiSwapperService");
        }
    }
}
