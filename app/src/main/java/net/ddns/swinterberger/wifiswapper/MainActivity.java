package net.ddns.swinterberger.wifiswapper;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ddns.swinterberger.wifiswapper.eventhandler.MarginSeekbarEventhandler;
import net.ddns.swinterberger.wifiswapper.eventhandler.ThresholdSeekbarEventhandler;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String MARGIN_PREFERENCE_NAME = "MarginPreference";
    private final String THRESHOLD_PREFERENCE_NAME = "ThresholdPreference";
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
        thresholdSeekbarEventhandler.setTHRESOLD_PREFERENCE_NAME(this.THRESHOLD_PREFERENCE_NAME);

        marginSeekbar = (SeekBar) findViewById(R.id.seekBar_Margin);
        MarginSeekbarEventhandler marginSeekbarEventhandler = new MarginSeekbarEventhandler(this);
        marginSeekbarEventhandler.setMarginValue(this.marginValue);
        marginSeekbar.setOnSeekBarChangeListener(marginSeekbarEventhandler);
        marginSeekbarEventhandler.setMARGIN_PREFERENCE_NAME(this.MARGIN_PREFERENCE_NAME);
    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int currentMarginValue = preferences.getInt(this.MARGIN_PREFERENCE_NAME, 0);
        int currentThresholdValue = preferences.getInt(this.THRESHOLD_PREFERENCE_NAME, 0);

        this.marginSeekbar.setProgress(currentMarginValue);
        this.thresholdSeekbar.setProgress(currentThresholdValue);
    }

    private void startSwapperService() {
        // Setup WiFi
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Get WiFi status
        WifiInfo info = wifi.getConnectionInfo();
        debugInfos.append("\n\nWiFi Status: " + info.toString() + "\n");

        // List available networks
        List<WifiConfiguration> configs = wifi.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                debugInfos.append("\n\n" + config.toString());
            }
        }

        WifiScanReceiver receiver = null;

        // Register Broadcast Receiver
        if (receiver == null) {
            receiver = new WifiScanReceiver();
            receiver.setMainActivity(this);
        }

        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void appendDebugInfos(String string) {
        debugInfos.append(string + "\n");
    }
}
