package com.intelligentSoftwares.Trichain.batterymanager;

import com.github.abara.library.batterystats.service.BatteryTimeService;

public class NotificationService extends BatteryTimeService {

    @Override
    protected void onCalculatingChargingTime() {
        //Called while charging and time to charge is being calculated.
    }

    @Override
    protected void onChargingTimePublish(int hours, int mins) {
        //hours and mins params indicates remaining time for full charge.
    }

    @Override
    protected void onDischargeTimePublish(int days, int hours, int mins) {
        //days, hours and mins params indicates remaining time for discharge.
    }

    @Override
    protected void onCalculatingDischargingTime() {
        //Called while discharging and time to discharge is being calculated.
    }

    @Override
    protected void onFullBattery() {
        //Called when device is charging and battery becomes full.
    }

    /**
     * Other default Android service methods can be overridden.
     */

}