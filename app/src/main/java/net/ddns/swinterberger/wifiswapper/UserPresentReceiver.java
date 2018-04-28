package net.ddns.swinterberger.wifiswapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Stefan on 09.03.2017.
 */
public final class UserPresentReceiver extends BroadcastReceiver {

    private WifiSwapService wifiSwapService;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (wifiSwapService != null) {
            wifiSwapService.callbackFromUserPresentReceiver();
        }
    }

    public final void setWifiSwapService(final WifiSwapService wifiSwapService) {
        this.wifiSwapService = wifiSwapService;
    }
}
