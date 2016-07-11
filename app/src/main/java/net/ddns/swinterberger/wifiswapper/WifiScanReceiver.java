package net.ddns.swinterberger.wifiswapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcastreceiver for Wifi-Scan-Results. Notifies the Service, that there are new Results.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public final class WifiScanReceiver extends BroadcastReceiver {

    private WifiSwapService wifiSwapService;

    @Override
    public final void onReceive(final Context context, final Intent intent) {
        if (wifiSwapService != null) {
            wifiSwapService.callbackFromWifiScanReceiver();
        }
    }

    public final void setWifiSwapService(final WifiSwapService wifiSwapService) {
        this.wifiSwapService = wifiSwapService;
    }
}
