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

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();
        ScanResult bestSignal = null;
        for (ScanResult result : results) {
            if (bestSignal == null
                    || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0)
                bestSignal = result;
        }

        String message = String.format("%s networks found. %s is the strongest.",
                results.size(), bestSignal.SSID);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        Log.d("Debug", "onReceive() message: " + message);
    }
}
