package net.ddns.swinterberger.wifiswapper;

/**
 * Created by Stefan on 07.07.2016.
 */
public interface SwapperServiceApi {

    void registerMainActivity(MainActivity mainActivity);

    boolean isMainActivityAvailable();

    MainActivity getMainActivity();

    void testService();
}
