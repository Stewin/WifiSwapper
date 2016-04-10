package net.ddns.swinterberger.wifiswapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Stefan on 06.03.2016.
 */
public class WifiScanReceiver extends BroadcastReceiver {

    private WifiSwapService wifiSwapService;
    private int threshold, margin;

    private MainActivity mainActivity;

    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();
        ScanResult bestSignal = null;
        for (ScanResult result : results) {
            if (bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0) {
                bestSignal = result;
            }
        }

        String message = String.format("%s networks found. %s is the strongest.", results.size(), bestSignal.SSID);

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Log.i("Debug", "onReceive() message: " + message);
        Log.i("ScanReciever: ", "Current Threshold: " + this.threshold);
        Log.i("ScanReciever: ", "Current Margin: " + this.margin);

        Log.d("Debug", "onReceive() message: " + message);

        if (mainActivity != null) {
            mainActivity.appendDebugInfos("Test");
        }
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.wifiSwapService.scanResultsAvailable();
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public WifiSwapService getWifiSwapService() {
        return wifiSwapService;
    }

    public void setWifiSwapService(WifiSwapService wifiSwapService) {
        this.wifiSwapService = wifiSwapService;
    }
}
