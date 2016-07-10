package net.ddns.swinterberger.wifiswapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Broadcastreceiver for Wifi-Scan-Results. If there are better Access Points available, the AP changes.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public class WifiScanReceiver extends BroadcastReceiver {

    private static final int WLAN_MINIMAL_STRENGTH = -95; //Entspricht einem WLAN-Signal von zirka 1%
    private static final int WLAN_MAXIMUM_STRENGTH = -35; //Entspricht einem WLAN-Signal von zirka 100 %
    private WifiSwapService wifiSwapService;
    private int threshold;
    private int margin;
    private WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // Calculate current WiFi Signal Strength (Normalized between 0-10)
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int currentSignalLevel = normalizeSignalStrength(wifiInfo.getRssi());
        if (wifiSwapService != null) {
            wifiSwapService.logInformation("Current Signal Strength " + currentSignalLevel + "\n");
        }

        //Determine best Alternative
        int bestAlternativeSignalLevel = 0;
        ScanResult bestAlternative = null;
        if (currentSignalLevel <= threshold) {
            bestAlternative = getBestAlternative();
            bestAlternativeSignalLevel = normalizeSignalStrength(bestAlternative.level);
            wifiSwapService.logInformation("Best Alternative Strength " + bestAlternativeSignalLevel + "\n");
        }

        //If Difference between current Strength and best Alternative is more than the Margin, then Change
        if ((bestAlternativeSignalLevel - currentSignalLevel) >= margin) {

            //List available networks
            List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
            if (configs != null) {
                for (WifiConfiguration config : configs) {
                    if (config.SSID.contains(bestAlternative.SSID)) {
                        wifiManager.enableNetwork(config.networkId, true);
                        wifiManager.reconnect();

                    }
                }
            }
        }
    }

    private ScanResult getBestAlternative() {
        List<ScanResult> results = wifiManager.getScanResults();
        ScanResult bestSignal = null;
        for (ScanResult result : results) {
            if (bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0) {
                bestSignal = result;
            }
        }
        return bestSignal;
    }

    private int normalizeSignalStrength(int rssi) {
        float signalStrength = ((float) (rssi - WLAN_MINIMAL_STRENGTH) / (WLAN_MAXIMUM_STRENGTH - WLAN_MINIMAL_STRENGTH) * 10);
        return Math.abs((int) signalStrength);
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public void setWifiSwapService(WifiSwapService wifiSwapService) {
        this.wifiSwapService = wifiSwapService;
    }
}
