package net.ddns.swinterberger.wifiswapper;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Background Service, who keeps running, when the Activity is closed. Uses the Broadcast-Receiver.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public class WifiSwapService extends Service {

    private WifiScanReceiver wifiScanReceiver = null;
    private SwapperServiceBinder swapperServiceBinder;

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiScanReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (swapperServiceBinder == null) {
            swapperServiceBinder = new SwapperServiceBinder();
        }
        return swapperServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        int threshold = intent.getIntExtra(getResources().getString(R.string.intentextrathreshold), 3);
        int margin = intent.getIntExtra(getResources().getString(R.string.intentextramargin), 3);

        // Register Broadcast Receiver
        if (wifiScanReceiver == null) {
            wifiScanReceiver = new WifiScanReceiver();
            wifiScanReceiver.setWifiSwapService(this);
            wifiScanReceiver.setThreshold(threshold);
            wifiScanReceiver.setMargin(margin);
        }
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        return START_REDELIVER_INTENT;
    }

    /**
     * If the Activity is open, this Method logs a Massage on the Debug-Info-Textfield.
     *
     * @param logMessage The Message to log.
     */
    public void logInformation(final String logMessage) {
        if (swapperServiceBinder.isMainActivityAvailable()) {
            swapperServiceBinder.getMainActivity().logMessage(logMessage);
        }
    }
}
