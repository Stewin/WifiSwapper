package net.ddns.swinterberger.wifiswapper;

/**
 * Interface for serve Methods of a bound Service.
 *
 * @author Stefan Winterberger
 * @version 0.1.0_Prototype
 */
public interface SwapperServiceApi {


    /**
     * Checks if a MainActivity is registered.
     *
     * @return true if a MainActivity is registered.
     */
    boolean isMainActivityAvailable();

    /**
     * Returns the registered MainActivity.
     *
     * @return MainActivity of the App.
     */
    MainActivity getMainActivity();

    /**
     * Set the MainActivity for Callback Methods.
     *
     * @param mainActivity MainActiviy of the App.
     */
    void setMainActivity(MainActivity mainActivity);

    /**
     * Get the Threshold.
     *
     * @return threshold
     */
    int getThreshold();

    /**
     * Set a new Threshold for the Service.
     *
     * @param threshold new Threshold
     */
    void setThreshold(int threshold);

    /**
     * Get the Margin.
     *
     * @return margin
     */
    int getMargin();

    /**
     * Set a new Margin for the Service.
     *
     * @param margin new Margin.
     */
    void setMargin(int margin);

    /**
     * Get the TimerInterval.
     *
     * @return timerInterval
     */
    int getTimerInterval();

    void setTimerInterval(int progress);

}
